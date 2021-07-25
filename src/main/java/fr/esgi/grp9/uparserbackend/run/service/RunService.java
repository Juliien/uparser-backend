package fr.esgi.grp9.uparserbackend.run.service;

import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RunService implements IRunService {
    private final RunRepository runRepository;

    @Autowired
    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    @Override
    public Run createRun(Run run) {
        return runRepository.save(
            Run.builder()
                .userId(run.getUserId())
                .codeId(run.getCodeId())
                .stdout(run.getStdout())
                .stderr(run.getStderr())
                .artifact(null)
                .stats(run.getStats())
                .creationDate(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public Optional<Run> findRunById(String id) {
        return runRepository.findById(id);
    }

    @Override
    public List<Run> getRuns() {
        return runRepository.findAll();
    }

    @Override
    public void deleteRunById(String id) {
        runRepository.deleteById(id);
    }
}