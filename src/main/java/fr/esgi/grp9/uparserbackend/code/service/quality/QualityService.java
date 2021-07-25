package fr.esgi.grp9.uparserbackend.code.service.quality;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.domain.CodeRepository;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.ArrayUtils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    private Optional<Code> checkIfCodeExist(String hash) {
        return this.codeQualityRepository.findByHash(hash);
    }

    private void parseCode(String code) {
        // count lignes of code
        String[] lines = code.split("\r\n|\r|\n");
        System.out.println(lines.length);
    }
}
