package org.bipmed.brave.server;

import org.bipmed.brave.server.query.Query;
import org.bipmed.brave.server.search.SearchInput;
import org.bipmed.brave.server.search.SearchResponse;
import org.bipmed.brave.server.variant.Variant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.Resources;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerApplicationTest {

    @LocalServerPort
    private Integer localPort;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    private RestTemplate client;

    @Value("${spring.security.user.password}")
    private String password;

    private List<Variant> variants = Arrays.asList(
            Variant.builder()
                    .datasetId("test")
                    .assemblyId("GCRh38")
                    .totalSamples(4)
                    .snpIds(singletonList("rs6054257"))
                    .referenceName("20")
                    .start(14370L)
                    .referenceBases("G")
                    .alternateBases(singletonList("A"))
                    .alleleFrequency(singletonList(0.5f))
                    .sampleCount(3L)
                    .geneSymbol(Arrays.asList("DEFB125", "DEFB126"))
                    .hgvs(singletonList("n.14370G>A"))
                    .build(),
            Variant.builder()
                    .datasetId("test")
                    .assemblyId("GCRh38")
                    .totalSamples(4)
                    .referenceName("20")
                    .start(17330L)
                    .referenceBases("A")
                    .alternateBases(singletonList("T"))
                    .alleleFrequency(singletonList(0.017f))
                    .sampleCount(3L)
                    .hgvs(singletonList("n.17330T>A"))
                    .build(),
            Variant.builder()
                    .datasetId("test")
                    .assemblyId("GCRh38")
                    .totalSamples(4)
                    .snpIds(singletonList("rs6040355"))
                    .referenceName("20")
                    .start(1110696L)
                    .referenceBases("A")
                    .alternateBases(Arrays.asList("G", "T"))
                    .alleleFrequency(Arrays.asList(0.333F, 0.667F))
                    .sampleCount(2L)
                    .geneSymbol(singletonList("TMEM74B"))
                    .build(),
            Variant.builder()
                    .datasetId("test")
                    .assemblyId("GCRh38")
                    .totalSamples(4)
                    .referenceName("20")
                    .start(1230237L)
                    .referenceBases("T")
                    .alternateBases(singletonList("."))
                    .sampleCount(3L)
                    .build(),
            Variant.builder()
                    .datasetId("test")
                    .assemblyId("GCRh38")
                    .totalSamples(4)
                    .snpIds(singletonList("microsat1"))
                    .referenceName("20")
                    .start(1234567L)
                    .referenceBases("GTC")
                    .alternateBases(Arrays.asList("G", "GTCT"))
                    .sampleCount(3L)
                    .build()
    );

    private List<Query> queries = Arrays.asList(
            Query.builder().snpId("rs6054257").build(),
            Query.builder().referenceName("20").start(1230237L).build(),
            Query.builder().referenceName("20").start(1230237L).end(1234567L).build(),
            Query.builder().geneSymbol("DEFB125").build()
    );

    private List<URI> variantUris;

    @Before
    public void init() {
        client = restTemplateBuilder
                .rootUri("http://localhost:" + localPort)
                .basicAuthorization("user", password)
                .build();

        variantUris = variants.stream()
                .map(variant -> client.postForLocation("/variants", variant))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @After
    public void tearDown() {
        variantUris.forEach(variant -> client.delete(variant));
    }

    @Test
    public void query() {
        assertThat(queryVariant(SearchInput.builder().queries(singletonList(queries.get(0))).build()).getData().get(0)).isEqualTo(getAllVariants().get(0));
        assertThat(queryVariant(SearchInput.builder().queries(singletonList(queries.get(1))).build()).getData().get(0)).isEqualTo(getAllVariants().get(3));
        assertThat(queryVariant(SearchInput.builder().queries(singletonList(queries.get(2))).build()).getData()).isEqualTo(getAllVariants().subList(3, 5));
        assertThat(queryVariant(SearchInput.builder().queries(singletonList(queries.get(3))).build()).getData().get(0)).isEqualTo(getAllVariants().get(0));
    }

    @Test
    public void queryPaging() {
        SearchInput input = SearchInput.builder()
                .draw(1)
                .start(0)
                .length(3)
                .queries(singletonList(Query.builder().referenceName("20").start(14370L).end(1230237L).build()))
                .build();

        SearchResponse output = queryVariant(input);

        assertThat(output.getRecordsTotal()).isEqualTo(5);
        assertThat(output.getRecordsFiltered()).isEqualTo(4);
        assertThat(output.getData()).hasSize(3);

        input.setDraw(2);
        input.setStart(3);
        output = queryVariant(input);

        assertThat(output.getRecordsTotal()).isEqualTo(5);
        assertThat(output.getRecordsFiltered()).isEqualTo(4);
        assertThat(output.getData()).hasSize(1);

        input = SearchInput.builder().queries(singletonList(new Query())).build();
        output = queryVariant(input);
        assertThat(output.getData()).hasSize(variants.size());
    }

    @Test
    public void multipleQueries() {
        SearchInput input = SearchInput.builder()
                .queries(Arrays.asList(
                        Query.builder().snpId("rs6040355").build(),
                        Query.builder().geneSymbol("DEFB125").build(),
                        Query.builder().referenceName("1").start(10L).end(20L).build()
                )).build();
        SearchResponse output = queryVariant(input);
        assertThat(output.getData()).hasSize(2);
    }

    private List<Variant> getAllVariants() {
        return new ArrayList<>(client.getForObject("/variants", VariantResources.class).getContent());
    }

    private SearchResponse queryVariant(SearchInput input) {
        return client.postForObject("/search", input, SearchResponse.class);
    }

}

class VariantResources extends Resources<Variant> {
}