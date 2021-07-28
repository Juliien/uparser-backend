package fr.esgi.grp9.uparserbackend.code.domain;

import com.mongodb.lang.NonNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@Document(collection = "codes")
public class Code {
    @Id
    private String id;
    @Field(value = "user_id")
    private String userId;
    @Field(value = "grade_id")
    private String gradeId;
    @Field(value = "code_encoded")
    private String codeEncoded;
    private String hash;
    @Field(value = "extension_start")
    private String extensionStart;
    @Field(value = "extension_end")
    private String extensionEnd;
    private String language;
    @Field(value = "code_mark")
    private int codeMark;
    private boolean isPlagiarism;
    @NonNull
    private boolean isEnable;
    private Date date;
}
