package fr.esgi.grp9.uparserbackend.code.quality.domain;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class CodeQualityServiceImpl implements CodeQualityService {
    private final CodeQualityRepository codeQualityRepository;

    public CodeQualityServiceImpl(CodeQualityRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    @Override
    public Code testCode(Code code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code.getCodeEncoded());
        String decodedCode = new String(decodedBytes);
        Code result = Code.builder().codeEncoded(decodedCode).userId(code.getUserId()).build();
        // check & hash
        boolean plagiat = this.checkCode(code);
        if(plagiat) {
            return null;
        }
        return this.codeQualityRepository.save(result);
    }

    private boolean checkCode(Code code) {
        return this.codeQualityRepository.exists(code.getCodeEncoded());
    }
}
