package fr.esgi.grp9.uparserbackend.code.domain;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public List<Code> getUserCodeHistory(String userId) {
        return this.codeRepository.findAllByUserId(userId);
    }

    public Code addCode(Code code) {
        return this.codeRepository.save(code);
    }

}
