package fr.esgi.grp9.uparserbackend.run.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Hashtable;

@Data
@Builder
public class RunRaw {

    public RunRaw(String run_id, String stdout, String stderr, String artifact, Hashtable<String, String> stats) {
        this.run_id = run_id;
        this.stdout = stdout;
        this.stderr = stderr;
        this.artifact = artifact;
        this.stats =  stats;
    }

    public RunRaw() {}

    @JsonProperty("run_id")
    private String run_id;
    @JsonProperty("stdout")
    private String stdout;
    @JsonProperty("stderr")
    private String stderr;
    @JsonProperty("artifact")
    private String artifact;
    @JsonProperty("stats")
    private Hashtable<String, String> stats;
}
