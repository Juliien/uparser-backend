package fr.esgi.grp9.uparserbackend.code.quality.domain;

import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class CodeQualityServiceImpl {

    public Code testCode(Code code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code.getCodeEncoded());
        String decodedCode = new String(decodedBytes);
        System.out.println(decodedCode);
        Code result = Code.builder().codeEncoded(decodedCode).build();
        return result;
    }
}
