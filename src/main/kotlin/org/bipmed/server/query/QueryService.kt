package org.bipmed.server.query

import org.bipmed.server.datatables.DataTablesInput
import org.bipmed.server.datatables.DataTablesOutput
import org.bipmed.server.variant.Variant
import org.bipmed.server.variant.VariantRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.stereotype.Service

@Service
class QueryService(private val mongoTemplate: MongoTemplate, private val variantRepository: VariantRepository) {

    fun search(query: Query): List<Variant> {
        val mongoQuery = org.springframework.data.mongodb.core.query.Query()
        mongoQuery.addCriteria(getCriteria(query))

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        mongoTemplate.insert(query)

        return variants
    }

    fun search(input: DataTablesInput): DataTablesOutput {
        val mongoQuery = org.springframework.data.mongodb.core.query.Query()

        if (input.queries == null || input.queries.isEmpty()) {

        } else if (input.queries.size == 1) {
            mongoQuery.addCriteria(getCriteria(input.queries.single()))
        } else {
            val criteriaList = input.queries.map {
                getCriteria(it)
            }
            mongoQuery.addCriteria(Criteria().orOperator(*criteriaList.toTypedArray()))
        }

        if (input.length > 0) {
            val pageable = PageRequest.of(input.start / input.length, input.length)
            mongoQuery.with(pageable)
        }

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        if (input.draw == 0 && input.queries != null) {
            input.queries.forEach {
                mongoTemplate.insert(it)
            }
        }

        val data = variants.map { variant ->
            variant.copy(snpIds = variant.snpIds.map { snpId ->
                if (snpId.startsWith("rs")) {
                    "<a target='_blank' href='https://www.ncbi.nlm.nih.gov/snp/$snpId'>$snpId</a>"
                } else {
                    snpId
                }
            })
        }

        return DataTablesOutput(
                draw = input.draw,
                recordsTotal = variantRepository.count(),
                recordsFiltered = mongoTemplate.count(mongoQuery, Variant::class.java),
                data = data
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