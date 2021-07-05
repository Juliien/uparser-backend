package fr.esgi.grp9.uparserbackend.run.domain;

import fr.esgi.grp9.uparserbackend.exception.common.NotFoundWithIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RunServiceImpl implements RunService{
    private final RunRepository runRepository;

    @Autowired
    public RunServiceImpl(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    @Override
    public Run createRun(Run run) {
        return runRepository.save(
            Run.builder()
                .userEmail(run.getUserEmail())
                .codeId(run.getCodeId())
                .stdout(run.getStdout())
                .stderr(run.getStderr())
                .artifact(run.getArtifact())
                .stats(run.getStats())
                .creationDate(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public Run updateRun(Run run) {
        Run _run = runRepository.findById(run.getId()).orElseThrow(() -> new NotFoundWithIdException("Run", run.getId()));
        return runRepository.save(_run);
    }

    @Override
    public Run findRunById(String id) {
        return runRepository.findById(id).orElseThrow(() -> new NotFoundWithIdException("Run", id));
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
