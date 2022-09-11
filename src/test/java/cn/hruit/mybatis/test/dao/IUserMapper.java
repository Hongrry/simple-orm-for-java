package cn.hruit.mybatis.test.dao;

import cn.hruit.mybatis.test.po.User;

public interface IUserMapper {
    User queryUserInfoById(Long uid);

    User queryUserInfoByInfo(User user);

    Long selectUserCount(Long uid);

    Integer addUser(User req);
}
