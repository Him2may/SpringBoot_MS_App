package com.department_service.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.department_service.app.entiry.Department;

@Repository
public interface DepartmentRepo extends CrudRepository<Department, Long> {

	Optional<Department> findByDepartmentNameOrId(String DepartmentName, long id);

}
