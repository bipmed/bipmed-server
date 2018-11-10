package org.bipmed.brave.server.search

import org.bipmed.brave.server.query.Query
import org.bipmed.brave.server.variant.Variant
import org.bipmed.brave.server.variant.VariantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service

@Service
class SearchService(private val mongoTemplate: MongoTemplate, private val variantRepository: VariantRepository) {

    fun search(input: SearchInput): SearchResponse {
        val mongoQuery = org.springframework.data.mongodb.core.query.Query()

        if (input.queries != null && !input.queries.isEmpty()) {
            val criteriaList = input.queries.map { getCriteria(it) }
            mongoQuery.addCriteria(Criteria().orOperator(*criteriaList.toTypedArray()))
        }

        if (input.length > 0) {
            val pageable = PageRequest.of(input.start / input.length, input.length)
            mongoQuery.with(pageable)
        }

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        return SearchResponse(
                draw = input.draw,
                recordsTotal = variantRepository.count(),
                recordsFiltered = mongoTemplate.count(mongoQuery, Variant::class.java),
                data = variants
        )
    }

    private fun getCriteria(query: Query): Criteria {
        val criteria = Criteria()

        with(query) {
            if (assemblyId != null) {
                criteria.and("assemblyId").`is`(assemblyId)
            }

            if (geneSymbol != null) {
                criteria.and("geneSymbol").`is`(geneSymbol)
            }

            if (datasetId != null) {
                criteria.and("datasetId").`is`(datasetId)
            }

            if (snpId != null) {
                criteria.and("snpIds").all(snpId)
            }

            if (referenceName != null && start != null && end != null) {
                criteria.and("referenceName").`is`(referenceName)
                        .and("start").gte(start).lte(end)
            } else if (referenceName != null && start != null) {
                criteria.and("referenceName").`is`(referenceName)
                        .and("start").`is`(start)
            } else {
            }
        }

        return criteria
    }
}