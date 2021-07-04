package fr.esgi.grp9.uparserbackend.kafka.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class KafkaTransaction {

    public KafkaTransaction(String id, String inputfile, String from, String to, String algorithm, String language) {
        this.id = id;
        this.inputfile = inputfile;
        this.from = from;
        this.to = to;
        this.algorithm = algorithm;
        this.language = language;
    }

    @Id
    private String id;
    private String inputfile;
    private String from;
    private String to;
    private String algorithm;
    private String language;
}
