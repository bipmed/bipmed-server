package org.bipmed.server.variant

import org.springframework.data.mongodb.repository.MongoRepository

interface VariantRepository : MongoRepository<Variant, String>