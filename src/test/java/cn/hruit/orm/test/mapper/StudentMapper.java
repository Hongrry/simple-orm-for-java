package cn.hruit.orm.test.mapper;


import cn.hruit.orm.test.entity.StudentEntity;

public interface StudentMapper {

    public StudentEntity getStudentById(int id);

    public int addStudent(StudentEntity student);

    public int updateStudentName(StudentEntity student);

    public StudentEntity getStudentByIdWithClassInfo(int id);
}
