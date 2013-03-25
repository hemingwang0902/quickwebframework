package com.qwf.school.mis.student.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qwf.school.mis.student.dao.StudentDao;
import com.qwf.school.mis.student.entity.Student;
import com.qwf.school.mis.student.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	private StudentDao studentDao;

	@Override
	public int getStudentCount() {
		return studentDao.getStudentCount();
	}

	@Override
	public Student getStudent(String id) {
		return studentDao.getStudent(id);
	}

	@Override
	public List<Student> queryStudent(String name, int pageIndex, int pageSize) {
		return studentDao.queryStudent(name, pageIndex, pageSize);
	}

	@Override
	public void addStudent(Student stu) {
		studentDao.addStudent(stu);
	}

	@Override
	public void updateStudent(Student stu) {
		studentDao.updateStudent(stu);
	}

	@Override
	public void deleteStudent(Student stu) {
		studentDao.deleteStudent(stu);
	}

	@Override
	public void deleteStudentById(String id) {
		studentDao.deleteStudentById(id);
	}

	@Override
	public boolean checkStudentTables() {
		return studentDao.checkStudentTable();
	}

	@Override
	public void repairStudentTables() {
		studentDao.repairStudentTable();
	}
}
