package org.bipmed.server.datatables

import org.bipmed.server.query.Query

data class DataTablesInput(
        val draw: Int,
        val start: Int,
        val length: Int,

        val query: Query
)