package com.employee_service.app.serivce;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.employee_service.app.entity.DepartmentDTO;

@RefreshScope
@FeignClient(name = "department-service", url = "${feign.client.url}" )
public interface DepartmentClient {

    @GetMapping("/api/department/get/{id}")
    ResponseEntity<DepartmentDTO> getDepartment(@PathVariable("id") Long id);

    @PutMapping("/api/department/{id}/increment")
    void incrementHeadCount(@PathVariable("id") Long id);

    @PutMapping("/api/department/{id}/decrement")
    void decrementHeadCount(@PathVariable("id") Long id);
}
