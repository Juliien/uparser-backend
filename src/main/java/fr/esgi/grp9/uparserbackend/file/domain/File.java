package fr.esgi.grp9.uparserbackend.file.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@Document(collection = "files")
public class File {
    @Id
    private String id;
    @NonNull
    @Field(value = "filename")
    private String fileName;
    @NonNull
    @Field(value = "filepath")
    private String filePath;
    @Field(value = "creation_date")
    private LocalDate creationDate;
    @Field(value = "run_date")
    private Date runDate;
}
