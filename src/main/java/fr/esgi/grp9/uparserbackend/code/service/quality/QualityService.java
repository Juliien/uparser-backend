package fr.esgi.grp9.uparserbackend.code.service.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;
import fr.esgi.grp9.uparserbackend.code.domain.quality.GradeRepository;
import fr.esgi.grp9.uparserbackend.code.domain.quality.HashCode;
import fr.esgi.grp9.uparserbackend.code.domain.quality.HashCodeRepository;
import fr.esgi.grp9.uparserbackend.code.service.keyfinder.KeyFinderService;
import fr.esgi.grp9.uparserbackend.code.domain.parser.ParserResponse;
import fr.esgi.grp9.uparserbackend.code.service.parser.PythonParser;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class QualityService implements IQualityService {
    private final CodeRepository codeQualityRepository;
    private final GradeRepository gradeRepository;
    private final HashCodeRepository hashCodeRepository;
    private final PythonParser pythonParser = new PythonParser();

    public QualityService(CodeRepository codeQualityRepository, GradeRepository gradeRepository, HashCodeRepository hashCodeRepository) {
        this.codeQualityRepository = codeQualityRepository;
        this.gradeRepository = gradeRepository;
        this.hashCodeRepository = hashCodeRepository;
    }

    @Override
    public Code isCodePlagiarism(Code code) throws NoSuchAlgorithmException {
        KeyFinderService keyFinderService = new KeyFinderService(code);

        List<String> linesHash = new ArrayList<>();
        Hashtable<String, String> funHash = new Hashtable<>();

        for (List<String> line: keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism()){
            linesHash.add(createMD5Hash(line.toString()));
        }

        if (linesHash.size() == 0){
            code.setPlagiarism(false);
            return code;
        }

        for (Map.Entry<String, List<List<String>>> set:
                keyFinderService.getArrayOfFunctionBodyByNames().entrySet()) {
            funHash.put(set.getKey(), createMD5Hash(set.getValue().toString()));
        }

        HashCode hashCode = HashCode.builder()
                .codeId(code.getId())
                .linesHash(linesHash)
                .funHash(funHash)
                .build();

        List<HashCode> listOfHashCode = this.hashCodeRepository.findAll();

        if (listOfHashCode.size() == 0) {
            code.setPlagiarism(false);
            hashCodeRepository.save(hashCode);
            return code;
        }

        Integer numberOfSameLines = 0;
        Integer numberOfSameFunctions = 0;

        for (HashCode fetchedHashCode: listOfHashCode) {
            for (String hashLine : hashCode.getLinesHash()) {
                for (String fetchedHashLine: fetchedHashCode.getLinesHash()){
                    if (hashLine.equals(fetchedHashLine)){
                        numberOfSameLines++;
                        break;
                    }
                }
            }

            for (Map.Entry<String, String> funSet: hashCode.getFunHash().entrySet()) {
                for (Map.Entry<String, String> fetchedFunSet: fetchedHashCode.getFunHash().entrySet()) {
                    if (fetchedFunSet.getValue().equals(funSet.getValue())) {
                        numberOfSameFunctions++;
                        break;
                    }
                }
            }
        }

        float linesPlagiarismRate = 100 - ((((float) linesHash.size() - (float) numberOfSameLines)/(float) linesHash.size())) * 100;
        float funPlagiarismRate = 100 - ((((float) funHash.size() - (float) numberOfSameFunctions)/(float) funHash.size())) * 100;

        // if plagiarism is detected
        // 20.0 is the % limit of code that could be considered as plagarism without setting the isPlagarism to true
        if (linesPlagiarismRate < 20.0 || funPlagiarismRate < 20.0) {
            code.setPlagiarism(false);
            hashCodeRepository.save(hashCode);
        } else {
            code.setPlagiarism(true);
        }

        return code;
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

        Grade grade = new Grade(new KeyFinderService(code));

        String gradeId = this.gradeRepository.save(grade).getId();

        code.setCodeMark(grade.getAverage());

        code.setGradeId(gradeId);

        return code;
    }
}
