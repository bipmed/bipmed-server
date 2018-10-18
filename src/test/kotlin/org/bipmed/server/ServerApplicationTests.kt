package org.bipmed.server

import org.assertj.core.api.Assertions.assertThat
import org.bipmed.server.api.QueryResponse
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
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import javax.annotation.PostConstruct

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServerApplicationTests {

    @LocalServerPort
    private var localPort: Int = 0

    @Autowired
    private lateinit var restTemplateBuilder: RestTemplateBuilder

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    private lateinit var client: RestTemplate

    private val variants = listOf(
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
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
                    referenceName = "20",
                    start = 1230237,
                    referenceBases = "T",
                    alternateBases = listOf("."),
                    sampleCount = 3
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    snpIds = listOf("microsat1"),
                    referenceName = "20",
                    start = 1234567,
                    referenceBases = "GTC",
                    alternateBases = listOf("G", "GTCT"),
                    sampleCount = 3
            )
    )

    @PostConstruct
    fun init() {
        client = restTemplateBuilder
                .rootUri("http://localhost:$localPort")
                .build()
    }

    @Before
    fun initDatabase() {
        variants.forEach { mongoTemplate.insert(it) }
    }

    @After
    fun removeDatabase() {
        mongoTemplate.dropCollection(Variant::class.java)
    }

    @Test
    fun query() {
        val queries = listOf(
                Query(snpId = "rs6054257"),
                Query(referenceName = "20", start = 1230237),
                Query(referenceName = "20", start = 1230237, end = 1234567),
                Query(geneSymbol = "DEFB125")
        )

        assertThat(queryVariant(queries[0]).single()).isEqualTo(variants.first())

        assertThat(queryVariant(queries[1]).single()).isEqualTo(variants[3])

        assertThat(queryVariant(queries[2])).isEqualTo(variants.subList(3, 5))

        assertThat(queryVariant(queries[3]).single()).isEqualTo(variants.first())

        with (mongoTemplate.findAll(Query::class.java)) {
            assertThat(this[0]).isEqualTo(queries[0].copy(variants = 1))
            assertThat(this[1]).isEqualTo(queries[1].copy(variants = 1))
            assertThat(this[2]).isEqualTo(queries[2].copy(variants = 2))
            assertThat(this[3]).isEqualTo(queries[3].copy(variants = 1))
        }
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
        return client.postForObject("/", query, QueryResponse::class.java)!!.variants
    }

}
