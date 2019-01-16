package org.bipmed.brave.server.search;

import lombok.Builder;
import lombok.Data;
import org.bipmed.brave.server.query.Query;

import java.util.List;

@Data
@Builder
public class SearchInput {

    @Builder.Default
    private Integer draw = 1;

    @Builder.Default
    private Integer start = 0;

    @Builder.Default
    private Integer length = 0;

    private List<Query> queries;
}
