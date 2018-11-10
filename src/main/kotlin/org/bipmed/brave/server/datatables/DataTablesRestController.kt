package org.bipmed.brave.server.datatables

import org.bipmed.brave.server.query.QueryService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class DataTablesRestController(private val queryService: QueryService) {

    @PostMapping("/datatables")
    fun search(@RequestBody input: DataTablesInput): DataTablesOutput {
        return queryService.search(input)
    }
}