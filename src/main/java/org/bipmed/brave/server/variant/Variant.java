package org.bipmed.brave.server.variant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Data
@Builder
public class Variant {

    @JsonIgnore
    @Id
    private String id;
    private List<String> snpIds;
    private String datasetId;
    private String assemblyId;
    private Integer totalSamples;
    private String referenceName;
    private Long start;
    private String referenceBases;
    private List<String> alternateBases;
    private List<String> geneSymbol;
    private List<Float> alleleFrequency;
    private Long sampleCount;

    private Statistics coverage;
    private Statistics genotypeQuality;

    private String clnsig;
    private List<String> hgvs;
    private List<String> type;

    @Data
    class Statistics {

        private Integer min;
        private Float q25;
        private Float median;
        private Float q75;
        private Integer max;
        private Float mean;
    }
}
