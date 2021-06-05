package com.sharer.api.controller;

import com.sharer.api.shiro.RequestUtils;
import com.sharer.api.shiro.vo.UserTokenVo;
import com.sharer.common.utils.GeneraterResult;
import com.sharer.common.utils.Result;
import com.sharer.user.entity.FriendEntity;
import com.sharer.user.service.IFriendService;
import com.sharer.user.vo.FriendsListVo;
import com.sharer.user.vo.FriendsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/friends/")
public class FriendsController {

    @Autowired
    private IFriendService friendService;

    @RequestMapping("/list")
    public Result queryList() {
        UserTokenVo userVo = RequestUtils.getUser();
        List<Object> result = new ArrayList<>();
        // 后面要返回的数据要包含 新朋友，群聊，好友列表，
        List<FriendsVo> list = friendService.queryFriendsList(userVo.getUserid());
        result.addAll(dealList(list,"friend"));
        return GeneraterResult.success(result);
    }

    /**
     * 组装返回的list
     * @param list
     */
    public List<FriendsListVo> dealList(List<FriendsVo> list,String type) {
        List<FriendsListVo> result = new ArrayList<FriendsListVo>();
        Map<String, List<FriendsVo>> map = new HashMap<String,  List<FriendsVo>>();
        list.forEach(friendEntity -> {
            String letter = friendEntity.getPinyin().substring(0,1).toUpperCase();
            // 首字母分类
            if (map.containsKey(letter)) {
                List<FriendsVo> letterList = map.get(letter);
                letterList.add(friendEntity);
                map.put(letter,letterList);
            }else {
                List<FriendsVo> letterList = new ArrayList<>();
                letterList.add(friendEntity);
                map.put(letter,letterList);
            }
        });
        Set<String> set = map.keySet();
        set.forEach(key->{
            FriendsListVo friendsListVo = new FriendsListVo(key,type,map.get(key));
            result.add(friendsListVo);
        });
        return result;
    }

}
