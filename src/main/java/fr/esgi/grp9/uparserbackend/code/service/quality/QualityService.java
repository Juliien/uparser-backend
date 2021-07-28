package fr.esgi.grp9.uparserbackend.code.service.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.code.domain.parser.ParserResponse;
import fr.esgi.grp9.uparserbackend.code.service.parser.PythonParser;
import fr.esgi.grp9.uparserbackend.kafka.domain.KafkaTransaction;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private String decodeString(String s) {
        byte[] decodedBytes = Base64.getDecoder().decode(s);
        return new String(decodedBytes);
    }

    private String prepareCode(String code) {
        //modify variable and function and argument
      /*String str = code.replace("(", " ( ");
        String str2 = str.replace(")"," ) ");
        String str3 = str2.replace("="," = ");
        String str4 = str3.replace(","," ,");
        String str5 = str4.replace("\""," \" ");
        String[] tab = str5.split(" ");
        List<String> abcd  = Arrays.asList(tab);
        int index1 = abcd.indexOf("(");
        int index2 = abcd.indexOf(")");
        System.out.println(index1);
        for (int i=0; i< tab.length; i++)
        {
            if (i > index1 && i < index2 && !tab[i].equals(",") && tab[index1-2].equals("def"))
                //System.out.println(tab[i]);
                tab[i] = "parameter";
            System.out.println(tab[i]);
            //if(tab[i].equals("if") || tab[i].equals("while") )
             //   tab[i+1] = "variable";
            if (tab[i].equals("def") ){
                tab[i + 1] = "function";

            }

            if (tab[i].equals("=") || tab[i].equals("!=") && !tab[i-2].equals(">") && !tab[i+2].equals(">") )
               // tab[i-2] = "variable";
            for (int j=0; j< tab.length; j++) {
                if (tab[j].equals(tab[i-2]))
                    tab[j] = "variable";
            }


        }

        StringJoiner preparedcode = new StringJoiner("");
        for (String c:tab)

            preparedcode.add(c);

        return String.valueOf(preparedcode);*/
        return null;
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
    public ParserResponse parseFile(KafkaTransaction k) throws JsonProcessingException, JSONException {
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

    @Override
    public Code testCodeQuality(Code code) {
        String decode = decodeString(code.getCodeEncoded());

        code.setCodeMark(10);
        return code;
    }
}
