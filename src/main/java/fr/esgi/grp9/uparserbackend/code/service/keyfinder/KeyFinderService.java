package fr.esgi.grp9.uparserbackend.code.service.keyfinder;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.*;

public class KeyFinderService {

    private final Code code;
    private final List<String> arrayOfSingleWords;

    public KeyFinderService(Code code) {
        this.code = code;
        this.arrayOfSingleWords = getFinalListOfWordsFromCode();
    }

    public static void main(String[] args) {
        Code code = Code.builder()
                .language("python")
                .extensionEnd("csv")
                .extensionEnd("json")
                .codeEncoded("IyBQeXRob24gcHJvZ3JhbSB0byBkaXNwbGF5IHRoZSBGaWJvbmFjY2kgc2VxdWVuY2UKZGVmIHJlY3VyX2ZpYm8obik6CiAgICAgICAgaWYgbiA8PSAxOgogICAgICAgIHJldHVybiBuCmVsc2U6CiAgICAgICByZXR1cm4ocmVjdXJfZmlibyhuLTEpICsgcmVjdXJfZmlibyhuLTIpKQoKbnRlcm1zID0gMTAKCiMgY2hlY2sgaWYgdGhlIG51bWJlciBvZiB0ZXJtcyBpcyB2YWxpZAppZiBudGVybXMgPD0gMDoKICAgIHByaW50KCJQbGVzZSBlbnRlciBhIHBvc2l0aXZlIGludGVnZXIiKQplbHNlOgogICAgcHJpbnQoIkZpYm9uYWNjaSBzZXF1ZW5jZToiKQogICAgZm9yIGkgaW4gcmFuZ2UobnRlcm1zKToKICAgICAgICBwcmludChyZWN1cl9maWJvKGkpKQog")
                .build();
//        new KeyFinderService(code).getFinalListOfWordsFromCode();
    }

    private String decodeCode(String code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code);
        return new String(decodedBytes);
    }

    private List<String> findFunctionNameFromArrayOfSingleWord(List<String> strings){
        List<String> arrayOfFunctionNames = new ArrayList<>();

        for (int start = 0; start < strings.size(); start++) {
            if (strings.get(start).contains("def")){
                arrayOfFunctionNames.add(strings.get(start+1));
            }
        }
        return arrayOfFunctionNames;
    }

    private List<String> findVariablesFromArrayOfSingleWord(List<String> strings) {
        List<String> arrayOfVariables = new ArrayList<>();

        for (int start = 0; start < strings.size(); start++) {
            if (strings.get(start).contains("=")){
                if (!strings.get(start-1).contains("=") && !strings.get(start+1).contains("=")
                        && !strings.get(start-1).contains(">") && !strings.get(start-1).contains("<")
                        && !strings.get(start-1).contains("!")) {
                    arrayOfVariables.add(strings.get(start - 1).trim());
                }
            }
        }
        return arrayOfVariables;
    }

    private List<String> findParamsFromArrayOfSingleWord(List<String> strings) {
        List<String> arrayOfParam = new ArrayList<>();

        for (int start = 0; start < strings.size(); start++) {
            if (strings.get(start).contains("def")){
                start = start + 3;
                boolean shouldContinue = true;
                while (shouldContinue){
                    if (strings.get(start).contains(")")){
                        shouldContinue = false;
                    } else if (!strings.get(start).contains(",")){
                        arrayOfParam.add(strings.get(start));
                    }
                    start++;
                }
            }
        }
        return arrayOfParam;
    }

    public List<String> deleteDuplicationFromList(List<String> strings){
        List<String> arrayOfStringDuplicationLess = new ArrayList<>();
        for (String str: strings) {
            if (!arrayOfStringDuplicationLess.contains(str)){
                arrayOfStringDuplicationLess.add(str);
            }
        }
        return arrayOfStringDuplicationLess;
    }

    private String reformatString(String decode) {
        List<String> keyChars = Arrays.asList("(", ")", "=", ",", "\"");
        for (String keyChar: keyChars) {
            boolean shouldContinue = true;
            while (shouldContinue) {
                if (decode.contains(" " + keyChar + " ")) {
                    decode = decode.replace(" " + keyChar + " ", "" + keyChar + "");
                } else if (decode.contains("" + keyChar + " ")) {
                    decode = decode.replace("" + keyChar + " ", "" + keyChar + "");
                } else if (decode.contains(" " + keyChar + "")) {
                    decode = decode.replace(" " + keyChar + "", "" + keyChar + "");
                } else {
                    shouldContinue = false;
                }
            }
        }
        return decode
                .replace("(", " ( ")
                .replace(")"," ) ")
                .replace("="," = ")
                .replace(","," , ")
                .replace("\""," \" ");
    }


    public List<String> getFinalListOfWordsFromCode() {
        String decode = decodeCode(this.code.getCodeEncoded());
        String str = reformatString(decode);
        return Arrays.asList(str.split(" "));

//        List<String> initialCodeArray = new ArrayList<>(Arrays.asList(decodeCode(this.code.getCodeEncoded()).split(" ")));
//
//        for (String possibleMultipleWords: initialCodeArray) {
//
//            //test si potentiel function
//            //si c'est le cas passer le string dans une fonction qui va le decouper et ajouter son contenu au tableau
//            //sinon tester presence variable puis decouper et ajouter si c'est le cas
//            if (possibleMultipleWords.contains("def") && !possibleMultipleWords.contains("\\:")) {
//                //faire la fonction que recup jusqu'a :
////            } else if (){
////
//            }
//            //contient def mais pas de :){
////            faire la fonction qui recupère tous jusqu'au colon
//
//            if (possibleMultipleWords.contains("def") || possibleMultipleWords.contains("\\(")
//                    || possibleMultipleWords.contains("\\)")  || possibleMultipleWords.contains("\\:")){
//                //ici faire la fonction qui cherche les params
//            } else if (possibleMultipleWords.contains("=")) {
//                //ici faire la fonction qui cherche les variables
//            } else {
//                //ajouter le mot complet au tableau
//            }
//
//            if(findVariableFromString(possibleMultipleWords).isPresent()){
//                arrayOfSingleWords.addAll(findVariableFromString(possibleMultipleWords).get());
//            }
//
//        }
//
//        for (int i = 0; i < initialCodeArray.size(); i++) {
//            //si le bloque de fonction n'est pas complet
//            if (initialCodeArray.get(i).contains("def") && !initialCodeArray.get(i).contains("\\:")) {
//                //ajoute les element separer dans le tableau
//                arrayOfSingleWords.addAll(getParamsFromArray(getEverythingTillColon(initialCodeArray, i)));
//                //met l'iterator après le bloque si contient le colon pour ne pas l'ajouter dans le tableau
//                i = setIteratorToColonPosition(initialCodeArray, i);
//            } else if (initialCodeArray.get(i).contains("def") && initialCodeArray.get(i).contains("\\:")){
////                arrayOfSingleWords.addAll()
//            } else {
//                arrayOfSingleWords.add(initialCodeArray.get(i));
//            }
//        }
    }


