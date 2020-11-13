package br.com.devcave.jpa.domain

data class EmployeeResponse(
    val id: Long,
    val name: String,
    val document: String
)