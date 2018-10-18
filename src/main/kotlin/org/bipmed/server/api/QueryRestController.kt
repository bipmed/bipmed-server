package org.bipmed.server.api

import org.bipmed.server.error.InvalidQuery
import org.bipmed.server.query.QueryService
import org.bipmed.server.query.Query
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class QueryRestController (private val queryService: QueryService) {
    
    @PostMapping
    fun query(@RequestBody query: Query): QueryResponse {
        if (query.snpId != null || query.geneSymbol != null || (query.referenceName != null && query.start != null)) {
            return QueryResponse(queryService.query(query))
        } else {
            throw InvalidQuery()
        }
    }
} 