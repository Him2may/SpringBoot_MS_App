package com.department_service.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.department_service.app.entiry.Department;
import com.department_service.app.repository.DepartmentRepo;

import jakarta.annotation.PostConstruct;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
public class DepartmentService {

	final
	DepartmentRepo departmentRepo;
    private final Environment environment;

	public DepartmentService(DepartmentRepo departmentRepo, Environment environment) {
		this.departmentRepo = departmentRepo;
		this.environment = environment;
	}

	@PostConstruct
	public void defaultDepartment() {
//		departmentRepo.findById(1l).ifPresentOrElse(existingDep -> {
//			existingDep.setDepartmentName("Bench");
//			existingDep.setHeadCount(Math.max(existingDep.getHeadCount(), 0));
//			departmentRepo.save(existingDep);
//		}, () -> {
//			Department department = new Department();
//			department.setId(1);
//			department.setDepartmentName("Bench");
//			department.setHeadCount(Math.max(department.getHeadCount(), 0));
//			departmentRepo.save(department);
//		});
		System.out.println("Active profile: " +	Arrays.toString(environment.getActiveProfiles()));
		Department department = departmentRepo.findById(0L).orElse(new Department());
		department.setDepartmentName("Bench");
		department.setHeadCount(Math.max(department.getHeadCount(), 0));
		departmentRepo.save(department);

	}

	public ResponseEntity<?> getDepartments() {
		return ResponseEntity.ok(departmentRepo.findAll());
	}

	public ResponseEntity<?> addDepartment(Department depart) {
		Department department = departmentRepo.findByDepartmentNameOrId(depart.getDepartmentName(), depart.getId())
				.orElse(null);
		if (department == null) {
			department = new Department();
			department.setDepartmentName(depart.getDepartmentName());
		}
		department.setHeadCount(depart.getHeadCount());
		department = departmentRepo.save(department);
		System.out.println(department.getHeadCount());
		return ResponseEntity.status(HttpStatus.CREATED).body(department);
	}

	public ResponseEntity<?> getDepartment(long id) {
		Department department = departmentRepo.findById(id).orElse(null);
		if (department != null) {
			return ResponseEntity.ok(department);
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<?> deleteDepartment(long id) {
		Department department = departmentRepo.findById(id).orElse(null);
		if (department != null) {
			departmentRepo.delete(department);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	public void incrementHeadCount(Long id) {
		System.out.println(id);
		Department dept = departmentRepo.findById(id).get();
		dept.setHeadCount(dept.getHeadCount() + 1);
		departmentRepo.save(dept);
	}

	public void decrementHeadCount(Long id) {
		Department dept = departmentRepo.findById(id).get();
		dept.setHeadCount(Math.max(0, dept.getHeadCount() - 1));
		departmentRepo.save(dept);
	}
}
