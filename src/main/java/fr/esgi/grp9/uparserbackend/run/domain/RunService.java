package fr.esgi.grp9.uparserbackend.run.domain;

import java.util.List;

public interface RunService {
    Run createRun(Run run);
    Run modifyRun(Run run);
    Run findRunById(String id);
    List<Run> getRuns();
    void deleteRunById(String id);
}
