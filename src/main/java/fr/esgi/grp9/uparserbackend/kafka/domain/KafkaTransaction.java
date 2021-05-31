package fr.esgi.grp9.uparserbackend.kafka.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaTransaction {
    @JsonProperty
    private String idRun;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String fileName;
    @JsonProperty
    private String fileContent;
    @JsonProperty
    private String code;
    @JsonProperty
    private Extension extensionEnd;
}
