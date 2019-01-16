package org.bipmed.brave.server.variant;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface VariantRepository extends PagingAndSortingRepository<Variant, String> {
}
