/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.ActionType
import love.forte.simbot.component.qguild.QGBot
import love.forte.simbot.component.qguild.event.QGMemberAddEvent
import love.forte.simbot.component.qguild.event.QGMemberRemoveEvent
import love.forte.simbot.component.qguild.event.QGMemberUpdateEvent
import love.forte.simbot.component.qguild.internal.QGGuildImpl
import love.forte.simbot.component.qguild.internal.QGMemberImpl
import love.forte.simbot.qguild.InternalApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.event.EventMember
import love.forte.simbot.qguild.isUnauthorized


internal class QGMemberAddEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberAddEvent() {
    private val currentTimeMillis = System.currentTimeMillis()
    override val changedTime: Timestamp get() = Timestamp.byMillisecond(currentTimeMillis)
    override val actionType: ActionType
        get() = if (sourceEventEntity.opUserId == sourceEventEntity.user.id) ActionType.PROACTIVE else ActionType.PASSIVE

    override val id: ID get() = memberEventId(0, bot.id, sourceEventEntity.user.id, currentTimeMillis, hashCode())
    override suspend fun member(): QGMemberImpl = _member
    @OptIn(InternalApi::class)
    override suspend fun operator(): QGMemberImpl? {
        return try {
            guild().member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member.guild()
}

internal class QGMemberUpdateEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberUpdateEvent() {
    private val currentTimeMillis = System.currentTimeMillis()
    override val changedTime: Timestamp get() = Timestamp.byMillisecond(currentTimeMillis)
    override val id: ID get() = memberEventId(1, bot.id, sourceEventEntity.user.id, currentTimeMillis, hashCode())
    override suspend fun member(): QGMemberImpl = _member
    @OptIn(InternalApi::class)
    override suspend fun operator(): QGMemberImpl? {
        return try {
            guild().member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member.guild()
}

internal class QGMemberRemoveEventImpl(
    override val bot: QGBot,
    override val eventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberRemoveEvent() {
    private val currentTimeMillis = System.currentTimeMillis()
    override val changedTime: Timestamp get() = Timestamp.byMillisecond(currentTimeMillis)
    override val actionType: ActionType
        get() = if (sourceEventEntity.opUserId == sourceEventEntity.user.id) ActionType.PROACTIVE else ActionType.PASSIVE

    override val id: ID get() = memberEventId(2, bot.id, sourceEventEntity.user.id, currentTimeMillis, hashCode())
    override suspend fun member(): QGMemberImpl = _member
    @OptIn(InternalApi::class)
    override suspend fun operator(): QGMemberImpl? {
        return try {
            guild().member(sourceEventEntity.opUserId)
        } catch (apiEx: QQGuildApiException) {
            // process no auth
            if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace()
        }
    }

    override suspend fun guild(): QGGuildImpl = _member.guild()
}

private fun memberEventId(t: Int, sourceBot: ID, sourceUserId: String, timestamp: Long, hash: Int): ID =
    "$t$sourceBot.$timestamp.$sourceUserId.$hash".ID
