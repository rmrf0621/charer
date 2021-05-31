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
@TableName("t_user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 头像
     */
    private String portrait;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 公司
     */
    private String company;

    /**
     * 盐值
     */
    private String salt;

    /**
     * 是否已删除
     */
    private String delete;

    /**
     * 创建时间
     */
    private LocalDateTime createtime;


}
