package fr.esgi.grp9.uparserbackend.code.quality.domain;

import java.util.List;

public interface CodeQualityService {
    Code testCode(Code code);
    List<Code> getUserCodeHistory(String userId);
}
