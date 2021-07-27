package fr.esgi.grp9.uparserbackend.code.service.keyfinder;

import fr.esgi.grp9.uparserbackend.code.domain.Code;
import lombok.Builder;

import java.util.*;

public class KeyFinderService {

    private final Code code;

    private String formattedCode;
    private List<String> arrayOfSingleWords = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWords = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWordsForPlagiarism = new ArrayList<>();
    private List<String> arrayOfSingleWordsForPlagiarism = new ArrayList<>();

    private List<String> arrayOfVariables = new ArrayList<>();
    private List<String> arrayOfParams = new ArrayList<>();
    private List<String> arrayOfFunctionNames = new ArrayList<>();

    private Hashtable<String, List<List<String>>> arrayOfFunctionBodyByNames = new Hashtable<>();

    public KeyFinderService(Code code) throws Exception {
        this.code = code;

        initFormattedCode();

        initArrayOfSingleWords();
        initArrayOfLinesOfSingleWords();
        initArrayOfLinesOfSingleWordsForPlagiarism();
        initArrayOfSingleWordsForPlagiarism();

        initVariablesFromArrayOfSingleWord();
        initParamsFromArrayOfSingleWord();
        initFunctionNameFromArrayOfSingleWord();
        initArrayOfFunctionBodyByNames();

        deleteDuplicationFromLists();
    }

    public Code getCode() {
        return code;
    }

    public List<List<String>> getArrayOfLinesOfSingleWordsForPlagiarism() {
        return arrayOfLinesOfSingleWordsForPlagiarism;
    }

