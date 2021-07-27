package fr.esgi.grp9.uparserbackend.code.service.keyfinder;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import lombok.Builder;

import java.util.*;

public class KeyFinderService {

    private final Code code;

    private String formattedCode;
    private List<String> arrayOfSingleWords = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWords = new ArrayList<>();

    private List<String> arrayOfVariables = new ArrayList<>();
    private List<String> arrayOfParams = new ArrayList<>();
    private List<String> arrayOfFunctionNames = new ArrayList<>();

    public Code getCode() {
        return code;
    }

    public String getFormattedCode() {
        return formattedCode;
    }

    public List<String> getArrayOfSingleWords() {
        return arrayOfSingleWords;
    }

    public List<List<String>> getArrayOfLinesOfSingleWords() {
        return arrayOfLinesOfSingleWords;
    }

    public List<String> getArrayOfVariables() {
        return arrayOfVariables;
    }

    public List<String> getArrayOfParams() {
        return arrayOfParams;
    }

    public List<String> getArrayOfFunctionNames() {
        return arrayOfFunctionNames;
    }

    public KeyFinderService(Code code) throws Exception {
        this.code = code;

        initFormattedCode();
        initArrayOfSingleWords();
        initArrayOfLinesOfSingleWords();
        initVariablesFromArrayOfSingleWord();
        initParamsFromArrayOfSingleWord();
        initFunctionNameFromArrayOfSingleWord();
        deleteDuplicationFromLists();
    }

