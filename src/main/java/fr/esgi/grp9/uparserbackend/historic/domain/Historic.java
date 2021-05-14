package fr.esgi.grp9.uparserbackend.historic.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "historic")
public class Historic {
    @Id
    private String id;
    private String language;
    private String from;
    private String to;
    private String code;
}
