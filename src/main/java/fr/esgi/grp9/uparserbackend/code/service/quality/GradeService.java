package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;
import fr.esgi.grp9.uparserbackend.code.domain.quality.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService implements IGradeService {
    private final GradeRepository gradeRepository;

    @Autowired
    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public Grade createGrade(Grade grade) {
        return this.gradeRepository.save(grade);
    }

    @Override
    public Optional<Grade> findGradeById(String id) {
        return this.gradeRepository.findById(id);
    }

    @Override
    public List<Grade> getGrades() {
        return this.gradeRepository.findAll();
    }

    @Override
    public Optional<List<Grade>> getGradesByRunId(String runId) {
        return this.gradeRepository.findAllByRunId(runId);
    }

    @Override
    public Optional<List<Grade>> getGradesByCodeId(String codeId) {
        return this.gradeRepository.findAllByCodeId(codeId);
    }

    @Override
    public void deleteGradeById(String id) {
        this.gradeRepository.deleteById(id);
    }
}
