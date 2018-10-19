package org.bipmed.server.query

import org.bipmed.server.datatables.DataTablesInput
import org.bipmed.server.datatables.DataTablesOutput
import org.bipmed.server.variant.Variant
import org.bipmed.server.variant.VariantRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Service

@Service
class QueryService(private val mongoTemplate: MongoTemplate, private val variantRepository: VariantRepository) {

    fun search(query: Query): List<Variant> {
        val mongoQuery = getMongoQuery(query)

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        mongoTemplate.insert(query.copy(variants = variants.size))

        return variants
    }

    fun search(input: DataTablesInput): DataTablesOutput {
        val mongoQuery = getMongoQuery(input.query)

        if (input.length > 0) {
            val pageable = PageRequest.of(input.start / input.length, input.length)
            mongoQuery.with(pageable)
        }

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        mongoTemplate.insert(input.query.copy(variants = variants.size))

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

    private fun getMongoQuery(query: Query): org.springframework.data.mongodb.core.query.Query {
        val mongoQuery = org.springframework.data.mongodb.core.query.Query()

        with(query) {
            if (assemblyId != null) {
                mongoQuery.addCriteria(where("assemblyId").`is`(assemblyId))
            }

            if (geneSymbol != null) {
                mongoQuery.addCriteria(where("geneSymbol").`is`(geneSymbol))
            }

            if (datasetId != null) {
                mongoQuery.addCriteria(where("datasetId").`is`(datasetId))
            }

            if (snpId != null) {
                mongoQuery.addCriteria(where("snpIds").all(snpId))
            }

            when {
                (referenceName != null && start != null && end != null) ->
                    mongoQuery.addCriteria(where("referenceName").`is`(referenceName)
                            .and("start").gte(start).lte(end))

                (referenceName != null && start != null) ->
                    mongoQuery.addCriteria(where("referenceName").`is`(referenceName)
                            .and("start").`is`(start))

                else -> {
                }
            }
        }

        return mongoQuery
    }
}