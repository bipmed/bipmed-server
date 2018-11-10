package org.bipmed.brave.server.api

import org.bipmed.brave.server.variant.Variant

data class QueryResponse(
        val variants: List<Variant>
)