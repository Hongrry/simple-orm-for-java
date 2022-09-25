package cn.hruit.orm.test.dao;

import cn.hruit.orm.annotations.Delete;
import cn.hruit.orm.annotations.Insert;
import cn.hruit.orm.annotations.Select;
import cn.hruit.orm.annotations.Update;
import cn.hruit.orm.test.po.User;

import java.util.List;

public interface IAnnotationUserMapper {
    @Select("SELECT id, user_id, user_name, user_head\n" +
            "FROM user\n" +
            "where id = #{id}")
    User queryUserInfoById(Long id);

    @Select("SELECT id, user_id, user_name, user_head\n" +
            "        FROM user\n" +
            "        where id = #{id}")
    User queryUserInfo(User req);

    @Select("SELECT id, user_id, user_name, user_head\n" +
            "FROM user")
    List<User> queryUserInfoList();

    @Update("UPDATE user\n" +
            "SET user_name = #{userName}\n" +
            "WHERE id = #{id}")
    int updateUserInfo(User req);

    @Insert("INSERT INTO user\n" +
            "(user_id, user_name, user_head, create_time, update_time)\n" +
            "VALUES (#{userId}, #{userName}, #{userHead}, now(), now())")
    int insertUserInfo(User req);

    @Delete("DELETE FROM user WHERE user_id = #{user_id}")
    int deleteUserInfoByUserId(String user_id);
}
