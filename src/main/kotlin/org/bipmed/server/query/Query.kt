package org.bipmed.server.query

data class Query (
        val variantId: String? = null,
        val assemblyId: String? = null,
        val datasetId: String? = null,
        val referenceName: String? = null,
        val start: Long? = null,
        val end: Long? = null,
        val geneSymbol: String? = null
)