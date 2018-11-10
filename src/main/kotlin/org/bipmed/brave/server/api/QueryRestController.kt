package org.bipmed.brave.server.api

import org.bipmed.brave.server.error.InvalidQuery
import org.bipmed.brave.server.query.Query
import org.bipmed.brave.server.query.QueryService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class QueryRestController(private val queryService: QueryService) {

    @PostMapping("/search")
    fun query(@RequestBody query: Query): QueryResponse {
        if (query.snpId != null || query.geneSymbol != null || (query.referenceName != null && query.start != null)) {
            return QueryResponse(queryService.search(query))
        } else {
            throw InvalidQuery()
        }
    }
} 