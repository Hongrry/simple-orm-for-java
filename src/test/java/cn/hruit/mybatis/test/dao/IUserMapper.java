package cn.hruit.mybatis.test.dao;

import cn.hruit.mybatis.test.po.User;

public interface IUserMapper {
    User queryUserInfoById(Long uId);

}
