package org.bipmed.brave.server.variant

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Variant(
        @JsonIgnore
        @Id
        val id: String? = null,
        val snpIds: List<String> = emptyList(),
        val datasetId: String,
        val assemblyId: String,
        val totalSamples: Int,
        val referenceName: String,
        val start: Long,
        val referenceBases: String,
        val alternateBases: List<String>,
        val geneSymbol: List<String>? = null,
        val alleleFrequency: List<Number> = emptyList(),
        val sampleCount: Long? = null,

        val coverage: Statistics? = null,
        val genotypeQuality: Statistics? = null,

        val clnsig: String? = null,
        val hgvs: List<String>? = null,
        val type: List<String>? = null
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