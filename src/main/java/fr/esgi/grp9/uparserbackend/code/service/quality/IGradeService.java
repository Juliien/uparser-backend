package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;

import java.util.List;
import java.util.Optional;

public interface IGradeService {
    Grade createGrade(Grade grade);
    Optional<Grade> findGradeById(String id);
    List<Grade> getGrades();
    Optional<List<Grade>> getGradesByRunId(String runId);
    Optional<List<Grade>> getGradesByCodeId(String codeId);
    void deleteGradeById(String id);
}
