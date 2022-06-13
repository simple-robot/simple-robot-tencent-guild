/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.component.tencentguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.event.TcgAudioEvent
import love.forte.simbot.component.tencentguild.internal.TencentChannelImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildComponentBotImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl
import love.forte.simbot.component.tencentguild.internal.TencentGuildImpl.Companion.tencentGuildImpl
import love.forte.simbot.event.Event
import love.forte.simbot.tencentguild.EventSignals
import love.forte.simbot.tencentguild.TencentAudioAction
import love.forte.simbot.tencentguild.api.channel.GetChannelApi
import love.forte.simbot.tencentguild.api.guild.GetGuildApi
import love.forte.simbot.tencentguild.requestBy
import love.forte.simbot.utils.LazyValue


private suspend fun TencentGuildComponentBotImpl.createGuildImpl(
    action: TencentAudioAction,
): TencentGuildImpl {
    val guild = GetGuildApi(action.guildId).requestBy(sourceBot)
    return tencentGuildImpl(this, guild)
}

private suspend fun TencentGuildComponentBotImpl.createChannelImpl(
    guild: TencentGuildImpl, action: TencentAudioAction,
): TencentChannelImpl {
    val channel = GetChannelApi(action.channelId).requestBy(sourceBot)
    return TencentChannelImpl(this, channel, guild)
}

private fun tcgAudioEventId(
    type: Int, guildId: ID, channelId: ID, timestamp: Timestamp,
): ID = "$type.$guildId.$channelId.${timestamp.second}".ID


/**
 *
 */
internal class TcgAudioStart(
    override val bot: TencentGuildComponentBotImpl,
    override val sourceEventEntity: TencentAudioAction,
    override val channel: TencentChannelImpl,
) : TcgAudioEvent.Start() {
    override val id: ID = tcgAudioEventId(0, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioStart
        override val key: Event.Key<*>
            get() = Start
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            val guild = bot.getInternalGuild(data.guildId)
            val channel = guild?.getInternalChannel(data.channelId) ?: kotlin.run {
                bot.createChannelImpl(guild ?: bot.createGuildImpl(data), data)
            }
            
            return TcgAudioStart(bot, data)
        }
        
    }
}

/**
 *
 */
internal class TcgAudioFinish(
    override val bot: TencentGuildComponentBotImpl, override val sourceEventEntity: TencentAudioAction,
) : TcgAudioEvent.Finish() {
    override val id: ID = tcgAudioEventId(1, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)
    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioFinish
        override val key: Event.Key<*>
            get() = Finish
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            return TcgAudioFinish(bot, data)
        }
    }
}

/**
 *
 */
internal class TcgAudioOnMic(
    override val bot: TencentGuildComponentBotImpl, override val sourceEventEntity: TencentAudioAction,
) : TcgAudioEvent.OnMic() {
    override val id: ID = tcgAudioEventId(2, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)
    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOnMic
        override val key: Event.Key<*>
            get() = OnMic
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            return TcgAudioOnMic(bot, data)
        }
    }
}

/**
 *
 */
internal class TcgAudioOffMic(
    override val bot: TencentGuildComponentBotImpl, override val sourceEventEntity: TencentAudioAction,
) : TcgAudioEvent.OffMic() {
    override val id: ID = tcgAudioEventId(3, sourceEventEntity.guildId, sourceEventEntity.channelId, timestamp)
    
    private val lazyGuild: LazyValue<TencentGuildImpl> = bot.lazyGuild(sourceEventEntity)
    private val lazyChannel: LazyValue<TencentChannelImpl> = bot.lazyChannel(lazyGuild, sourceEventEntity)
    override suspend fun channel(): TencentChannelImpl = lazyChannel.await()
    
    internal object Parser : BaseSignalToEvent<TencentAudioAction>() {
        override val type: EventSignals<TencentAudioAction>
            get() = EventSignals.AudioAction.AudioOffMic
        override val key: Event.Key<*>
            get() = OffMic
        
        override suspend fun doParser(data: TencentAudioAction, bot: TencentGuildComponentBotImpl): Event {
            return TcgAudioOffMic(bot, data)
        }
    }
}