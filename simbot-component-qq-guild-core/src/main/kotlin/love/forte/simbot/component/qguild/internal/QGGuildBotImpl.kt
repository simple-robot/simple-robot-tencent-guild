/*
 * Copyright (c) 2022-2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ID
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.QGGuildBot
import love.forte.simbot.component.qguild.QQGuildBotManager
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.util.requestBy
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.literal
import love.forte.simbot.message.Image
import love.forte.simbot.qguild.Bot
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.User
import love.forte.simbot.utils.item.Items
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildBotImpl(
    override val bot: QGBotImpl,
    private val guildId: String,
    private val currentMsgId: String? = null,
    private val sourceGuild: QGGuildImpl? = null,
) : QGGuildBot, QGBot {
    override suspend fun asMember(): QGMemberImpl {
        val member = try {
            GetMemberApi.create(guildId, bot.userId.literal).requestBy(bot)
        } catch (apiEx: QQGuildApiException) {
            if (apiEx.isNotFound) throw NoSuchElementException("bot member(id=${bot.userId})") else throw apiEx
        }
        return QGMemberImpl(bot, member, guildId.ID, sourceGuild)
    }

    override val userId: ID get() = bot.userId
    override val username: String get() = bot.username
    override val avatar: String get() = bot.avatar

    override suspend fun guild(id: ID): QGGuildImpl? = guild(id, this.currentMsgId)

    internal suspend fun guild(id: ID, currentMsgId: String? = this.currentMsgId): QGGuildImpl? =
        bot.guild(id)

    override suspend fun resolveImage(id: ID): Image<*> = bot.resolveImage(id)

    override val coroutineContext: CoroutineContext get() = bot.coroutineContext
    override val isCancelled: Boolean get() = bot.isCancelled
    override val isStarted: Boolean get() = bot.isStarted
    override val logger: Logger get() = bot.logger

    override suspend fun cancel(reason: Throwable?): Boolean = bot.cancel(reason)

    override suspend fun join() = bot.join()

    override val component: QQGuildComponent get() = bot.component
    override val source: Bot get() = bot.source

    override fun isMe(id: ID): Boolean = bot.isMe(id)

    override suspend fun start(): Boolean = bot.start()

    override val manager: QQGuildBotManager
        get() = bot.manager

    override val eventProcessor: EventProcessor
        get() = bot.eventProcessor

    override val guilds: Items<QGGuildImpl>
        get() = bot.guilds

    override suspend fun me(): User = bot.me()
}

