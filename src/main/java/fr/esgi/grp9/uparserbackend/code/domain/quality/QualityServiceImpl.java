package fr.esgi.grp9.uparserbackend.code.domain.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QualityServiceImpl implements QualityService {
    private final CodeRepository codeQualityRepository;

    public QualityServiceImpl(CodeRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    public List<Code> findAllCodes() {
        return this.codeQualityRepository.findAll();
    }

    public Optional<Code> findById(String id) {
        return this.codeQualityRepository.findById(id);
    }
    @Override
    public List<Code> getUserCodeHistory(String userId) {
        return this.codeQualityRepository.findAllByUserId(userId);
    }

    @Override
    public Code testCode(Code code) {
        // check copy code
        Code _code = this.checkCodeExist(code);
        if(_code != null && code.getUserId().equals(_code.getUserId())) {
            return null;
        }
        String userCode = this.decodeCode(code);
//        this.parseCode(userCode);
        code.setDate(new Date());
        return this.codeQualityRepository.save(code);
    }

    private Code checkCodeExist(Code code) {
            return this.codeQualityRepository.findByCodeEncoded(code.getCodeEncoded());
    }

    private String decodeCode(Code code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code.getCodeEncoded());
        return new String(decodedBytes);
    }

    private void parseCode(String code) {
        // count lignes of code
        String[] lines = code.split("\r\n|\r|\n");
        System.out.println(lines.length);
    }
}
