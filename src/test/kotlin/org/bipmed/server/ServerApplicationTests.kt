package org.bipmed.server

import org.assertj.core.api.Assertions.assertThat
import org.bipmed.server.api.QueryResponse
import org.bipmed.server.query.Query
import org.bipmed.server.variant.Variant
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit4.SpringRunner
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
                    variantIds = listOf("rs6054257"),
                    referenceName = "20",
                    start = 14370,
                    referenceBases = "G",
                    alternateBases = listOf("A"),
                    alleleFrequency = 0.5,
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
                    alleleFrequency = 0.017,
                    sampleCount = 3
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    variantIds = listOf("rs6040355"),
                    referenceName = "20",
                    start = 1110696,
                    referenceBases = "A",
                    alternateBases = listOf("G", "T"),
                    alleleFrequency = 0.5,
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
                    alleleFrequency = 0.5,
                    sampleCount = 3
            ),
            Variant(
                    datasetId = "test",
                    assemblyId = "GCRh38",
                    variantIds = listOf("microsat1"),
                    referenceName = "20",
                    start = 1234567,
                    referenceBases = "GTC",
                    alternateBases = listOf("G", "GTCT"),
                    alleleFrequency = 0.5,
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

    @Test
    fun query() {
        assertThat(queryVariant(Query(variantId = "rs6054257")).single()).isEqualTo(variants.first())

        assertThat(queryVariant(Query(datasetId = "test"))).isEqualTo(variants)

        assertThat(queryVariant(Query(referenceName = "20", start = 1230237)).single()).isEqualTo(variants[3])

        assertThat(queryVariant(Query(referenceName = "20", start = 1230237, end = 1234567))).isEqualTo(variants.subList(3, 5))

        assertThat(queryVariant(Query(geneSymbol = "DEFB125")).single()).isEqualTo(variants.first())
    }

    private fun queryVariant(query: Query): List<Variant> {
        return client.postForObject("/", query, QueryResponse::class.java)!!.variants
    }

}
