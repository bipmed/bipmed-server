package org.bipmed.server

import org.assertj.core.api.Assertions.assertThat
import org.bipmed.server.api.QueryResponse
import org.bipmed.server.datatables.DataTablesInput
import org.bipmed.server.datatables.DataTablesOutput
import org.bipmed.server.query.Query
import org.bipmed.server.variant.Variant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.hateoas.Resources
import java.net.URI

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerApplicationTests {

    @LocalServerPort
    private var localPort: Int = 0

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    private lateinit var client: RestTemplate

    private val variants = listOf(
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    totalSamples = 4,
                    snpIds = listOf("rs6054257"),
                    referenceName = "20",
                    start = 14370,
                    referenceBases = "G",
                    alternateBases = listOf("A"),
                    alleleFrequency = listOf(0.5),
                    sampleCount = 3,
                    geneSymbol = "DEFB125"
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    totalSamples = 4,
                    referenceName = "20",
                    start = 17330,
                    referenceBases = "A",
                    alternateBases = listOf("T"),
                    alleleFrequency = listOf(0.017),
                    sampleCount = 3
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    totalSamples = 4,
                    snpIds = listOf("rs6040355"),
                    referenceName = "20",
                    start = 1110696,
                    referenceBases = "A",
                    alternateBases = listOf("G", "T"),
                    alleleFrequency = listOf(0.333, 0.667),
                    sampleCount = 2,
                    geneSymbol = "TMEM74B "
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    totalSamples = 4,
                    referenceName = "20",
                    start = 1230237,
                    referenceBases = "T",
                    alternateBases = listOf("."),
                    sampleCount = 3
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    totalSamples = 4,
                    snpIds = listOf("microsat1"),
                    referenceName = "20",
                    start = 1234567,
                    referenceBases = "GTC",
                    alternateBases = listOf("G", "GTCT"),
                    sampleCount = 3
            )
    )

    private val queries = listOf(
            Query(snpId = "rs6054257"),
            Query(referenceName = "20", start = 1230237),
            Query(referenceName = "20", start = 1230237, end = 1234567),
            Query(geneSymbol = "DEFB125")
    )

    private lateinit var variantUris: List<URI>

    @Before
    fun init() {
        client = restTemplateBuilder
                .rootUri("http://localhost:$localPort")
                .build()

        variantUris = variants.mapNotNull { client.postForLocation("/variants", it) }
    }

    @After
    fun tearDown() {
        variantUris.forEach { client.delete(it) }
    }

    @Test
    fun query() {
        assertThat(queryVariant(queries[0]).single()).isEqualTo(getAllVariants().first())

        assertThat(queryVariant(queries[1]).single()).isEqualTo(getAllVariants()[3])

        assertThat(queryVariant(queries[2])).isEqualTo(getAllVariants().subList(3, 5))

        assertThat(queryVariant(queries[3]).single()).isEqualTo(getAllVariants().first())
    }

    private fun getAllVariants(): List<Variant> {
        return client.getForObject("/variants", VariantResources::class.java)!!.content.toList()
    }

    @Test
    fun queryPaging() {
        var input = DataTablesInput(
                draw = 1,
                start = 0,
                length = 3,
                queries = listOf(Query(referenceName = "20", start = 14370, end = 1230237))
        )

        var output = queryVariantPaging(input)

        assertThat(output.recordsTotal).isEqualTo(5)
        assertThat(output.recordsFiltered).isEqualTo(4)
        assertThat(output.data).hasSize(3)

        input = input.copy(draw = 2, start = 3)
        output = queryVariantPaging(input)

        assertThat(output.recordsTotal).isEqualTo(5)
        assertThat(output.recordsFiltered).isEqualTo(4)
        assertThat(output.data).hasSize(1)

        input = DataTablesInput(queries = listOf(Query()))
        output = queryVariantPaging(input)
        assertThat(output.data!!).hasSize(variants.size)
    }

    @Test
    fun multipleQueries() {
        val output = queryVariantPaging(DataTablesInput(
                queries = listOf(Query(snpId = "rs6040355"), Query(geneSymbol = "DEFB125"), Query(referenceName = "1", start = 10, end = 20
                ))))
        assertThat(output.data).hasSize(2)
    }

    @Test(expected = HttpClientErrorException::class)
    fun queryOnlyDatasetId() {
        queryVariant(Query(datasetId = "test"))
    }

    @Test(expected = HttpClientErrorException::class)
    fun queryOnlyAssemblyId() {
        queryVariant(Query(assemblyId = "GCRh38"))
    }

    private fun queryVariant(query: Query): List<Variant> {
        return client.postForObject("/search", query, QueryResponse::class.java)!!.variants
    }

    private fun queryVariantPaging(input: DataTablesInput): DataTablesOutput {
        return client.postForObject("/datatables", input, DataTablesOutput::class.java)!!
    }

    class VariantResources : Resources<Variant>()
}
