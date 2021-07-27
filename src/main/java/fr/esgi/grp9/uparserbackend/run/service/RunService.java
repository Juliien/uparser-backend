package fr.esgi.grp9.uparserbackend.run.service;

import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.run.domain.Run;
import fr.esgi.grp9.uparserbackend.run.domain.RunRepository;
import fr.esgi.grp9.uparserbackend.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RunService implements IRunService {
    private final RunRepository runRepository;
    private final UserRepository userRepository;
    private final CodeRepository codeRepository;

    @Autowired
    public RunService(RunRepository runRepository, UserRepository userRepository, CodeRepository codeRepository) {
        this.runRepository = runRepository;
        this.userRepository = userRepository;
        this.codeRepository = codeRepository;
    }

    @Override
    public Run createRun(Run run) {
        return runRepository.save(
            Run.builder()
                .userId(run.getUserId())
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
    public Optional<Run> findRunById(String id) {
        return this.runRepository.findById(id);
    }

    @Override
    public List<Run> getRuns() {
        return this.runRepository.findAll();
    }

    @Override
    public Optional<List<Run>> getRunsByUserId(String userId) {
        Optional<List<Run>> _runs = Optional.empty();
        if (this.userRepository.findById(userId).isEmpty()){
            return _runs;
        }
        return this.runRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<List<Run>> getRunsByCodeId(String codeId) {
        Optional<List<Run>> _runs = Optional.empty();
        if (this.codeRepository.findById(codeId).isEmpty()){
            return _runs;
        }
        return this.runRepository.findAllByCodeId(codeId);
    }

    @Override
    public void deleteRunById(String id) {
        this.runRepository.deleteById(id);
    }
}
