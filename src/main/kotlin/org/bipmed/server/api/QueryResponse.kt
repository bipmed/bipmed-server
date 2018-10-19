package org.bipmed.server.api

import org.bipmed.server.variant.Variant

data class QueryResponse(
        val variants: List<Variant>
)