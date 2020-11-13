package br.com.devcave.jpa.service

import br.com.devcave.jpa.domain.EmployeeRequest
import br.com.devcave.jpa.domain.Employee
import br.com.devcave.jpa.domain.EmployeeResponse
import br.com.devcave.jpa.exception.EmployeeAlreadyExistsException
import br.com.devcave.jpa.exception.EntityNotFoundException
import br.com.devcave.jpa.repository.EmployeeRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmployeeService(
    private val employeeRepository: EmployeeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun deleteEmployee(id: Long) {
        if (!employeeRepository.existsById(id)) {
            throw EntityNotFoundException("employee id $id not found")
        }
        employeeRepository.deleteById(id)
    }

    @Transactional
    fun findAll(page: Int, size: Int): List<EmployeeResponse> {
        val pageRequest = PageRequest.of(page - 1, size, Sort.by("name"))

        return employeeRepository
            .findAll(pageRequest)
            .content.map {
                EmployeeResponse(
                    it.id,
                    it.name,
                    it.document
                )
            }
    }

    @Transactional(readOnly = true)
    fun createEmployee(request: EmployeeRequest): Long {
        if (employeeRepository.existsByDocument(request.document)) {
            throw EmployeeAlreadyExistsException("Employee document ${request.document} already exists")
        }

        val employee = Employee(
            name = request.name,
            document = request.document
        )

        return employeeRepository.save(employee).id
    }

    @Transactional
    fun updateEmployee(id: Long, request: EmployeeRequest) {
        if (!employeeRepository.existsById(id)) {
            throw EntityNotFoundException("employee id $id not found")
        }

        if (employeeRepository.existsByDocumentAndIdNot(request.document, id)) {
            throw EmployeeAlreadyExistsException("Employee document ${request.document} already exists")
        }

        employeeRepository.findById(id).ifPresent {
            val employee = it.copy(
                name = request.name,
                document = request.document
            )
            employeeRepository.save(employee)
        }
    }

    @Transactional(readOnly = true)
    fun findById(id: Long): EmployeeResponse {
        return employeeRepository.findById(id).orElseThrow {
            EntityNotFoundException("employee id $id not found")
        }.let {
            EmployeeResponse(
                it.id,
                it.name,
                it.document
            )
        }
    }
}