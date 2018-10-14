package org.bipmed.server.variant

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Variant(
        val variantIds: List<String> = emptyList(),
        val datasetId: String,
        val assemblyId: String,
        val referenceName: String,
        val start: Long,
        val referenceBases: String,
        val alternateBases: List<String>,
        val geneSymbol: String? = null,
        val alleleFrequency: List<Number> = emptyList(),
        val sampleCount: Long? = null,

        val coverage: Statistics? = null,
        val genotypeQuality: Statistics? = null,

        val clnsig: String? = null
) {
    data class Statistics(
            val min: Int? = null,
            val q25: Number? = null,
            val median: Number? = null,
            val q75: Number? = null,
            val max: Int? = null,
            val mean: Number? = null
    )
}