package fr.esgi.grp9.uparserbackend.file.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@Builder
@Document(collection = "files")
public class File {
    @Id
    private String id;
    @NonNull
    private String userId;
    @NonNull
    @Field(value = "file_name")
    private String fileName;
    @Field(value = "file_content")
    private String fileContent;
    @Field(value = "file_extension")
    private String fileExtension;
    @Field(value = "create_date")
    private Date createDate;
}
