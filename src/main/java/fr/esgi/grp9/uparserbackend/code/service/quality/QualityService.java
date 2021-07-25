package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.code.service.parser.PythonParser;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class QualityService implements IQualityService {
    private final CodeRepository codeQualityRepository;
    private final PythonParser pythonParser = new PythonParser();

    public QualityService(CodeRepository codeQualityRepository) {
        this.codeQualityRepository = codeQualityRepository;
    }

    @Override
    public Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException {
        // decode base64
        String decodeCode = this.decodeString(code.getCodeEncoded());
        // trim code
        String codeTrim = this.prepareCode(decodeCode);
        // hash code
        String hash = this.createMD5Hash(codeTrim);

        List<Code> codeExist = this.checkIfCodeExist(hash);
        if(codeExist.size() > 0) {
            code.setPlagiarism(true);
        }
        code.setHash(hash);
        return code;
    }

    private String decodeString(String s) {
        byte[] decodedBytes = Base64.getDecoder().decode(s);
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

    private List<Code> checkIfCodeExist(String hash) {
        return this.codeQualityRepository.findAllByHash(hash);
    }

    private void parseCode(String code) {
        // count lignes of code
        String[] lines = code.split("\r\n|\r|\n");
        System.out.println(lines.length);
    }

    @Override
    public String parseFile(KafkaTransaction kafkaTransaction) {
        String result = "";
        String[] _artifact = decodeString(kafkaTransaction.getInputfile()).split("\n");
        List<String> list = new ArrayList<>();
        for(String i: _artifact) {
            list.add(i);
        }
        if(kafkaTransaction.getLanguage().equals("python")) {
            //ajouter le switch
            result = this.pythonParser.csv_to_json(list);
        }
        return result;
    }

    @Override
    public Code testCodeQuality(Code code) {
        String decode = decodeString(code.getCodeEncoded());

        code.setCodeMark(10);
        return code;
    }
}
