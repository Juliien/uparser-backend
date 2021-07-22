package fr.esgi.grp9.uparserbackend.run.service;

import fr.esgi.grp9.uparserbackend.run.domain.Run;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

import java.util.List;
import java.util.Optional;

public interface IRunService {
    Run createRun(Run run);
    Optional<Run> findRunById(String id);
    List<Run> getRuns();
    void deleteRunById(String id);
}
