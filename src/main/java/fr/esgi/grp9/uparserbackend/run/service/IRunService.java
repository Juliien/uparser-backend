package fr.esgi.grp9.uparserbackend.run.service;

import fr.esgi.grp9.uparserbackend.run.domain.Run;

import java.util.List;
import java.util.Optional;

public interface    IRunService {
    Run createRun(Run run);
    Optional<Run> findRunById(String id);
    List<Run> getRuns();
    Optional<List<Run>> getRunsByUserId(String userId);
    Optional<List<Run>> getRunsByCodeId(String codeId);
    void deleteRunById(String id);
}
