package fr.esgi.grp9.uparserbackend.code.service.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;
import fr.esgi.grp9.uparserbackend.code.domain.quality.GradeRepository;
import fr.esgi.grp9.uparserbackend.code.service.keyfinder.KeyFinderService;
import fr.esgi.grp9.uparserbackend.code.domain.parser.ParserResponse;
import fr.esgi.grp9.uparserbackend.code.service.parser.PythonParser;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class QualityService implements IQualityService {
    private final CodeRepository codeQualityRepository;
    private final GradeRepository gradeRepository;
    private final PythonParser pythonParser = new PythonParser();

    public QualityService(CodeRepository codeQualityRepository, GradeRepository gradeRepository) {
        this.codeQualityRepository = codeQualityRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException {
        // decode base64
        String decodeCode = this.decodeString(code.getCodeEncoded());
        // trim code
        // decodementer
//        String codeTrim = this.prepareCode(decodeCode);
        // hash code
        String hash = this.createMD5Hash(decodeCode);

        List<Code> codeExist = this.checkIfCodeExist(hash);
        if(codeExist.size() > 0) {
            code.setPlagiarism(true);
        }
        code.setHash(hash);
        return code;
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
    public ParserResponse parseFile(KafkaTransaction k) throws JsonProcessingException {
        String _result = "";
        String _artifact = decodeString(k.getInputfile());

        if(k.getLanguage().equals("python")) {
            if(k.getFrom().equals("json") && k.getTo().equals("csv")) {
                _result = this.pythonParser.json_to_csv(_artifact);
            }
            if(k.getFrom().equals("json") && k.getTo().equals("xml")) {
                _result = this.pythonParser.json_to_xml(_artifact);
            }
            if(k.getFrom().equals("xml") && k.getTo().equals("json")) {
                _result = this.pythonParser.xml_to_json(_artifact);
            }
        }
        return ParserResponse.builder().result(_result).build();
    }


    ////////////////////////////////////////////////////////////////

    private String decodeString(String s) {
        byte[] decodedBytes = Base64.getDecoder().decode(s);
        return new String(decodedBytes);
    }

    private String createMD5Hash(String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    @Override
    public Code testCodeQuality(Code code) {
        //TODO faire les traitements pour le grade
        //change le type de retour par un object grade avec 10 boolean
        //zero si le code ne compile
        //if isValid == false renvoie grade 0

        Grade grade = new Grade(new KeyFinderService(code));

        String gradeId = this.gradeRepository.save(grade).getId();

        code.setGradeId(gradeId);

        return code;
    }
}
