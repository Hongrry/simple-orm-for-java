package cn.hruit.orm.test.dao;

import cn.hruit.orm.test.po.User;

import java.util.List;

public interface IUserMapper {
    User queryUserInfoById(Long uid);

    User queryUserInfo(User user);

    List<User> queryUserInfoList();

    Integer insertUserInfo(User user);

    Integer updateUserInfo(User user);

    Integer deleteUserInfoByUserId(String userId);
}
