package cn.hruit.mybatis.test.dao;

import cn.hruit.mybatis.test.po.User;

import java.util.List;

public interface IUserMapper {
    User queryUserInfoById(Long uid);

    User queryUserInfo(User user);

    List<User> queryUserInfoList();

    Integer insertUserInfo(User user);

    Integer updateUserInfo(User user);
}
