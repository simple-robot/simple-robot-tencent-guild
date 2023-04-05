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

package love.forte.simbot.qguild.api.channel.schedules

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.RouteInfoBuilder
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.Schedule
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [获取频道日程列表](https://bot.q.qq.com/wiki/develop/api/openapi/schedule/get_schedules.html)
 *
 * 用于获取 `channel_id` 指定的子频道中当天的日程列表。
 *
 * - 若带了参数 `since`，则返回在 `since` 对应当天的日程列表；若未带参数 `since`，则默认返回今天的日程列表。
 *
 * @author ForteScarlet
 */
public class GetScheduleListApi private constructor(
    channelId: String, private val since: Long
) : GetQQGuildApi<List<Schedule>>() {
    public companion object Factory : SimpleGetApiDescription("/channels/{channel_id}/schedules") {
        private val serializer = ListSerializer(Schedule.serializer())

        /**
         * 构造 [GetScheduleListApi]。
         *
         * @param channelId 查询的目标频道
         * @param since 若 [since] > 0，则返回在 [since] 对应当天的日程列表；否则默认返回今天的日程列表。
         * [since] 默认为 `-1`，即回今天的日程列表。
         *
         */
        @JvmStatic
        @JvmOverloads
        public fun create(channelId: String, since: Long = -1): GetScheduleListApi = GetScheduleListApi(channelId, since)
    }

    override val resultDeserializer: DeserializationStrategy<List<Schedule>> get() = serializer

    private val path = arrayOf("channels", channelId, "schedules")

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = path
        if (since > 0) {
            builder.parametersAppender.append("since", since)
        }
    }
}
