package org.bipmed.server.variant

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Variant (
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

        val minCov: Int? = null,
        val q25Cov: Number? = null,
        val medianCov: Number? = null,
        val q75Cov: Number? = null,
        val maxCov: Int? = null,
        val meanCov: Number? = null,

        val minGenQual: Int? = null,
        val q25GenQual: Number? = null,
        val medianGenQual: Number? = null,
        val q75GenQual: Number? = null,
        val maxGenQual: Int? = null,
        val meanGenQual: Number? = null,

        val clnsig: Int? = null
)