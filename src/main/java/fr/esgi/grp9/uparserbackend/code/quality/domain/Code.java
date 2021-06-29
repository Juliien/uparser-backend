package fr.esgi.grp9.uparserbackend.code.quality.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "codes")
public class Code {
    @Id
    private String id;
    private String userId;
    private String codeEncoded;
}
