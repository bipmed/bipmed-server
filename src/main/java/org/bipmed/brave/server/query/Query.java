package org.bipmed.brave.server.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Query {

    private String snpId;
    private String assemblyId;
    private String datasetId;
    private String referenceName;
    private Long start;
    private Long end;
    private String geneSymbol;

}
