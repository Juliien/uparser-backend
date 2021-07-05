package fr.esgi.grp9.uparserbackend.run.domain;

import java.util.List;

public interface RunService {
    Run createRun(Run run);
    Run findRunById(String id);
    Run updateRun(Run run);
    List<Run> getRuns();
    void deleteRunById(String id);
}
