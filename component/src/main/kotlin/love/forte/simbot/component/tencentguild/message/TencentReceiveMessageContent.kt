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

package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.message.Text

/**
 *
 * @author ForteScarlet
 */
public abstract class TencentReceiveMessageContent: ReceivedMessageContent() {
    /**
     * metadata
     */
    abstract override val metadata: Metadata

    /**
     * 转化消息列表
     */
    abstract override val messages: Messages

    /**
     *
     * 会按照顺序数量逐次替换mention对应字符串.
     *
     * 例如，发送的消息为：
     * ```
     * @张三 你好
     * ```
     * 此时收到的消息中的 content为
     * ```
     * <@!123456> 你好
     * ```
     * 那么就会根据mentions的数据今天替换，最终的 plainText 的值为：
     * ```
     *  你好
     * ```
     * ⚠️ 注意！此处的 ` 你好` 前面是大概率有空格的，因为目前在默认情况下不会对消息有过多的操作。
     * 因此如果有需要，请注意在判断之前先进行 `trim` 等操作来消除空格。
     *
     * 如果发送的消息为：
     * ```
     * @张三 <@!123456> 你好
     * ```
     * 注意，此时消息发送者伪造了一个假的 `<@!123456>`, 这时收到的消息的content为：
     * ```
     * <@!123456> <@!123456> 你好
     * ```
     * [plainText] 便会根据 `mentions` 中的数据，只清除其中的第一个 `<@!123456>`，
     * 最终的 [plainText] 便会表现为：
     * ```
     *  <@!123456> 你好
     * ```
     *
     * 对于 @全体成员 同理，且如果存在@全体成员，只会清理一次。
     *
     * 如果你想要得到本次消息最原始的 `content`，请从 [messages] 中获取唯一的 [Text] 类型元素.
     *
     */
    abstract override val plainText: String

    override fun toString(): String {
        return "TencentReceiveMessageContent(metadata=$metadata)"
    }

    override fun hashCode(): Int = metadata.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is TencentReceiveMessageContent) return false
        return metadata == other.metadata
    }

    /**
     * Tencent频道中的消息元数据，存储消息的id，消息频道的id，消息子频道的id和消息创建的时间。
     */
    @SerialName("tcg.m.meta")
    @Serializable
    public data class Metadata(
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        override val id: ID,
        @SerialName("channel_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        public val channelId: ID,
        @SerialName("guild_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        public val guildId: ID,
        @SerialName("create_time")
        public val createTime: Timestamp
    ) : Message.Metadata() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Metadata

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + id.hashCode()
            return result
        }
    }

}