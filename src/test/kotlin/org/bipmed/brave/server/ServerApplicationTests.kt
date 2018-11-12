package org.bipmed.brave.server

import org.assertj.core.api.Assertions.assertThat
import org.bipmed.brave.server.query.Query
import org.bipmed.brave.server.search.SearchInput
import org.bipmed.brave.server.search.SearchResponse
import org.bipmed.brave.server.variant.Variant
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.env.Environment
import org.springframework.hateoas.Resources
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import java.net.URI

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerApplicationTests {

    @LocalServerPort
    private var localPort: Int = 0

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    private lateinit var client: RestTemplate

    @Value("\${spring.security.user.password}")
    private val password = ""

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
                    geneSymbol = listOf("DEFB125"),
                    hgvs = listOf("n.14370G>A")
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
                    sampleCount = 3,
                    hgvs = listOf("n.17330T>A")
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
                    geneSymbol = listOf("TMEM74B")
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
                .basicAuthorization("user", password)
                .build()

        variantUris = variants.mapNotNull { client.postForLocation("/variants", it) }
    }

    @After
    fun tearDown() {
        variantUris.forEach { client.delete(it) }
    }

    @Test
    fun query() {
        assertThat(queryVariant(SearchInput(queries = listOf(queries[0]))).data!!.single()).isEqualTo(getAllVariants().first())

        assertThat(queryVariant(SearchInput(queries = listOf(queries[1]))).data!!.single()).isEqualTo(getAllVariants()[3])

        assertThat(queryVariant(SearchInput(queries = listOf(queries[2]))).data!!).isEqualTo(getAllVariants().subList(3, 5))

        assertThat(queryVariant(SearchInput(queries = listOf(queries[3]))).data!!.single()).isEqualTo(getAllVariants().first())
    }

    private fun getAllVariants(): List<Variant> {
        return client.getForObject("/variants", VariantResources::class.java)!!.content.toList()
    }

    @Test
    fun queryPaging() {
        var input = SearchInput(
                draw = 1,
                start = 0,
                length = 3,
                queries = listOf(Query(referenceName = "20", start = 14370, end = 1230237))
        )

        var output = queryVariant(input)

        assertThat(output.recordsTotal).isEqualTo(5)
        assertThat(output.recordsFiltered).isEqualTo(4)
        assertThat(output.data).hasSize(3)

        input = input.copy(draw = 2, start = 3)
        output = queryVariant(input)

        assertThat(output.recordsTotal).isEqualTo(5)
        assertThat(output.recordsFiltered).isEqualTo(4)
        assertThat(output.data).hasSize(1)

        input = SearchInput(queries = listOf(Query()))
        output = queryVariant(input)
        assertThat(output.data!!).hasSize(variants.size)
    }

    @Test
    fun multipleQueries() {
        val output = queryVariant(SearchInput(
                queries = listOf(Query(snpId = "rs6040355"), Query(geneSymbol = "DEFB125"), Query(referenceName = "1", start = 10, end = 20
                ))))
        assertThat(output.data).hasSize(2)
    }

    private fun queryVariant(input: SearchInput): SearchResponse {
        return client.postForObject("/search", input, SearchResponse::class.java)!!
    }

    class VariantResources : Resources<Variant>()
}
