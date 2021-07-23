package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

@Service
public class QualityService implements IQualityService {
    private final CodeRepository codeQualityRepository;

    public QualityService(CodeRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    @Override
    public Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException {
        // decode base64
        String decodeCode = this.decodeCode(code.getCodeEncoded());
        // trim code
        String codeTrim = this.prepareCode(decodeCode);
        // hash code
        String hash = this.createMD5Hash(codeTrim);

        Optional<Code> codeExist = this.checkIfCodeExist(hash);
        if(codeExist.isPresent()) {
            code.setPlagiarism(true);
        }
        code.setHash(hash);
        return code;
    }

    private String decodeCode(String code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code);
        return new String(decodedBytes);
    }

    private String prepareCode(String code) {
        //modify variable and function and argument
        return code.replaceAll("\\s+","");
    }

    private String createMD5Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    private Optional<Code> checkIfCodeExist(String hash) {
        return this.codeQualityRepository.findByHash(hash);
    }

    private void parseCode(String code) {
        // count lignes of code
        String[] lines = code.split("\r\n|\r|\n");
        System.out.println(lines.length);
    }
}
