package fr.esgi.grp9.uparserbackend.run.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Hashtable;

@Data
@Builder
@Document(collection = "runs")
public class Run {

    public Run(String id, String userId, String codeId, String stdout, String stderr, String artifact, Hashtable<String, String> stats, LocalDateTime creationDate) {
        this.id = id;
        this.userId = userId;
        this.codeId = codeId;
        this.stdout = stdout;
        this.stderr = stderr;
        this.artifact = artifact;
        this.stats = stats;
        this.creationDate = creationDate;
    }

    @Id
    private String id;
    @NonNull
    @Field(value = "user_id")
    private String userId;
    @Field(value = "code_id")
    private String codeId;
    @NonNull
    @Field(value = "stdout")
    private String stdout;
    @NonNull
    @Field(value = "stderr")
    private String stderr;
    @Field(value = "artifact")
    private String artifact;
//    @NonNull
    @Field(value = "stats")
    private Hashtable<String, String> stats;
    @Field(value = "creation_date")
    private LocalDateTime creationDate;
}