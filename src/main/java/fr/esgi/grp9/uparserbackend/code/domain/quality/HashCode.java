package fr.esgi.grp9.uparserbackend.code.domain.quality;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Hashtable;
import java.util.List;

@Data
@Builder
@Document(collection = "hash_codes")
public class HashCode {

    @Id
    private String id;

    @Field(value = "code_id")
    private String codeId;

    @Field(value = "fun_hash")
    private Hashtable<String, String> funHash;

    @Field(value = "lines_hash")
    private List<String> linesHash;

}
