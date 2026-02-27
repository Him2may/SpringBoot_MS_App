package com.employee_service.app.controller;

import com.employee_service.app.entity.DepartmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.employee_service.app.entity.Employee;
import com.employee_service.app.serivce.EmployeeService;

import java.lang.management.ManagementFactory;
import java.util.List;

@RestController
@RequestMapping("/api/Employee")
public class EmployeeController {

	private final EmployeeService empService;

    public EmployeeController(EmployeeService empService) {
        this.empService = empService;
    }

    @GetMapping("/getAll")
	public ResponseEntity<List<Employee>> getEmployees() {
		return empService.getEmployees();
	}

	@PostMapping("/add")
	public ResponseEntity<Employee> addEmployee(@RequestBody Employee emp) {
		return empService.addOnlyNewEmployee(emp);
	}

	@PutMapping("/update")
	public ResponseEntity<Employee> updateEmployee(@RequestBody Employee emp) {
		return empService.addEmployee(emp);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<Employee> getEmployee(@PathVariable long id) {
		return empService.getEmployee(id);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Employee> deleteEmployee(@PathVariable long id) {
		return empService.deleteEmployee(id);

	}
	
	@GetMapping("/getEmpByDepartment/{id}")
	public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(@PathVariable long id) {
		return empService.getEmployeesByDepartmentId(id);
	}

	@GetMapping("/getDepartmentByEmpId/{id}")
	public ResponseEntity<DepartmentDTO> getDepartmentByEmpId(@PathVariable long id) {
		return ResponseEntity.ok(empService.getEmployeesDepartment(id));
	}


	@GetMapping("/info")
	public String info() {
		return "Handled by instance: " +
				ManagementFactory.getRuntimeMXBean().getName();
	}

}
