package com.sharer.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
     * 时间戳
     */
    private Long datestamp;

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


}
