package org.bipmed.server.variant

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Variant (
        val variantIds: List<String>? = null,
        val datasetId: String,
        val assemblyId: String,
        val referenceName: String,
        val start: Long,
        val referenceBases: String,
        val alternateBases: List<String>,
        val geneSymbol: String? = null,
        val alleleFrequency: Number,
        val sampleCount: Long
)