package fr.esgi.grp9.uparserbackend.code.domain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        code.setDate(new Date());
        return this.codeRepository.save(code);
    }

    public Code enableCodeToCatalog(Code code) {
        Optional<Code> _code = this.codeRepository.findById(code.getId());
        if(_code.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This code doesn't exist.");
        }
        code.setEnable(true);
        return this.codeRepository.save(code);
    }

}
