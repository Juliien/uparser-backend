package fr.esgi.grp9.uparserbackend.kafka.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class KafkaTransaction {

    public KafkaTransaction() {
    }

    public KafkaTransaction(String runId, String userId, String fileName, String fileContent, String code, Extension extensionEnd) {
        this.runId = runId;
        this.userId = userId;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.code = code;
        this.extensionEnd = extensionEnd;
    }

    @Id
    private String runId;
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