    public List<String> getArrayOfSingleWordsForPlagiarism() {
        return arrayOfSingleWordsForPlagiarism;
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

    public Hashtable<String, List<List<String>>> getArrayOfFunctionBodyByNames() {
        return arrayOfFunctionBodyByNames;
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

    private void initArrayOfSingleWords() throws Exception {
        List<String> tempList = Arrays.asList(this.formattedCode.split("\\s"));
        List<String> specificSpaceTrimmedList = trimSpecificSpacesFromArray(tempList);
        this.arrayOfSingleWords = specificSpaceTrimmedList;
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

    private void initArrayOfLinesOfSingleWordsForPlagiarism(){
        List<List<String>> lines = this.arrayOfLinesOfSingleWords;
        lines = reformatLinesCommentLess(lines);
        lines = reformatLinesImportLess(lines);

        this.arrayOfLinesOfSingleWordsForPlagiarism = lines;
    }

    private void initArrayOfSingleWordsForPlagiarism() throws Exception {
        List<String> tempList = arrayOfLinesToArrayOfSingleWords(this.arrayOfLinesOfSingleWordsForPlagiarism);
        List<String> printValueLessList = reformatStringsPrint(tempList, "PRINT_DATA");
        this.arrayOfSingleWordsForPlagiarism = printValueLessList;
    }

    private List<String> arrayOfLinesToArrayOfSingleWords(List<List<String>> lines){
        List<String> finalList = new ArrayList<>();
        for (List<String> strings: lines){
            for (String str: strings){
                finalList.add(str);
            }
            finalList.add("\\n");
        }
        return finalList;
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

    private void initArrayOfFunctionBodyByNames() {
        List<List<String>> target = this.arrayOfLinesOfSingleWordsForPlagiarism;
        Hashtable<String, List<List<String>>> funNameAndBody = new Hashtable<>();
        for (String funName: this.getArrayOfFunctionNames()){
            List<List<String>> funBody = new ArrayList<>();
            for (int i = 0; i < target.size(); i++) {
                if (target.get(i).contains(funName) && target.get(i).contains("def")){
                    int funIndentation = indentationLevelOfALine(target.get(i));
                    funBody.add(target.get(i));
                    int y = i + 1;
                    while(indentationLevelOfALine(target.get(y)) != funIndentation){
                        funBody.add(target.get(y));
                        y++;
                    }
                }
            }
            funNameAndBody.put(funName, funBody);
        }
        this.arrayOfFunctionBodyByNames = funNameAndBody;
    }

    private int indentationLevelOfALine(List<String> line){
        if (line.size() == 0){
            return 0;
        }
        int level = 0;
        while(line.get(level).equals("")){
            level++;
        }
        return level;
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
//                    if(strings.get(i+1).equals("(")){
                        finalList.add(strings.get(i));
//                        if(!strings.get(i+2).equals(")")){
                            i++;
                            int depth = 0;
                            boolean shouldContinue = true;
//                            while (shouldContinue) {
                            while (!strings.get(i).equals("\\n")) {
//                                if(strings.get(i).equals("(")){
//                                    if (depth > 0){
//                                        finalList.add(print_data);
//                                    } else {
//                                        finalList.add(strings.get(i));
//                                    }
//                                    depth++;
//                                } else if (strings.get(i).equals(")")){
//                                    depth--;
//                                    if (depth > 0){
//                                        finalList.add(print_data);
//                                    } else {
//                                        finalList.add(strings.get(i));
//                                    }
//                                } else {
//                                    finalList.add(print_data);
//                                }
//                                i++;
//                                if (depth == 0){
//                                    shouldContinue = false;
//                                }

                                finalList.add(print_data);
                                i++;
                            }
                            finalList.add(strings.get(i));
//                        }
//                    } else {
//                        int y = i;
//                        List<String> details = new ArrayList<>();
//                        while (!strings.get(y).equals("\\n")){
//                            System.out.println(strings.get(y));
//                            details.add(strings.get(y));
//                            y++;
//                        }
//                        throw new Exception("Print syntax Error" + details.toString());
//                    }
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
        System.out.print(" -- ligne : ");
        for (int i = 1; i < strings.size()-1; i++) {
            System.out.print(strings.get(i));
        }
        System.out.println();
    }

    private void debugDisplayOfHashTable(Hashtable<String, List<List<String>>> hashtable){
        for (Map.Entry<String, List<List<String>>> set:
                hashtable.entrySet()) {
            System.out.println(set.getKey() + " :");
            for (List<String> line: set.getValue()){
                debugDisplayOfArray(line);
            }
            System.out.println();
        }
    }

    private List<List<String>> reformatLinesImportLess(List<List<String>> lines) {
        List<List<String>> finalLines = new ArrayList<>();
        for (List<String> line: lines){
            if (!line.contains("import") && !line.contains("from")){
                finalLines.add(line);
            }
        }
        return finalLines;
    }

    private List<List<String>> reformatLinesCommentLess(List<List<String>> lines) {
        List<List<String>> finalLines = new ArrayList<>();
        for (List<String> line: lines){
            if (line.size() > 0){
                if (!line.get(0).startsWith("#")){
                    finalLines.add(line);
                }
            } else {
                finalLines.add(line);
            }
        }
        return finalLines;
    }

//    public String getArrayOfSingleWordsWithEscapeSequences(String string) {
//        List<String> escapeSequences = Arrays.asList("\t", "\n", "\b", "\r", "\f");
//        for (String escapeSequence: escapeSequences) {
//            string = string.replace(escapeSequence, "\\" + escapeSequence);
//        }
//        return string;
//    }

    public static void main(String[] args) throws Exception {
        Code code = Code.builder()
                .language("python")
                .extensionEnd("csv")
                .extensionEnd("json")
                .codeEncoded("IyEvdXNyL2Jpbi9lbnYgcHl0aG9uMjcKaW1wb3J0IHVybGxpYjIKaW1wb3J0IGJhc2U2NAppbXBvcnQganNvbgppbXBvcnQgb3MKaW1wb3J0IHN5cwppbXBvcnQgcmUKCm9zLnN5c3RlbSgiY2xlYXIiKQpwcmludCAiLSIgKiA4MApwcmludCAiQ29tbWFuZCBMaW5lIFNlYXJjaCBUb29sIgpwcmludCAiLSIgKiA4MAoKZGVmIEJhbm5lcih0ZXh0KToKICAgIHByaW50ICI9IiAqIDcwCiAgICBwcmludCB0ZXh0CiAgICBwcmludCAiPSIgKiA3MAogICAgc3lzLnN0ZG91dC5mbHVzaCgpCgpkZWYgc29ydEJ5Vm90ZXMoKToKICAgIEJhbm5lcignU29ydCBCeSBWb3RlcycpCiAgICB1cmwgPSAiaHR0cDovL3d3dy5jb21tYW5kbGluZWZ1LmNvbS9jb21tYW5kcy9icm93c2Uvc29ydC1ieS12b3Rlcy9qc29uIgogICAgcmVxdWVzdCA9IHVybGxpYjIuUmVxdWVzdCh1cmwpCiAgICByZXNwb25zZSA9IGpzb24ubG9hZCh1cmxsaWIyLnVybG9wZW4ocmVxdWVzdCkpCiAgICAjcHJpbnQganNvbi5kdW1wcyhyZXNwb25zZSxpbmRlbnQ9MikKICAgIGZvciBjIGluIHJlc3BvbnNlOgogICAgICAgIHByaW50ICItIiAqIDYwCiAgICAgICAgcHJpbnQgY1snY29tbWFuZCddCgpkZWYgc29ydEJ5Vm90ZXNUb2RheSgpOgogICAgQmFubmVyKCdQcmludGluZyBBbGwgY29tbWFuZHMgdGhlIGxhc3QgZGF5IChTb3J0IEJ5IFZvdGVzKSAnKQogICAgdXJsID0gImh0dHA6Ly93d3cuY29tbWFuZGxpbmVmdS5jb20vY29tbWFuZHMvYnJvd3NlL2xhc3QtZGF5L3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlWb3Rlc1dlZWsoKToKICAgIEJhbm5lcignUHJpbnRpbmcgQWxsIGNvbW1hbmRzIHRoZSBsYXN0IHdlZWsgKFNvcnQgQnkgVm90ZXMpICcpCiAgICB1cmwgPSAiaHR0cDovL3d3dy5jb21tYW5kbGluZWZ1LmNvbS9jb21tYW5kcy9icm93c2UvbGFzdC13ZWVrL3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlWb3Rlc01vbnRoKCk6CiAgICBCYW5uZXIoJ1ByaW50aW5nOiBBbGwgY29tbWFuZHMgZnJvbSB0aGUgbGFzdCBtb250aHMgKFNvcnRlZCBCeSBWb3RlcykgJykKICAgIHVybCA9ICJodHRwOi8vd3d3LmNvbW1hbmRsaW5lZnUuY29tL2NvbW1hbmRzL2Jyb3dzZS9sYXN0LW1vbnRoL3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlNYXRjaCgpOgogICAgI2ltcG9ydCBiYXNlNjQKICAgIEJhbm5lcigiU29ydCBCeSBNYXRjaCIpCiAgICBtYXRjaCA9IHJhd19pbnB1dCgiUGxlYXNlIGVudGVyIGEgc2VhcmNoIGNvbW1hbmQ6ICIpCiAgICBiZXN0bWF0Y2ggPSByZS5jb21waWxlKHInICcpCiAgICBzZWFyY2ggPSBiZXN0bWF0Y2guc3ViKCcrJywgbWF0Y2gpCiAgICBiNjRfZW5jb2RlZCA9IGJhc2U2NC5iNjRlbmNvZGUoc2VhcmNoKQogICAgdXJsID0gImh0dHA6Ly93d3cuY29tbWFuZGxpbmVmdS5jb20vY29tbWFuZHMvbWF0Y2hpbmcvIiArIHNlYXJjaCArICIvIiArIGI2NF9lbmNvZGVkICsgIi9qc29uIgogICAgcmVxdWVzdCA9IHVybGxpYjIuUmVxdWVzdCh1cmwpCiAgICByZXNwb25zZSA9IGpzb24ubG9hZCh1cmxsaWIyLnVybG9wZW4ocmVxdWVzdCkpCiAgICBmb3IgYyBpbiByZXNwb25zZToKICAgICAgICBwcmludCAiLSIgKiA2MAogIHByaW50IGNbJ2NvbW1hbmQnXQoKcHJpbnQgIiIiCjEuIFNvcnQgQnkgVm90ZXMgKEFsbCB0aW1lKQoyLiBTb3J0IEJ5IFZvdGVzIChUb2RheSkKMy4gU29ydCBieSBWb3RlcyAoV2VlaykKNC4gU29ydCBieSBWb3RlcyAoTW9udGgpCjUuIFNlYXJjaCBmb3IgYSBjb21tYW5kCiAKUHJlc3MgZW50ZXIgdG8gcXVpdAoiIiIKCndoaWxlIFRydWU6CiAgYW5zd2VyID0gcmF3X2lucHV0KCJXaGF0IHdvdWxkIHlvdSBsaWtlIHRvIGRvPyAiKQoKIGlmIGFuc3dlciA9PSAiIjoKICAgIHN5cy5leGl0KCkKICAKICBlbGlmIGFuc3dlciA9PSAiMSI6CiAgIHNvcnRCeVZvdGVzKCkKIAogIGVsaWYgYW5zd2VyID09ICIyIjoKICAgcHJpbnQgc29ydEJ5Vm90ZXNUb2RheSgpCiAgCiAgZWxpZiBhbnN3ZXIgPT0gIjMiOgogICBwcmludCBzb3J0QnlWb3Rlc1dlZWsoKQogCiAgZWxpZiBhbnN3ZXIgPT0gIjQiOgogICBwcmludCBzb3J0QnlWb3Rlc01vbnRoKCkKICAKICBlbGlmIGFuc3dlciA9PSAiNSI6CiAgIHByaW50IHNvcnRCeU1hdGNoKCkKIAogIGVsc2U6CiAgIHByaW50ICJOb3QgYSB2YWxpZCBjaG9pY2Ui")
                .build();

        KeyFinderService keyFinderService = new KeyFinderService(code);

        System.out.println(keyFinderService.decodeCode(code.getCodeEncoded()));
        System.out.println("keyFinderService.formattedCode = " + keyFinderService.getFormattedCode());
        System.out.println("keyFinderService.arrayOfSingleWords = " + keyFinderService.getArrayOfSingleWords());
        System.out.println("keyFinderService.arrayOfLinesOfSingleWords = " + keyFinderService.getArrayOfLinesOfSingleWords());
        System.out.println("keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism = " + keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism());
        System.out.println("keyFinderService.getArrayOfSingleWordsForPlagiarism = " + keyFinderService.getArrayOfSingleWordsForPlagiarism());
        System.out.println("keyFinderService.arrayOfVariables = " + keyFinderService.getArrayOfVariables());
        System.out.println("keyFinderService.arrayOfParams = " + keyFinderService.getArrayOfParams());
        System.out.println("keyFinderService.arrayOfFunctionNames = " + keyFinderService.getArrayOfFunctionNames());
        System.out.println("keyFinderService.getArrayOfFunctionBodyByNames = " + keyFinderService.getArrayOfFunctionBodyByNames());
//        keyFinderService.debugDisplayOfArray(keyFinderService.getArrayOfSingleWordsForPlagiarism());
        System.out.println("keyFinderService.debugDisplayOfHashTable(keyFinderService.getArrayOfFunctionBodyByNames()) : ///////////////////////////////");
        keyFinderService.debugDisplayOfHashTable(keyFinderService.getArrayOfFunctionBodyByNames());
    }
}