    private String decodeCode(String code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code);
        return new String(decodedBytes);
    }

    private void initFormattedCode(){
        String begin = decodeCode(this.code.getCodeEncoded());
        String spaced = reformatStringsSpaces(begin);
        String formatted = reformatStringsEscapedChars(spaced);
        this.formattedCode = formatted;
    }

    private void initArrayOfLinesOfSingleWords(){
        List<List<String>> lines = new ArrayList<>();
        List<String> tempList = new ArrayList<>();
        List<String> strings = this.arrayOfSingleWords;

        for (String str: strings){
            if (!str.equals("\\n")){
                tempList.add(str);
            } else {
                lines.add(tempList);
                tempList = new ArrayList<>();
            }
        }
        this.arrayOfLinesOfSingleWords =  reformatLinesImportLess(lines);
    }

    private void initArrayOfSingleWords() throws Exception {
        List<String> tempList = Arrays.asList(this.formattedCode.split("\\s"));
        List<String> specificSpaceTrimmedList = trimSpecificSpacesFromArray(tempList);
        List<String> printValueLessList = reformatStringsPrint(specificSpaceTrimmedList, "PRINT_DATA");
        this.arrayOfSingleWords = printValueLessList;
    }

    private void initVariablesFromArrayOfSingleWord() {
        for (int start = 0; start < this.arrayOfSingleWords.size(); start++) {
            if (this.arrayOfSingleWords.get(start).contains("=")){
                if (!this.arrayOfSingleWords.get(start-1).contains("=") && !this.arrayOfSingleWords.get(start+1).contains("=")
                        && !this.arrayOfSingleWords.get(start-1).contains(">") && !this.arrayOfSingleWords.get(start-1).contains("<")
                        && !this.arrayOfSingleWords.get(start-1).contains("!")) {
                    this.arrayOfVariables.add(this.arrayOfSingleWords.get(start - 1).trim());
                }
            }
        }
    }

    private void initParamsFromArrayOfSingleWord() {
        for (int start = 0; start < this.arrayOfSingleWords.size(); start++) {
            if (this.arrayOfSingleWords.get(start).contains("def")){
                start = start + 3;
                boolean shouldContinue = true;
                while (shouldContinue){
                    if (this.arrayOfSingleWords.get(start).contains(")")){
                        shouldContinue = false;
                    } else if (!this.arrayOfSingleWords.get(start).contains(",")){
                        this.arrayOfParams.add(this.arrayOfSingleWords.get(start));
                    }
                    start++;
                }
            }
        }
    }

    private void initFunctionNameFromArrayOfSingleWord(){
        for (int start = 0; start < this.arrayOfSingleWords.size(); start++) {
            if (this.arrayOfSingleWords.get(start).contains("def")){
                this.arrayOfFunctionNames.add(this.arrayOfSingleWords.get(start+1));
            }
        }
    }

    private void deleteDuplicationFromLists() {
        this.arrayOfVariables = deleteDuplicationFromAList(this.arrayOfVariables);
        this.arrayOfParams = deleteDuplicationFromAList(this.arrayOfParams);
        this.arrayOfFunctionNames = deleteDuplicationFromAList(this.arrayOfFunctionNames);
    }

    private List<String> deleteDuplicationFromAList(List<String> strings){
        List<String> arrayOfStringDuplicationLess = new ArrayList<>();
        for (String str: strings) {
            if (!arrayOfStringDuplicationLess.contains(str)){
                arrayOfStringDuplicationLess.add(str);
            }
        }
        return arrayOfStringDuplicationLess;
    }

    private String reformatStringsSpaces(String decode) {
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
            decode = decode.replace(keyChar, " " + keyChar + " ");
        }
        return decode;
    }

    private String reformatStringsEscapedChars(String string){
        Hashtable<String, String> my_dict = new Hashtable<>();
        my_dict.put("\t", "t");
        my_dict.put("\n", "n");
        my_dict.put("\b", "b");
        my_dict.put("\r", "r");
        my_dict.put("\f", "f");

        for (Map.Entry<String, String> set:
                my_dict.entrySet()) {
            string = string.replace(set.getKey()," \\" + set.getValue() + " ");
        }
        return string;
    }

    private List<String> reformatStringsPrint(List<String> strings, String replaceWith) throws Exception {
        String print_data = replaceWith;
        List<String> finalList = new ArrayList<>();
        for (int i = 0; i < strings.size(); i++) {
            if(strings.get(i).equals("print")){
                try {
                    if(strings.get(i+1).equals("(")){
                        finalList.add(strings.get(i));
                        if(!strings.get(i+2).equals(")")){
                            i++;
                            int depth = 0;
                            boolean shouldContinue = true;
                            while (shouldContinue) {
                                if(strings.get(i).equals("(")){
                                    if (depth > 0){
                                        finalList.add(print_data);
                                    } else {
                                        finalList.add(strings.get(i));
                                    }
                                    depth++;
                                } else if (strings.get(i).equals(")")){
                                    depth--;
                                    if (depth > 0){
                                        finalList.add(print_data);
                                    } else {
                                        finalList.add(strings.get(i));
                                    }
                                } else {
                                    finalList.add(print_data);
                                }
                                i++;
                                if (depth == 0){
                                    shouldContinue = false;
                                }
                            }
                        }
                    } else {
                        throw new Exception("Print syntax Error");
                    }
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            } else {
                finalList.add(strings.get(i));
            }
        }
        return finalList;
    }

    private List<String> trimSpecificSpacesFromArray(List<String> strings){
        List<String> finalList = new ArrayList<>();
        finalList.add(strings.get(0));
        for (int i = 1; i < strings.size()-1; i++) {
            if (!((!strings.get(i-1).equals("") && !strings.get(i+1).equals("")) && strings.get(i).equals(""))){
                finalList.add(strings.get(i));
            }
        }
        return finalList;
    }

    private void debugDisplayOfArray(List<String> strings){
        for (int i = 1; i < strings.size()-1; i++) {
            System.out.println( i + " : " + strings.get(i));
        }
    }

    private List<List<String>> reformatLinesImportLess(List<List<String>> lines) {
        List<List<String>> finalLines = new ArrayList<>();
        for (List<String> line: lines){
            if (!line.contains("import") || !line.contains("from")){
                finalLines.add(line);
            }
        }
        return finalLines;
    }
    
    //TODO faire fonction qui enlever les commentaires

//    public String getArrayOfSingleWordsWithEscapeSequences(String string) {
//        List<String> escapeSequences = Arrays.asList("\t", "\n", "\b", "\r", "\f");
//        for (String escapeSequence: escapeSequences) {
//            string = string.replace(escapeSequence, "\\" + escapeSequence);
//        }
//        return string;
//    }

//    public static void main(String[] args) throws Exception {
//        Code code = Code.builder()
//                .language("python")
//                .extensionEnd("csv")
//                .extensionEnd("json")
//                .codeEncoded("IyBQeXRob24gcHJvZ3JhbSB0byBkaXNwbGF5IHRoZSBGaWJvbmFjY2kgc2VxdWVuY2UKCmRlZiByZWN1cl9maWJvICggbiApIDoKICAgaWYgbiA8PSAxOgogICAgICAgcmV0dXJuIG4KICAgZWxzZToKICAgICAgIHJldHVybihyZWN1cl9maWJvKG4tMSkgKyByZWN1cl9maWJvKG4tMikpCgpudGVybXMgPSAxMAoKIyBjaGVjayBpZiB0aGUgbnVtYmVyIG9mIHRlcm1zIGlzIHZhbGlkCmlmIG50ZXJtcyA8PSAwOgogICBwcmludCgiUGxlc2UgZW50ZXIgYSBwb3NpdGl2ZSBpbnRlZ2VyIikKZWxzZToKICAgcHJpbnQoIkZpYm9uYWNjaSBzZXF1ZW5jZToiKQogICBmb3IgaSBpbiByYW5nZShudGVybXMpOgogICAgICAgcHJpbnQocmVjdXJfZmlibyhpKSkK")
//                .build();
//
//        KeyFinderService keyFinderService = new KeyFinderService(code);
//
//        System.out.println(keyFinderService.decodeCode(code.getCodeEncoded()));
//        System.out.println("keyFinderService.formattedCode = " + keyFinderService.getFormattedCode());
//        System.out.println("keyFinderService.arrayOfSingleWords = " + keyFinderService.getArrayOfSingleWords());
//        System.out.println("keyFinderService.arrayOfLinesOfSingleWords = " + keyFinderService.getArrayOfLinesOfSingleWords());
//        System.out.println("keyFinderService.arrayOfVariables = " + keyFinderService.getArrayOfVariables());
//        System.out.println("keyFinderService.arrayOfParams = " + keyFinderService.getArrayOfParams());
//        System.out.println("keyFinderService.arrayOfFunctionNames = " + keyFinderService.getArrayOfFunctionNames());
//    }
}
