package br.com.devcave.jpa.repository

import br.com.devcave.jpa.domain.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository : JpaRepository<Employee, Long> {
    fun existsByDocument(document: String): Boolean
    fun existsByDocumentAndIdNot(document: String, id: Long): Boolean
}