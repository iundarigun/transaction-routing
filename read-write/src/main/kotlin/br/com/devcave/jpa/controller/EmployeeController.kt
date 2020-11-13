package br.com.devcave.jpa.controller

import br.com.devcave.jpa.domain.EmployeeRequest
import br.com.devcave.jpa.domain.EmployeeResponse
import br.com.devcave.jpa.service.EmployeeService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Validated
@RestController
@RequestMapping("employees")
class EmployeeController(
    private val employeeService: EmployeeService
) {
    companion object {
        private const val maxElements = 50L
    }

    @GetMapping
    fun findAll(
        @Valid @Min(1) @RequestParam(required = false, defaultValue = "1") page: Int,
        @Valid @Min(1) @Max(maxElements) @RequestParam(required = false, defaultValue = "20") size: Int = 20
    ): ResponseEntity<List<EmployeeResponse>> {
        val employeeList = employeeService.findAll(page, size)

        return ResponseEntity.ok(employeeList)
    }

    @GetMapping("{id}")
    fun findById(
        @PathVariable id: Long
    ): ResponseEntity<EmployeeResponse> {
        val employee = employeeService.findById(id)

        return ResponseEntity.ok(employee)
    }

    @PostMapping
    fun create(@Valid @RequestBody request: EmployeeRequest): ResponseEntity<Any?> {
        val id = employeeService.createEmployee(request)

        val location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .build(id)

        return ResponseEntity.created(location).build()
    }

    @PutMapping("{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: EmployeeRequest
    ): ResponseEntity<Any?> {
        employeeService.updateEmployee(id, request)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Any?> {
        employeeService.deleteEmployee(id)

        return ResponseEntity.noContent().build()
    }
}