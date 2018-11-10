package org.bipmed.brave.server.search

import org.bipmed.brave.server.query.Query

data class SearchInput(
        val draw: Int = 1,
        val start: Int = 0,
        val length: Int = 0,

        val queries: List<Query>?
)