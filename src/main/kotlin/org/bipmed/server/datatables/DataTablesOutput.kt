package org.bipmed.server.datatables

import org.bipmed.server.variant.Variant

data class DataTablesOutput(
        val draw: Int? = null,
        val recordsTotal: Long? = null,
        val recordsFiltered: Long? = null,
        val error: String? = null,
        val data: List<Variant>? = null
)