package com.department_service.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.department_service.app.entiry.Department;
import com.department_service.app.service.DepartmentService;

import java.lang.management.ManagementFactory;

@RestController
@RequestMapping("/api/department")
@RefreshScope
public class DepartmentController {

	@Value("${spring.datasource.url:NOT_FOUND}")
	private String dbUrl;

	@Value("${spring.datasource.username:NOT_FOUND}")
	private String dbUser;

	DepartmentService departmentService;

	@Autowired
	public DepartmentController(DepartmentService departmentService){
		this.departmentService = departmentService;
	}

	@GetMapping("/getAll")
	public ResponseEntity<?> getDepartments() {
		return departmentService.getDepartments();
	}

	@PostMapping("/add")
	public ResponseEntity<?> addDepartment(@RequestBody Department dep) {
		return departmentService.addDepartment(dep);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<?> getDepartment(@PathVariable long id) {
		return departmentService.getDepartment(id);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteDepartment(@PathVariable long id) {
		return departmentService.deleteDepartment(id);
	}

	@PutMapping("/{id}/increment")
	public void incrementHeadCount(@PathVariable Long id) {
		departmentService.incrementHeadCount(id);
	}

	@PutMapping("/{id}/decrement")
	public void decrementHeadCount(@PathVariable Long id) {
		departmentService.decrementHeadCount(id);
	}

	@GetMapping("/info")
	public String info() {
		return "Handled by instance: " +
				ManagementFactory.getRuntimeMXBean().getName()
				+ " " + dbUrl + " " + " " + dbUser;
	}
}
