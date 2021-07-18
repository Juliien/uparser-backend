package fr.esgi.grp9.uparserbackend.code.domain.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;

import java.util.List;

public interface QualityService {
    Code testCode(Code code);
    List<Code> getUserCodeHistory(String userId);
}
