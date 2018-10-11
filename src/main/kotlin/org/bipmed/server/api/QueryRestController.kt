package org.bipmed.server.api

import org.bipmed.server.query.QueryService
import org.bipmed.server.query.Query
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class QueryRestController (private val queryService: QueryService) {
    
    @PostMapping
    fun query(@RequestBody query: Query): QueryResponse {
        return QueryResponse(queryService.query(query))
    }
} 