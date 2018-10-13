package org.bipmed.server.query

import org.bipmed.server.variant.Variant
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.stereotype.Service

@Service
class QueryService(private val mongoTemplate: MongoTemplate) {

    fun query(query: Query): List<Variant> {
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

            if (variantId != null) {
                mongoQuery.addCriteria(where("variantIds").all(variantId))
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

        val variants = mongoTemplate.find(mongoQuery, Variant::class.java)

        mongoTemplate.insert(query.copy(variants = variants.size))

        return variants
    }
}