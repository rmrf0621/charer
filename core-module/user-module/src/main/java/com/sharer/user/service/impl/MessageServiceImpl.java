package com.sharer.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sharer.user.entity.MessageEntity;
import com.sharer.user.mapper.MessageMapper;
import com.sharer.user.service.IMessageService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sharer
 * @since 2021-05-31
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements IMessageService {

}
