package com.sharer.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_message")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发送人
     */
    private String senderId;

    /**
     * 发送人
     */
    private String senderAccount;

    /**
     * 接收人
     */
    private String receiverId;

    /**
     * 接收人
     */
    private String receiverName;

    /**
     * 聊天内容
     */
    private String content;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 读状态
     */
    private String readStatus;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * json形式的消息
     */
    private String message;


}
