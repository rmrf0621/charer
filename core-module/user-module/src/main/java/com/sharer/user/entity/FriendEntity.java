package com.sharer.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @since 2021-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_friend")
public class FriendEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String uid;

    /**
     * 好友账号
     */
    private String friendUid;

    /**
     * 0, normal; 1, deleted; 2, blacked
     */
    private Integer status;

    /**
     * 别名
     */
    private String alias;

    /**
     * 0, normal; 1, blacked
     */
    private Integer blacked;

    /**
     * 其他
     */
    private String extra;

    private String pinyin;

    /**
     * 好友添加方式
     */
    private String addWay;

    private String portrait;

    /**
     * 添加好友时间
     */
    //@JsonFormat(timezone = "GMT+8", pattern = "yyyyMMddHHmmss")
    private LocalDateTime addTime;


}
