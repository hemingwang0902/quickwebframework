package com.qwf.school.mis.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.qwf.school.mis.student.service.StudentService;

@Controller
public class StudentEditController {
	@Autowired
	private StudentService studentService;
	
}
