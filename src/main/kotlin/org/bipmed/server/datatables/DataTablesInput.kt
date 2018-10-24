package org.bipmed.server.datatables

import org.bipmed.server.query.Query

data class DataTablesInput(
        val draw: Int = 1,
        val start: Int = 0,
        val length: Int = -1,

        val data: List<Query>?
)