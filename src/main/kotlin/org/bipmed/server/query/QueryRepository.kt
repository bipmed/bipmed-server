package org.bipmed.server.query

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QueryRepository : MongoRepository<Query, String>