package org.bipmed.brave.server.search

import org.bipmed.brave.server.variant.Variant

data class SearchResponse(
        val draw: Int? = null,
        val recordsTotal: Long? = null,
        val recordsFiltered: Long? = null,
        val error: String? = null,
        val data: List<Variant>? = null
)