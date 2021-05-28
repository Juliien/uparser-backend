package fr.esgi.grp9.uparserbackend.kafka.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transaction {
    @JsonProperty
    private String id;
    @JsonProperty
    private String userId;
    @JsonProperty
    private String fileContent;
    @JsonProperty
    private String code;
}
