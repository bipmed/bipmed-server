package org.bipmed.brave.server.variant

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource
interface VariantRepository : MongoRepository<Variant, String>