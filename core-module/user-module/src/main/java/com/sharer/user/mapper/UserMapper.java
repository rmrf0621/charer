package com.sharer.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sharer.user.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

}
