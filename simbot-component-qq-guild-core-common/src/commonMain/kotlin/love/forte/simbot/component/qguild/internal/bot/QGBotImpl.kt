/*
 * Copyright (c) 2021-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal.bot

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.bot.JobBasedBot
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.bot.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.channel.*
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGGuildRelation
import love.forte.simbot.component.qguild.internal.channel.*
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl.Companion.qgGuild
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.message.sendMessage
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.guild.GetGuildApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.api.user.createFlow
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.SimpleGuild
import love.forte.simbot.qguild.model.isCategory
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import love.forte.simbot.qguild.model.User as QGUser
import love.forte.simbot.qguild.stdlib.Bot as StdlibBot

/**
 *
 * @author ForteScarlet
 */
internal class QGBotImpl(
    override val source: StdlibBot,
    override val component: QQGuildComponent,
    private val eventDispatcher: EventDispatcher,
    configuration: QGBotComponentConfiguration
) : QGBot, JobBasedBot() {
    private val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.qguild.bot.${source.ticket.secret}")

    override val job: Job
        get() = source.coroutineContext[Job]!!

    override val coroutineContext: CoroutineContext
        get() = source.coroutineContext

    private val cacheConfig = configuration.cacheConfig
    private val cacheable = cacheConfig?.enable == true

    init {
        // check config with warning log
        if (configuration.cacheConfig?.dynamicCacheConfig?.enable == true) {
            logger.warn("DynamicCacheConfig is not supported yet, but dynamicCacheConfig.enable == `true`. This will have no real effect.")
        }
    }

    @Volatile
    private lateinit var botSelf: QGUser

    override val userId: ID
        get() {
            if (!::botSelf.isInitialized) {
                throw IllegalStateException("Information of bot has not been initialized. Please execute the `start()` method at least once first")
            }

            return botSelf.id.ID
        }

    override val name: String
        get() {
            if (!::botSelf.isInitialized) {
                throw IllegalStateException("Information of bot has not been initialized. Please execute the `start()` method at least once first")
            }

            return botSelf.username
        }

    override val avatar: String
        get() = if (!::botSelf.isInitialized) "" else botSelf.avatar


    override fun isMe(id: ID): Boolean {
        if (id == this.id) return true
        return ::botSelf.isInitialized && botSelf.id == id.literal
    }

    override val guildRelation: QGGuildRelation = GuildRelationImpl()


    private inner class GuildRelationImpl : QGGuildRelation {
        @OptIn(ExperimentalCoroutinesApi::class)
        override val guilds: Collectable<QGGuild>
            get() = queryGuildList().flatMapConcat { it.asFlow() }.map {
                qgGuild(this@QGBotImpl, it)
            }.asCollectable()
        // TODO Spec Collectable type?


        override suspend fun guild(id: ID): QGGuild? = queryGuild(id.literal)

        override suspend fun guildCount(): Int =
            GetBotGuildListApi.createFlow { requestDataBy(source) }.count()

        override suspend fun channel(channelId: ID): QGChannel? = channel(channelId.literal, null)

        override suspend fun chatChannel(channelId: ID): QGTextChannel? {
            val channel = channel(channelId) ?: return null

            return channel as? QGTextChannel
                ?: throw IllegalStateException("The type of channel(id=${channel.source.id}, name=${channel.source.name}) is not category (${ChannelType.TEXT}), it is ${channel.source.type}")
        }

        override suspend fun category(channelId: ID): QGCategoryChannel? {
            val channel = channel(channelId) ?: return null

            return channel as? QGCategoryChannel
                ?: throw IllegalStateException("The type of channel(id=${channel.source.id}, name=${channel.source.name}) is not category (${ChannelType.CATEGORY}), it is ${channel.source.type}")
        }

        override suspend fun forumChannel(id: ID): QGForumChannel? {
            val channel = channel(id) ?: return null

            return channel as? QGForumChannel
                ?: throw IllegalStateException("The type of channel(id=${channel.source.id}, name=${channel.source.name}) is not category (${ChannelType.FORUM}), it is ${channel.source.type}")
        }
    }


    internal suspend fun queryGuild(id: String): QGGuildImpl? {
        return try {
            GetGuildApi.create(id).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }?.let { guild -> qgGuild(this, guild) }
    }

    private fun queryGuildList(batch: Int = 0): Flow<List<SimpleGuild>> = flow {
        val limit = batch.takeIf { it > 0 } ?: GetBotGuildListApi.DEFAULT_LIMIT
        var lastId: String? = null
        while (true) {
            val list = GetBotGuildListApi.create(after = lastId, limit = limit).requestDataBy(source)
            if (list.isEmpty()) break
            lastId = list.last().id
            emit(list)
        }
    }

    internal suspend fun querySimpleChannel(id: String): SimpleChannel? {
        return try {
            GetChannelApi.create(id).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    internal fun channelFlow(guildId: String): Flow<SimpleChannel> {
        return flow {
            GetGuildChannelListApi.create(guildId)
                .requestDataBy(source)
                .forEach {
                    emit(it)
                }
        }
    }


    internal suspend fun channel(id: String, sourceGuild: QGGuildImpl?): QGChannel? {
        val channelInfo = querySimpleChannel(id) ?: return null

        return when (channelInfo.type) {
            ChannelType.CATEGORY -> QGCategoryChannelImpl(
                bot = this,
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            ChannelType.TEXT -> QGTextChannelImpl(
                bot = this,
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            ChannelType.FORUM -> QGForumChannelImpl(
                bot = this,
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )

            else -> QGNonTextChannelImpl(
                bot = this,
                source = channelInfo,
                sourceGuild = checkIfTransmitCacheable(sourceGuild),
            )
        }
    }

    internal data class ChannelInfoWithCategory(val info: SimpleChannel, val category: QGCategory)

    internal fun channelFlowWithCategoryId(guildId: String, sourceGuild: QGGuildImpl?): Flow<ChannelInfoWithCategory> {
        val categoryMap = concurrentMutableMap<String, QGCategory>()

        return channelFlow(guildId).filter { info ->
            val gid = info.guildId.ID
            if (info.type.isCategory) {
                categoryMap.computeValueIfAbsent(info.id) {
                    QGCategoryImpl(
                        bot = this,
                        guildId = gid,
                        id = info.id.ID,
                        sourceGuild = checkIfTransmitCacheable(sourceGuild),
                        source = QGCategoryChannelImpl(
                            bot = this,
                            source = info,
                            sourceGuild = sourceGuild,
                        ),
                    )
                }

                false
            } else {
                true
            }
        }.map { info ->
            val gid = info.guildId.ID
            val category = categoryMap.computeValueIfAbsent(info.parentId) { cid ->
                QGCategoryImpl(
                    bot = this,
                    guildId = gid,
                    id = cid.ID,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                )
            }

            ChannelInfoWithCategory(info, category)
        }
    }

    /**
     * 通过API实时查询channels列表
     */
    internal fun queryChannels(guildId: String, sourceGuild: QGGuildImpl?): Flow<QGChannel> = flow {
        channelFlowWithCategoryId(guildId, sourceGuild).map { (info, category) ->
            when (info.type) {
                ChannelType.TEXT -> QGTextChannelImpl(
                    bot = this@QGBotImpl,
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category,
                )

                ChannelType.FORUM -> QGForumChannelImpl(
                    bot = this@QGBotImpl,
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category,
                )

                else -> QGNonTextChannelImpl(
                    bot = this@QGBotImpl,
                    source = info,
                    sourceGuild = checkIfTransmitCacheable(sourceGuild),
                    category = category
                )
            }
        }
    }

    override suspend fun sendTo(channelId: ID, text: String): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, text)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    override suspend fun sendTo(channelId: ID, message: Message): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    override suspend fun sendTo(channelId: ID, message: MessageContent): QGMessageReceipt {
        return try {
            sendMessage(channelId.literal, message)
        } catch (e: QQGuildApiException) {
            throw e.addStackTrace { "Bot.sendTo" }
        }
    }

    internal suspend fun member(guildId: String, userId: String, sourceGuild: QGGuildImpl?): QGMemberImpl? {
        val member = try {
            GetMemberApi.create(guildId, userId).requestDataBy(source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }

        return member?.let { info ->
            QGMemberImpl(
                bot = this,
                source = info,
                guildId = guildId.ID,
                sourceGuild = checkIfTransmitCacheable(sourceGuild)
            )
        }
    }

    private val startLock = Mutex()

    override suspend fun me(withCache: Boolean): QGUser {
        if (withCache && ::botSelf.isInitialized) {
            return botSelf
        }

        return source.me().also { botSelf = it }
    }


    @Volatile
    private var sourceListenerDisposableHandle: DisposableHandle? = null

    /**
     * 启动当前bot。
     */
    override suspend fun start() {
        startLock.withLock {
            source.start().also {
                // just set everytime.
                botSelf = me().also { me ->
                    logger.debug("bot own information: {}", me)
                }

                suspend fun pushStartedEvent() {
                    // TODO
//                    if (eventDispatcher.isProcessable(QGBotStartedEvent)) {
//                        launch {
//                            eventProcessor.push(QGBotStartedEventImpl(this@QGBotImpl))
//                        }
//                    }
                }

                if (!isStarted) {
                    pushStartedEvent()
                    return@also
                }

                sourceListenerDisposableHandle?.also { handle ->
                    handle.dispose()
                    sourceListenerDisposableHandle = null
                }
                // TODO
//                sourceListenerDisposableHandle = registerEventProcessor()
                pushStartedEvent()
            }

            isStarted = true
        }
    }


    private val isTransmitCacheable = cacheable && cacheConfig?.transmitCacheConfig?.enable == true

    internal fun <T> checkIfTransmitCacheable(target: T): T? = target.takeIf { isTransmitCacheable }

    override fun toString(): String {
        // 还未初始化
        val uid = if (::botSelf.isInitialized) botSelf.id else "(Not initialized yet)"
        return "QGBotImpl(appId=$id, userId=$uid, isActive=$isActive)"
    }
}


/**
 * 从 [QGBot] 中分配一个拥有新的 [SupervisorJob] 的 [CoroutineContext].
 */
internal fun CoroutineScope.newSupervisorCoroutineContext(): CoroutineContext =
    coroutineContext + SupervisorJob(coroutineContext[Job])
