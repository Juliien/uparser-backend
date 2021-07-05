package fr.esgi.grp9.uparserbackend.run.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Hashtable;

@Data
@Builder
@Document(collection = "runs")
public class Run {
    @Id
    private String id;
    @Field(value = "user_email")
    private String userEmail;
    @Field(value = "code_id")
    private String codeId;
    @Field(value = "stdout")
    private String stdout;
    @Field(value = "stderr")
    private String stderr;
    @Field(value = "artifact")
    private String artifact;
    @Field(value = "stats")
    private Hashtable<String, String> stats;
    @Field(value = "creation_date")
    private LocalDateTime creationDate;
}