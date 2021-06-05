package com.sharer.user.vo;

import com.sharer.user.entity.FriendEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 好友列表,数据组装，返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendsListVo implements Serializable {

    private String title;

    private String type;

    private List<FriendsVo> list;


}
