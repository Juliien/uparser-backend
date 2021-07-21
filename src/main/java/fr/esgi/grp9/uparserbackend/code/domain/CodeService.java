package fr.esgi.grp9.uparserbackend.code.domain;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeService {
    private final CodeRepository codeQualityRepository;

    public CodeService(CodeRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    public List<Code> getUserCodeHistory(String userId) {
        return this.codeQualityRepository.findAllByUserId(userId);
    }
}
