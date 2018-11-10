package org.bipmed.brave.server.query

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Query(
        val snpId: String? = null,
        val assemblyId: String? = null,
        val datasetId: String? = null,
        val referenceName: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val geneSymbol: String? = null
)