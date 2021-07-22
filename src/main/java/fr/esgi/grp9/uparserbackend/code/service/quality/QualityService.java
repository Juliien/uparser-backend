package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
public class QualityService implements IQualityService {
    private final CodeRepository codeQualityRepository;

    public QualityService(CodeRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    @Override
    public Code testCode(Code code) {
        //TODO don't save code
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
