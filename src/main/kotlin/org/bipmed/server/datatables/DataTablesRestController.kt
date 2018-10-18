package org.bipmed.server.datatables

import org.bipmed.server.api.QueryResponse
import org.bipmed.server.error.InvalidQuery
import org.bipmed.server.query.Query
import org.bipmed.server.query.QueryService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class DataTablesRestController(private val queryService: QueryService) {

    @PostMapping("/datatables")
    fun search(@RequestBody query: Query): DataTablesOutput {
        if (query.snpId != null || query.geneSymbol != null || (query.referenceName != null && query.start != null)) {
            val variants = queryService.query(query).map { variant ->
                variant.copy(snpIds = variant.snpIds.map { snpId ->
                    if (snpId.startsWith("rs")) {
                        "<a target='_blank' href='https://www.ncbi.nlm.nih.gov/snp/$snpId'>$snpId</a>"
                    } else {
                        snpId
                    }
                })
            }
            return DataTablesOutput(variants)
        } else {
            throw InvalidQuery()
        }
    }
}