package com.employee_service.app.serivce;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.employee_service.app.entity.DepartmentDTO;
import com.employee_service.app.entity.Employee;
import com.employee_service.app.repository.EmployeeRepo;

import feign.FeignException;
import java.util.logging.Logger;

@Service
public class EmployeeService {

	private final EmployeeRepo empRepo;
	private final DepartmentClient departClient;
	private final Environment environment;
	private static final String EMPLOYEE_SERVICE = "employee-service";
	Logger logger = Logger.getLogger(getClass().getName());


	public EmployeeService(EmployeeRepo empRepo, DepartmentClient departClient, Environment environment) {
        this.empRepo = empRepo;
        this.departClient = departClient;
        this.environment = environment;
    }

    @PostConstruct
	public void init(){
		logger.info("Active profile: " + Arrays.toString(environment.getActiveProfiles()));
		Employee emp = empRepo.findById(1L).orElse(new Employee());
		emp.setFirstName("Him");
		emp.setLastName("Sharma");
		emp.setSalary(new BigDecimal(100000));
		empRepo.save(emp);
	}

	public ResponseEntity<List<Employee>> getEmployees() {
		return ResponseEntity.ok(empRepo.findAll());
	}

	public ResponseEntity<Employee> addOnlyNewEmployee(Employee emp) {
		Employee employee = Employee
				.builder()
				.firstName(emp.getFirstName())
				.lastName(emp.getLastName())
				.salary(emp.getSalary())
				.build();
		try {
			ResponseEntity<DepartmentDTO> response = departClient.getDepartment(emp.getDepartmentId());
            if (response.getBody() != null) {
                departClient.incrementHeadCount(response.getBody().getId());
            }
            employee.setDepartmentId(emp.getDepartmentId());
		} catch (FeignException.NotFound e) {
			logger.info("No department found for the employee ID :" + employee.getEmployeeId());
		}
		employee = empRepo.save(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(employee);
	}

	public ResponseEntity<Employee> getEmployee(long id) {
		Employee employee = empRepo.findById(id).orElse(null);
		if (employee != null) {
			return ResponseEntity.ok(employee);
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Employee> deleteEmployee(long id) {
		Employee employee = empRepo.findById(id).orElse(null);
		if (employee != null) {
			try {
				ResponseEntity<DepartmentDTO> response = departClient.getDepartment(employee.getDepartmentId());
                if (response.getBody() != null) {
                    departClient.decrementHeadCount(response.getBody().getId());
                }
            } catch (FeignException.NotFound e) {
				logger.info("No department found for the employee ID :" + employee.getEmployeeId());
			}
			empRepo.delete(employee);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	public ResponseEntity<Employee> addEmployee(Employee emp) {
		Employee employee = empRepo.findById(emp.getEmployeeId()).orElse(new Employee());
		if (!emp.getFirstName().isEmpty()) {
			employee.setFirstName(emp.getFirstName());
		}
		if (!emp.getLastName().isEmpty()) {
			employee.setLastName(emp.getLastName());
		}
		if (emp.getSalary().compareTo(new BigDecimal(0)) > 0) {
			employee.setSalary(emp.getSalary());
		}
		try {
			ResponseEntity<DepartmentDTO> response = departClient.getDepartment(emp.getDepartmentId());
			if(response.getBody() != null){
				if (employee.getDepartmentId() != emp.getDepartmentId()) {
					if (employee.getDepartmentId() != 0)
						departClient.decrementHeadCount(employee.getDepartmentId());
					employee.setDepartmentId(emp.getDepartmentId());
				}
				departClient.incrementHeadCount(response.getBody().getId());
			}
		} catch (FeignException.NotFound e) {
			if (employee.getDepartmentId() == 0) {
				employee.setDepartmentId(1L);
				departClient.incrementHeadCount(1L);
			}
			logger.info("No department found for the Department ID :" + emp.getDepartmentId());
		}
		employee = empRepo.save(employee);
		return ResponseEntity.status(HttpStatus.CREATED).body(employee);
	}

	public ResponseEntity<List<Employee>> getEmployeesByDepartmentId(long id) {
		List<Employee> employeeList = empRepo.findByDepartmentId(id);
		return ResponseEntity.ok(employeeList);
	}

	@CircuitBreaker(name = EMPLOYEE_SERVICE, fallbackMethod = "getDefaultDepartment")
	@Retry(name=EMPLOYEE_SERVICE)
	public DepartmentDTO getEmployeesDepartment(long id) {
		DepartmentDTO departmentDTO = null;
		try {
			logger.info("Before Sending request for employee ID :" + id);
			ResponseEntity<DepartmentDTO> response = departClient.getDepartment(id);
			logger.info("After Sending request for employee ID :" + id);
			if (response.getBody() != null) {
				departmentDTO = DepartmentDTO.builder()
						.id(response.getBody().getId())
						.departmentName(response.getBody().getDepartmentName())
						.headCount(response.getBody().getHeadCount())
						.build();
			}
			else{
				logger.info("No department found for the employee ID :" + id);
				throw new RuntimeException("Department not found");
			}
		} catch (FeignException.NotFound e) {
			logger.info("No department found for the employee ID :" + id);
			throw new RuntimeException("Department not found");
		}
		return departmentDTO;
	}

	public DepartmentDTO getDefaultDepartment(long id, Exception e){
		logger.info("Fallback Method called : getDefaultDepartment");
		return DepartmentDTO.builder()
				.id(1L).departmentName("Default").headCount(0).build();
	}
}
