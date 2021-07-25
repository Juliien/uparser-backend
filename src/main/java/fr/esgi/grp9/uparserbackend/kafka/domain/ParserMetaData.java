package fr.esgi.grp9.uparserbackend.kafka.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParserMetaData {
    @JsonProperty
    private String topic;
    @JsonProperty
    private int partition;
    @JsonProperty
    private long baseOffset;
    @JsonProperty
    private long timestamp;
    @JsonProperty
    private int serializedKeySize;
    @JsonProperty
    private int serializedValueSize;
}