//    private List<String> findParamFromString(String str){
//        List<String> res = new ArrayList<>();
//        try{
//            str = str.split("\\(")[1];
//            str = str.split("\\)")[0];
//            for (String arg: str.split(",")) {
//                res.add(arg.trim());
//            }
//        } catch (Exception exception) {
//
//        }
//        return res;
//    }

//    private List<String> getParamsFromArray(List<String> strings) {
//        String all = StringUtils.join(strings);
//        return findParamFromString(all);
//    }

//    private int setIteratorToColonPosition(List<String> strings, int start) {
//        while(true){
//            if (strings.get(start+1).contains(":")){
//                break;
//            }
//            start++;
//        }
//        return start;
//    }

//    private List<String> getEverythingTillColon(List<String> strings, int start) {
//        List<String> res = new ArrayList<>();
//        while(true){
//            if (strings.get(start+1).contains(":")){
//                break;
//            }
//            res.add(strings.get(start));
//            start++;
//        }
//        return res;

//        strings.get(i).split("\\(")[1];
//        if(strings.get(i).contains("\\:") || strings.get(i).contains("\\)")) {
//
//            //ici recuperer les arguments
//        } else if (strings.get(i).contains("\\(")) {
//            //faire i
//        }
//
//
//            if(){
//                if(!strings.get(i).contains("\\(")){
//                    i++;
//                }
//            } else {
//                //ici on a pas la parenthèse fermante donc
//            }
//        } else {
//            //ici on a tous le bloque donc chercher tous les args
//        }
//
//        while(true){
//
//        }
//    }

//    private Optional<List<String>> findVariableFromString(String str){
//        Optional<List<String>> optVariablePlusEqualPlusValue = Optional.of(new ArrayList<>());
//        List<String> variablePlusEqualPlusValue = new ArrayList<>();
//        try {
//            str = str.trim();
//            String[] elements = str.split("=");
//            for (String element : elements) {
//                optVariablePlusEqualPlusValue.get().add(element);
//            }
//        } catch (Exception exception) {
//
//        }
//        return optVariablePlusEqualPlusValue;
//    }



}
