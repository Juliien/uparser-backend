package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import fr.esgi.grp9.uparserbackend.code.domain.quality.Grade;
import fr.esgi.grp9.uparserbackend.code.service.keyfinder.KeyFinderService;
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

    public String prepareCode(String code) {
        //modify variable and function and argument
        String str = code.replace("(", " ( ");
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

        return String.valueOf(preparedcode);
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
        //TODO faire les traitements pour le grade
        //change le type de retour par un object grade avec 10 boolean
        //zero si le code ne compile
        //if isValid == false renvoie grade 0
        KeyFinderService keyFinderService;

        try {
            keyFinderService = new KeyFinderService(code);
        } catch (Exception exception) {
            code.setCodeMark(0);
            return code;
        }

        Grade grade = new Grade(keyFinderService);


        code.setCodeMark(10);
        return code;
    }
}
