package fr.esgi.grp9.uparserbackend.code.domain.history;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@Document(collection = "codes_histories")
public class CodeHistory {
    @Id
    private String id;
    @Field(value = "user_id")
    private String userId;
    @Field(value = "code_encoded")
    private String codeEncoded;
    private String language;
    private Date date;
}
