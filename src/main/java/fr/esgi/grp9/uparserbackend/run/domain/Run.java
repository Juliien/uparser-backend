package fr.esgi.grp9.uparserbackend.run.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@Builder
@Document(collection = "runs")
public class Run {
    @Id
    private String id;
    @Field(value = "user_email")
    private String userEmail;
    @Field(value = "file_id")
    private String fileId;
    @Field(value = "creation_date")
    private LocalDate creationDate;
}
