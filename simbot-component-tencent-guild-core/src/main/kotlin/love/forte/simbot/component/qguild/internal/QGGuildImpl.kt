/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.qguild.QGChannel
import love.forte.simbot.component.qguild.QGGuild
import love.forte.simbot.component.qguild.QQGuildInitException
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.*
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.apipermission.hasAuth
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.*
import love.forte.simbot.toTimestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.utils.item.effectedFlowItems
import love.forte.simbot.utils.item.flowItems
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.Guild as QGSourceGuild


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildImpl private constructor(
    private val baseBot: QGBotImpl,
    override val source: QGSourceGuild,
    private val job: CompletableJob,
    override val coroutineContext: CoroutineContext,
) : QGGuild {


    internal fun update(guild: QGSourceGuild): QGGuildImpl {
        // copy job and context.
        return QGGuildImpl(
            baseBot, guild, job, coroutineContext,
        ).also {
            // copy internal info
            it.permissions = permissions
            it.internalMembers = internalMembers
            it.internalChannels = internalChannels
            // caches
            it.internalChannelInfoCache = internalChannelInfoCache
            it.internalMemberInfoCache = internalMemberInfoCache
            // skip init
            it.init.set(true)
        }
    }

    @Volatile
    override lateinit var permissions: ApiPermissions

    private suspend fun initPermissions(): ApiPermissions {
        return try {
            GetApiPermissionListApi.create(source.id).requestBy(baseBot)
        } catch (apiEx: QQGuildApiException) {
            if (apiEx.isUnauthorized) {
                logger.warn(
                    "Unable to get guild(id={}, name={}) API access because of '{}' ({}) , access will be treated as empty",
                    source.id,
                    name,
                    apiEx.description,
                    apiEx.info
                )
                if (logger.isDebugEnabled) {
                    logger.debug(
                        "Unable to get guild(id={}, name={}) API access because of '{}' ({}) , access will be treated as empty",
                        source.id,
                        name,
                        apiEx.description,
                        apiEx.info,
                        apiEx.copyCurrent()
                    )
                }
                ApiPermissions.EMPTY
            } else {
                throw apiEx.copyCurrent()
            }
        }.also {
            this.permissions = it
        }
    }

    private val permissionsRefreshLock = Mutex()

    override suspend fun refreshPermissions(evaluationInternalCache: Boolean): ApiPermissions =
        permissionsRefreshLock.withLock {
            val newPermissions = initPermissions()

            if (evaluationInternalCache) {
                if (internalMembers == null) {
                    trySyncMemberList()
                }
                if (internalChannels == null) {
                    trySyncChannels()
                }
            }

            newPermissions
        }

    override val id: ID = source.id.ID
    override val ownerId: ID = source.ownerId.ID

    override val maximumChannel: Int get() = -1
    override val createTime: Timestamp get() = source.joinedAt.toTimestamp()
    override val currentMember: Int get() = source.memberCount
    override val description: String get() = source.description
    override val icon: String get() = source.icon
    override val maximumMember: Int get() = source.maxMembers
    override val name: String get() = source.name

    private data class CacheData<V>(val value: V, val time: Long)


    //region channel cache
    /**
     * 内部 channel 缓存器，用于减缓API的调用。
     * 当前bot订阅的guild相关事件时可用。
     */
    private var internalChannelInfoCache: ConcurrentHashMap<String, CacheData<SimpleChannel>>? = null

    /**
     * 当 [internalChannelInfoCache] 可用时优先使用缓存。
     */
    internal suspend fun getChannelWithCache(id: String): SimpleChannel {
        suspend fun get() = GetChannelApi.create(id).requestBy(baseBot)
        val cache = internalChannelInfoCache ?: return get()

        return cache[id]?.value ?: kotlin.run {
            get().also(::updateChannelCache)
        }
    }

    internal fun removeChannelCache(id: String): SimpleChannel? {
        return internalChannelInfoCache?.remove(id)?.value
    }

    private fun updateChannelCache(channel: SimpleChannel): SimpleChannel? {
        // no update category
        if (channel.type.isCategory) return null

        val cache = internalChannelInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.compute(channel.id) { _, curr ->
            if (curr == null) {
                CacheData(channel, now)
            } else {
                if (curr.time > now) curr else CacheData(channel, now)
            }
        }?.value
    }

    private fun updateChannelCache(channel: EventChannel): SimpleChannel? {
        // no update category
        // 不更新B
        if (channel.type.isCategory) return null

        val cache = internalChannelInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(channel.id) { _, curr ->
            if (curr.time > now) curr else {
                CacheData(
                    curr.value.copy(
                        guildId = channel.guildId,
                        name = channel.name,
                        type = channel.type,
                        subType = channel.subType,
                        ownerId = channel.ownerId,
                    ), now
                )
            }
        }?.value
    }
    //endregion

    //region member cache
    /*
        Member跟其他人不太一样，它是大概率不存在的东西
     */
    private var internalMemberInfoCache: ConcurrentHashMap<String, CacheData<SimpleMember>>? = null

    /**
     * 当 [internalMemberInfoCache] 可用时优先使用缓存。
     */
    private suspend fun getMemberWithCache(id: String): SimpleMember {
        suspend fun get() = GetMemberApi.create(source.id, id).requestBy(baseBot)
        val cache = internalMemberInfoCache ?: return get()

        return cache[id]?.value ?: kotlin.run {
            get().also(::updateMemberCache)
        }
    }

    private fun removeMemberCache(id: String): SimpleMember? {
        return internalMemberInfoCache?.remove(id)?.value
    }

    private fun updateMemberCache(member: SimpleMember): SimpleMember? {
        val cache = internalMemberInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(member.user.id) { _, curr ->
            if (curr.time > now) curr else CacheData(member, now)
        }?.value
    }

    internal fun updateMemberCache(member: EventMember): SimpleMember? {
        val cache = internalMemberInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(member.user.id) { _, curr ->
            if (curr.time > now) curr else {
                CacheData(
                    curr.value.copy(
                        user = member.user,
                        nick = member.nick,
                        roles = member.roles,
                        joinedAt = if (member.joinedAt != Instant.EPOCH) member.joinedAt else curr.value.joinedAt,
                    ), now
                )
            }
        }?.value
    }
    //endregion

    private var internalMembers: ConcurrentHashMap<String, QGMemberImpl>? = null
    private var internalChannels: ConcurrentHashMap<String, QGChannelImpl>? = null

    /*
     * 构造的时候即初始化，如果为null则大概率说明权限不足。
     * 但是不管
     */
//    private var ownerInternal: QGMemberImpl? = null

    private val hasMemberListApiPermission get() = permissions hasAuth GetGuildMemberListApi
    private val hasChannelListApiPermission get() = permissions hasAuth GetGuildChannelListApi

    override lateinit var bot: QGGuildBotImpl
        private set

    override fun toString(): String {
        return "QGGuildImpl(id=$id, name=$name, bot=$baseBot, guild=$source)"
    }

    override suspend fun owner(): QGMemberImpl {
        return member(ownerId) ?: throw NoSuchElementException("owner(id=$ownerId)")
    }

    private val init = AtomicBoolean(false)

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     *
     * @param onlyFirst 如果为 true, 则只有当从未初始化过的时候才会进行初始化
     *
     * @throws QQGuildInitException 初始化过程出现预期外异常
     * @throws QQGuildApiException 初始化过程出现预期外API异常
     */
    internal suspend fun initData(onlyFirst: Boolean = false) {
        if (!onlyFirst or init.compareAndSet(false, true)) {
            initPermissions()
            initCache()
            syncData()
        }
    }

    private fun initCache() {
        if (EventIntents.Guilds.intents in baseBot.source.configuration.intents) {
            internalChannelInfoCache = ConcurrentHashMap()
        }
        if (EventIntents.GuildMembers.intents in baseBot.source.configuration.intents) {
            internalMemberInfoCache = ConcurrentHashMap()
        }
    }

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [ownerInternal] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     *
     * @throws QQGuildInitException
     */
    private suspend fun syncData() {
        kotlin.runCatching { syncMembers() }.onFailure { e ->
            throw QQGuildInitException(
                "Sync members data for guild(id=${source.id}, name=$name) failed: ${e.localizedMessage}",
                e
            )
        }

        kotlin.runCatching { trySyncChannels() }.onFailure { e ->
            throw QQGuildInitException(
                "Sync channels data for guild(id=${source.id}, name=$name) failed: ${e.localizedMessage}",
                e
            )
        }
    }

    override val members: Items<QGMemberImpl>
        get() = internalMembers?.values?.asItems()
            ?: (if (hasMemberListApiPermission) queryMembers() else emptyItems())

    @OptIn(FlowPreview::class)
    private fun queryMembers(): Items<QGMemberImpl> = flowItems { (limit, offset, batch) ->
        // 批次
        val batchLimit = batch.takeIf { it > 0 } ?: GetGuildMemberListApi.MAX_LIMIT
        val flow =
            GetGuildMemberListApi.createFlow(source.id, batchLimit) { requestBy(baseBot) }
                .flatMapConcat { it.asFlow() }
                .onEach(::updateMemberCache)
                .let {
                    if (offset > 0) it.drop(offset) else it
                }.let {
                    if (limit > 0) it.take(limit) else it
                }

        emitAll(flow.map { m -> QGMemberImpl(m, this@QGGuildImpl) })
    }

    override suspend fun member(id: ID): QGMemberImpl? = member(id.literal)

    /**
     * 如果 [internalMembers] 不为null，寻找缓存，否则直接查询
     */
    internal suspend fun member(id: String): QGMemberImpl? {
        return internalMembers?.get(id) ?: queryMember(id)
    }

    private suspend fun queryMember(id: String): QGMemberImpl? {
        val member = try {
            getMemberWithCache(id)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        return member?.let { info -> QGMemberImpl(info, this) }
    }

    override val roles: Items<QGRoleImpl>
        get() = bot.effectedFlowItems {
            GetGuildRoleListApi.create(source.id).requestBy(baseBot).roles.forEach { info ->
                val roleImpl = QGRoleImpl(baseBot, info)
                emit(roleImpl)
            }
        }


    override val currentChannel: Int get() = internalChannels?.size ?: -1

    override val channels: Items<QGChannelImpl>
        get() = internalChannels?.values?.asItems()
            ?: (if (hasChannelListApiPermission) queryChannels() else emptyItems())

    /**
     * 通过API实时查询channels列表
     */
    private fun queryChannels(): Items<QGChannelImpl> = effectedFlowItems {
        val channelListGroupedByCategories = GetGuildChannelListApi
            .create(source.id)
            .requestBy(baseBot)
            .asSequence()
            .onEach { updateChannelCache(it) } // update caches
            .groupBy { it.type.isCategory }

        val channels = channelListGroupedByCategories[false]
        // if empty, just return.
            ?: return@effectedFlowItems
        val categories = channelListGroupedByCategories[true]?.associateBy { it.id } ?: emptyMap()

        val flow = channels
            .asFlow()
            .filter { info ->
                val categoryId = info.parentId

                // 没有对应的分组?
                if (!categories.containsKey(categoryId)) {
                    logger.warn(
                        "Cannot find category(id={}) for channel({}). This is an expected problem and please report this log to issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose?labels=%E7%BC%BA%E9%99%B7",
                        categoryId,
                        info
                    )
                    return@filter false
                }

                true
            }.map { info ->
                val categoryInfo = categories[info.parentId]!!
                val category = QGChannelCategoryImpl(categoryInfo, this@QGGuildImpl)
                QGChannelImpl(bot, info, this@QGGuildImpl, category)
            }

        emitAll(flow)
    }

    override suspend fun channel(id: ID): QGChannel? {
        internalChannels?.also {
            return it[id.literal]
        }

        // by api
        val channelInfo = try {
            getChannelWithCache(source.id)
        } catch (apiEx: QQGuildApiException) {
            if (apiEx.value == 404) null else throw apiEx
        } ?: return null


        val category = QGChannelCategoryIdImpl(this, channelInfo.parentId.ID)
        return QGChannelImpl(bot, channelInfo, this, category)
    }

    override val categories: Items<QGChannelCategoryImpl>
        get() = (if (hasChannelListApiPermission) queryCategories() else emptyItems())

    private fun queryCategories(): Items<QGChannelCategoryImpl> = effectedFlowItems {
        val flow = GetGuildChannelListApi
            .create(source.id)
            .requestBy(baseBot)
            .asFlow()
            .filter { it.type.isCategory }
            .map { info ->
                QGChannelCategoryImpl(info, this@QGGuildImpl)
            }

        emitAll(flow)
    }

    override suspend fun category(id: ID): QGChannelCategoryImpl? = category(id.literal)

    private suspend fun category(id: String): QGChannelCategoryImpl? {
        val info = try {
            getChannelWithCache(id)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        if (info != null && info.type != ChannelType.CATEGORY) {
            throw IllegalStateException("The type of channel(id=${info.id}, name=${info.name}) in guild(id=${info.guildId}, name=$name) is not category, but ${info.type}(${info.type.value})")
        }

        return info?.let { QGChannelCategoryImpl(it, this) }
    }

    /**
     * 同步成员列表、owner、bot的信息。
     */
    private suspend fun syncMembers() {
        trySyncMemberList()

        // TODO 401 process?
        syncBot()
//        syncOwner()
    }

    private suspend fun trySyncMemberList() {
        if (hasMemberListApiPermission && EventIntents.GuildMembers.intents in baseBot.source.configuration.intents) {
            // 如果支持成员列表，且订阅了成员变更事件，查询并缓存用户列表。
            val guildId = source.id
            val members = ConcurrentHashMap<String, SimpleMember>()
            var after: String? = null

            /*
                > 1. 在每次翻页的过程中，可能会返回上一次请求已经返回过的member信息，需要调用方自己根据user id来进行去重。
                > 2. 每次返回的member数量与limit不一定完全相等。翻页请使用最后一个member的user id作为下一次请求的after参数，直到回包为空，拉取结束。
             */

            do {
                val memberList = GetGuildMemberListApi
                    .create(guildId, after = after)
                    .requestBy(bot)

                memberList
                    .asSequence()
                    .onEach { updateMemberCache(it) }
                    .associateByTo(members) { m -> m.user.id }

                after = memberList.lastOrNull()?.user?.id
            } while (memberList.isNotEmpty())

            this.internalMembers =
                members.mapValuesTo(ConcurrentHashMap()) { (_, member) -> QGMemberImpl(member, this) }
        }
    }

    /**
     * 从成员中寻找bot所代表的对象
     */
    private suspend fun syncBot() {
        // 查询用户信息，如果因为权限问题得不到，使用 bot 的user信息直接构建。
        var unauthorized = false
        val botUserId = baseBot.userId
        val member = try {
            member(botUserId)
        } catch (apiEx: QQGuildApiException) {
            if (apiEx.isUnauthorized) {
                unauthorized = true
                null
            } else throw apiEx.copyCurrent()
        }?.also {
            logger.debug("Synced guild(id={}, name={}) bot: {}", source.id, source.name, it)
        } ?: kotlin.run {
            if (unauthorized) {
                logger.warn(
                    "Synced guild(id={}, name={}) botAsMember(userId={}, username={}) is null, because unauthorized",
                    source.id,
                    source.name,
                    botUserId,
                    baseBot.username
                )
            } else {
                // 基本不可能
                logger.warn(
                    "Synced guild(id={}, name={}) botAsMember(userId={}, username={}) is null, because not found",
                    source.id,
                    source.name,
                    botUserId,
                    baseBot.username
                )
            }
            //
            logger.warn(
                "Will create bot member info by [bot.me()]. This will result in the loss of bot's nickname, role and other information in the guild(id={}, name={}).",
                source.id,
                source.name
            )
            val userInfo = baseBot.me()
            val memberInfoByUser = SimpleMemberWithGuildId(source.id, userInfo, "")
            QGMemberImpl(memberInfoByUser, this)
        }

        val guildBot = baseBot.asMember(member)
        bot = guildBot
    }

//    /**
//     * 从成员中寻找owner
//     */
//    private suspend fun syncOwner() {
//        val ownerId = source.ownerId
//        var unauthorized = false
//        val owner = try {
//            member(ownerId)
//        } catch (apiEx: QQGuildApiException) {
//            if (apiEx.isUnauthorized) {
//                unauthorized = true
//                null
//            } else throw apiEx.copyCurrent()
//        }
//
//        if (owner == null) {
//            if (unauthorized) {
//                logger.warn("Synced guild(id={}, name={}) owner(id={}) is null, because unauthorized", source.id, source.name, ownerId)
//            } else {
//                logger.warn("Synced guild(id={}, name={}) owner(id={}) is null, because not found", source.id, source.name, ownerId)
//            }
//        } else {
//            logger.debug("Synced guild(id={}, name={}) owner: {}", source.id, source.name, owner)
//        }
//
//        ownerInternal = owner
//    }


    private suspend fun trySyncChannels() {
        // 拥有API权限，且订阅了guild相关事件
        if (hasChannelListApiPermission && EventIntents.Guilds.intents in baseBot.source.configuration.intents) {
            val channelList = GetGuildChannelListApi.create(source.id).requestBy(baseBot)

            val channelSequence = channelList
                .asSequence()
                // 不要分组类型
                .filterNot { it.type.isCategory }
                .onEach(::updateChannelCache)


            val internalChannels: ConcurrentHashMap<String, QGChannelImpl> = channelSequence
                .associateByTo(ConcurrentHashMap(), { it.id }) { info ->
                    val category = QGChannelCategoryIdImpl(this, info.parentId.ID)
                    QGChannelImpl(bot, info, this, category)
                }

            logger.debug(
                "{} channels synced for Guild(id={}, name={}) (not including category channels)",
                internalChannels.size, id, name,
            )

            this.internalChannels = internalChannels
        } else {
            if (!hasChannelListApiPermission) {
                logger.warn(
                    "Bot(appId={}) does not have access to the channel list in guild(id={}), skip sync.",
                    baseBot.id,
                    source.id
                )
            } else {
                logger.warn(
                    "bot(appId={}) is not listening to Guild related events (see EventIntents.Guilds, intents={}), skip sync.",
                    baseBot.id,
                    EventIntents.Guilds.intents
                )
            }
        }
    }

    //region internal events
    private suspend fun resolveChannel(channel: EventChannel): QGChannelImpl {
        val channelId = channel.id
        val info = updateChannelCache(channel) ?: getChannelWithCache(channelId)

        if (channel.type.isCategory) {
            throw IllegalStateException("Cannot resolve category, but $channel")
        }

        val internalChannels = internalChannels

        return if (internalChannels == null) {
            // not support
            val category = QGChannelCategoryIdImpl(this, info.parentId.ID)
            QGChannelImpl(bot, info, this, category)
        } else {
            // ok
            internalChannels.computeIfPresent(channelId) { _, curr ->
                curr.update(info)
            } ?: kotlin.run {
                val category = QGChannelCategoryIdImpl(this, info.parentId.ID)
                internalChannels.computeIfAbsent(channelId) { QGChannelImpl(bot, info, this, category) }
            }
        }

    }

    internal suspend fun emitChannelCreate(event: ChannelCreate): QGChannelImpl {
        return resolveChannel(event.data)
    }

    internal suspend fun emitChannelUpdate(event: ChannelUpdate): QGChannelImpl {
        return resolveChannel(event.data)
    }

    internal fun emitChannelDelete(event: ChannelDelete): QGChannelImpl? {
        val channel = event.data
        val channelId = channel.id
        removeChannelCache(channelId)

        return internalChannels?.remove(channelId)
    }
    //endregion

    //region internal member events
    private fun resolveMember(member: EventMember): QGMemberImpl {
        // update cache
        updateMemberCache(member)

        fun newMember(): QGMemberImpl = QGMemberImpl(member, this)

        val id = member.user.id

        return internalMembers?.let { internalMembers ->
            internalMembers.compute(id) { _, old ->
                old?.update(member) ?: newMember()
            }
        } ?: newMember()
    }

    internal suspend fun emitMemberAdd(event: GuildMemberAdd): QGMemberImpl {
        return resolveMember(event.data)
    }

    internal suspend fun emitMemberUpdate(event: GuildMemberUpdate): QGMemberImpl {
        return resolveMember(event.data)
    }

    internal fun emitMemberRemove(event: GuildMemberRemove): QGMemberImpl? {
        val member = event.data
        val memberId = member.user.id
        removeMemberCache(memberId)

        return internalMembers?.remove(memberId)
    }
    //endregion


    companion object {
        private val logger =
            LoggerFactory.getLogger("love.forte.simbot.component.qguild.internal.QGGuildImpl")

        internal suspend fun qgGuild(
            bot: QGBotImpl,
            guild: QGSourceGuild
        ): QGGuildImpl {
            return qgGuildWithoutInit(bot, guild).also { it.initData() }
        }

        internal fun qgGuildWithoutInit(
            bot: QGBotImpl,
            guild: QGSourceGuild,
        ): QGGuildImpl {
            // permissions
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, job, coroutineContext)
        }
    }
}


/*
    你妈的逼缓存受不了了不想搞真勾吧麻烦一拳把公私域权限打爆
 */
