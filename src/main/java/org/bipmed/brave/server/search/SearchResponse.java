package org.bipmed.brave.server.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bipmed.brave.server.variant.Variant;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private Integer draw;
    private Long recordsTotal;
    private Long recordsFiltered;
    private String error;
    private List<Variant> data;
}
