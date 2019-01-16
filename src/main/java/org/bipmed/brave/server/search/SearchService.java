package org.bipmed.brave.server.search;

import org.bipmed.brave.server.query.Query;
import org.bipmed.brave.server.variant.Variant;
import org.bipmed.brave.server.variant.VariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final MongoTemplate mongoTemplate;
    private final VariantRepository variantRepository;

    @Autowired
    public SearchService(MongoTemplate mongoTemplate, VariantRepository variantRepository) {
        this.mongoTemplate = mongoTemplate;
        this.variantRepository = variantRepository;
    }

    SearchResponse search(SearchInput input) {
        org.springframework.data.mongodb.core.query.Query mongoQuery = new org.springframework.data.mongodb.core.query.Query();

        if (input.getQueries() != null && !input.getQueries().isEmpty()) {
            Criteria[] criteriaList = input.getQueries()
                    .stream()
                    .map(this::getCriteria)
                    .toArray(Criteria[]::new);
            mongoQuery.addCriteria(new Criteria().orOperator(criteriaList));
        }

        if (input.getLength() > 0) {
            PageRequest pageable = PageRequest.of(input.getStart() / input.getLength(), input.getLength());
            mongoQuery.with(pageable);
        }

        List<Variant> variants = mongoTemplate.find(mongoQuery, Variant.class);

        return new SearchResponse(
                input.getDraw(),
                variantRepository.count(),
                mongoTemplate.count(mongoQuery, Variant.class),
                null,
                variants
        );
    }

    private Criteria getCriteria(Query query) {
        Criteria criteria = new Criteria();

        if (query.getAssemblyId() != null) {
            criteria.and("assemblyId").is(query.getAssemblyId());
        }

        if (query.getGeneSymbol() != null) {
            criteria.and("geneSymbol").all(query.getGeneSymbol());
        }

        if (query.getDatasetId() != null) {
            criteria.and("datasetId").is(query.getDatasetId());
        }

        if (query.getSnpId() != null) {
            criteria.and("snpIds").all(query.getSnpId());
        }

        if (query.getReferenceName() != null && query.getStart() != null && query.getEnd() != null) {
            criteria.and("referenceName").is(query.getReferenceName()).and("start").gte(query.getStart()).lte(query.getEnd());
        } else if (query.getReferenceName() != null && query.getStart() != null) {
            criteria.and("referenceName").is(query.getReferenceName()).and("start").is(query.getStart());
        }

        return criteria;
    }
}
