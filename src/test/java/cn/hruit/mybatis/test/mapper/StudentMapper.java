package cn.hruit.mybatis.test.mapper;


import cn.hruit.mybatis.test.entity.StudentEntity;

public interface StudentMapper {

    public StudentEntity getStudentById(int id);

    public int addStudent(StudentEntity student);

    public int updateStudentName(StudentEntity student);

    public StudentEntity getStudentByIdWithClassInfo(int id);
}
