package fr.esgi.grp9.uparserbackend.code.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "codes")
public class Code {
    @Id
    private String id;
    private String userId;
    private String codeEncoded;
    private String extensionStart;
    private String extensionEnd;
    private String language;
    private Date date;
}
