package org.bipmed.brave.server.search

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@CrossOrigin
@RestController
class SearchController(private val searchService: SearchService) {

    @PostMapping("/search")
    fun search(@RequestBody input: SearchInput): SearchResponse {
        return searchService.search(input)
    }
}