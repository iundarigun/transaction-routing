package br.com.devcave.jpa.domain

import javax.validation.constraints.NotBlank

data class EmployeeRequest(
    @field:NotBlank
    var name: String = "",
    @field:NotBlank
    var document: String = ""
)