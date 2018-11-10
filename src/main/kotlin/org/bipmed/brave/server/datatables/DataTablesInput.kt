package org.bipmed.brave.server.datatables

import org.bipmed.brave.server.query.Query

data class DataTablesInput(
        val draw: Int = 1,
        val start: Int = 0,
        val length: Int = -1,

        val queries: List<Query>?
)