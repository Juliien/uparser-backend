package fr.esgi.grp9.uparserbackend.run.domain;

import fr.esgi.grp9.uparserbackend.common.exception.NotFoundWithIdException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .fileId(run.getFileId())
                .creationDate(LocalDate.now())
                .build()
        );
    }

    @Override
    public Run modifyRun(Run run) {
        Run _run = runRepository.findById(run.getId()).orElseThrow(() -> new NotFoundWithIdException("Run", run.getId()));
        _run.setUserEmail(run.getUserEmail());
        _run.setFileId(run.getFileId());
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
