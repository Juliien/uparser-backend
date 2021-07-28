package fr.esgi.grp9.uparserbackend.code.service.keyfinder;

import fr.esgi.grp9.uparserbackend.code.domain.Code;

import java.util.*;


public class KeyFinderService {

    private String END_OF_CODE_TAG;

    private final Code code;

    private String formattedCode;
    private List<String> arrayOfSingleWords = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWords = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWordsForQuality = new ArrayList<>();
    private List<String> arrayOfSingleWordsForQuality = new ArrayList<>();
    private List<List<String>> arrayOfLinesOfSingleWordsForPlagiarism = new ArrayList<>();

    private List<String> arrayOfVariables = new ArrayList<>();
    private List<String> arrayOfParams = new ArrayList<>();
    private List<String> arrayOfFunctionNames = new ArrayList<>();
    private List<List<String>> arrayOfImports = new ArrayList<>();
    private List<String> arrayOfClassName = new ArrayList<>();

    private Hashtable<String, List<List<String>>> arrayOfFunctionBodyByNames = new Hashtable<>();
    private Hashtable<String, Integer> arrayOfMaxDepthByFunctionName = new Hashtable<>();
    private List<Integer> allCodeIndentationValues = new ArrayList<>();

    public KeyFinderService(Code code) {
        this.code = code;
        this.END_OF_CODE_TAG = "**********endOfCodeTag**********";

        initFormattedCode();

        initArrayOfSingleWords();
        initArrayOfLinesOfSingleWords();
        initArrayOfLinesOfSingleWordsForQuality();
        initArrayOfSingleWordsForQuality();

        initVariablesFromArrayOfSingleWord();
        initParamsFromArrayOfSingleWord();
        initFunctionNameFromArrayOfSingleWord();
        initArrayOfFunctionBodyByNames();
        initArrayOfClassName();

        initAllCodeIndentationValues();
        initImportsFromLinesOfSingleWord();
        initArrayOfMaxDepthByFunctionName();
        initArrayOfLinesOfSingleWordsForPlagiarism();

        deleteDuplicationFromLists();
    }

    public Code getCode() {
        return code;
    }

    public List<List<String>> getArrayOfLinesOfSingleWordsForQuality() {
        return arrayOfLinesOfSingleWordsForQuality;
    }

    public List<String> getArrayOfSingleWordsForQuality() {
        return arrayOfSingleWordsForQuality;
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

    public List<Integer> getAllCodeIndentationValues() {
        return allCodeIndentationValues;
    }

    public List<List<String>> getArrayOfImports() {
        return arrayOfImports;
    }

    public List<String> getArrayOfClassName() {
        return arrayOfClassName;
    }

    public Hashtable<String, Integer> getArrayOfMaxDepthByFunctionName() {
        return arrayOfMaxDepthByFunctionName;
    }

    public List<List<String>> getArrayOfLinesOfSingleWordsForPlagiarism() {
        return arrayOfLinesOfSingleWordsForPlagiarism;
    }

    private String decodeCode(String code) {
        byte[] decodedBytes = Base64.getDecoder().decode(code);
        return new String(decodedBytes);
    }

    //init the formattedCode attribute
    // a string with good space and escaped chars
    private void initFormattedCode(){
        String begin = decodeCode(this.code.getCodeEncoded()) + "\n\n" + this.END_OF_CODE_TAG;
        String spaced = reformatStringsSpaces(begin);
        String formatted = reformatStringsEscapedChars(spaced);
//        String withEnd = addEndChar(formatted, this.END_OF_STRING);
        this.formattedCode = formatted;
    }

    //init the arrayOfSingleWords attribute
    // formattedCode split by \\s in a list of string
    private void initArrayOfSingleWords() {
        List<String> tempList = Arrays.asList(this.formattedCode.split("\\s"));
        List<String> specificSpaceTrimmedList = trimSpecificSpacesFromArray(tempList);
//        List<String> printValueLessList = reformatStringsPrint(specificSpaceTrimmedList, "PRINT_DATA");
//        this.arrayOfSingleWords = printValueLessList;
        this.arrayOfSingleWords = specificSpaceTrimmedList;
    }

    //init the arrayOfLinesOfSingleWords attribute
    // arrayOfSingleWords concatenate by \\n
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
        lines.add(tempList);

        this.arrayOfLinesOfSingleWords =  lines;
    }

    //init the arrayOfLinesOfSingleWordsForPlagiarism attribute
    // arrayOfLinesOfSingleWords without comment, import and print values for test plagiarism
    private void initArrayOfLinesOfSingleWordsForQuality(){
        List<List<String>> lines = this.arrayOfLinesOfSingleWords;

        lines = reformatLinesByStringElement(lines, "#");
        lines = reformatLinesByStringElement(lines, "import");
        lines = reformatLinesByStringElement(lines, "from");
        lines = reformatStringsPrint(lines, "PRINT_DATA");

        this.arrayOfLinesOfSingleWordsForQuality = lines;
    }

    private void initArrayOfLinesOfSingleWordsForPlagiarism(){
        List<List<String>> lines = this.arrayOfLinesOfSingleWordsForQuality;

        lines = trimEmptyLines(lines);
        lines = trimIndentationFromLines(lines);
        lines = anonymizeLinesOfCode(lines, this.arrayOfVariables, "VARIABLE");
        lines = anonymizeLinesOfCode(lines, this.arrayOfParams, "PARAMETER");
        lines = anonymizeLinesOfCode(lines, this.arrayOfFunctionNames, "FUNCTION");

        this.arrayOfLinesOfSingleWordsForPlagiarism = lines;
    }

    private List<List<String>> anonymizeLinesOfCode(List<List<String>> linesOfCode, List<String> wordsToAnonymize, String replacement){
        List<List<String>> endRes = new ArrayList<>();
        for (List<String> line: linesOfCode){
            List<String> tempList = new ArrayList<>();
            for (String wordFromCode: line){
                for (int i = 0; i < wordsToAnonymize.size(); i++) {
                    if (wordFromCode.equals(wordsToAnonymize.get(i))){
                        tempList.add(replacement + "n" + i);
                    } else {
                        tempList.add(wordFromCode);
                    }
                }
            }
            endRes.add(tempList);
        }
        return endRes;
    }

    //init the arrayOfSingleWordsForPlagiarism attribute
    // arrayOfLinesOfSingleWordsForPlagiarism transform into list of words for test plagiarism
    private void initArrayOfSingleWordsForQuality() {
        this.arrayOfSingleWordsForQuality = arrayOfLinesToArrayOfSingleWords(this.arrayOfLinesOfSingleWordsForQuality);
    }

    // convert List<List<String>> to List<String> when \\n is present at the end of a list
    private List<String> arrayOfLinesToArrayOfSingleWords(List<List<String>> lines){
        List<String> finalList = new ArrayList<>();
        for (List<String> strings: lines){
            for (String str: strings){
                finalList.add(str);
            }
        }
        return finalList;
    }

    //init the arrayOfVariables attribute
    // collect every variable name from arrayOfSingleWordsForPlagiarism by getting the value before every "="
    // and store them in the arrayOfVariables attribute
    private void initVariablesFromArrayOfSingleWord() {
        List<String> target = this.arrayOfSingleWordsForQuality;
        for (int start = 0; start < target.size(); start++) {
            if (target.get(start).contains("=")){
                if (!target.get(start-1).contains("=") && !target.get(start+1).contains("=")
                        && !target.get(start-1).contains(">") && !target.get(start-1).contains("<")
                        && !target.get(start-1).contains("\"") && !target.get(start-1).contains("'")
                        && !target.get(start-1).contains("!") && !target.get(start-1).contains("-")
                        && !target.get(start-1).contains("+") && !target.get(start-1).contains("%")) {
                    List<String> lineTemp = Arrays.asList(target.get(start - 1).split("\\."));
                    if (lineTemp.size() > 1){
                        this.arrayOfVariables.add(lineTemp.get(lineTemp.size() -1));
                    } else {
                        this.arrayOfVariables.add(lineTemp.get(0));
                    }
                }
            }
        }
    }

    //init the arrayOfParams attribute
    // collect every param name from arrayOfSingleWordsForPlagiarism by getting everything between "def (" and ") :"
    // and store them in the arrayOfParams attribute
    private void initParamsFromArrayOfSingleWord() {
        List<String> params = new ArrayList<>();
        for (List<String> line: this.arrayOfLinesOfSingleWordsForQuality){
            if (line.contains("def") && line.contains("(") && line.contains(")") && line.contains(":")){
                String temp = "";
                for (String str: line){
                    temp = temp + str;
                }
                params.addAll(Arrays.asList(temp.split("\\(")[1].split("\\)")[0].split(",")));
            }
        }
        this.arrayOfParams = params;
    }

    //init the arrayOfFunctionNames attribute
    // collect every param name from arrayOfSingleWordsForPlagiarism by getting everything between "def (" and ") :"
    // and store them in the arrayOfFunctionNames attribute
    private void initFunctionNameFromArrayOfSingleWord(){
        List<String> target = this.arrayOfSingleWordsForQuality;
        for (int start = 0; start < target.size(); start++) {
            if (target.get(start).contains("def")){
                this.arrayOfFunctionNames.add(target.get(start+1));
            }
        }
    }

    //init the arrayOfFunctionBodyByNames attribute
    // seek through the arrayOfLinesOfSingleWordsForPlagiarism the occurrence of a function name and the key word "def" and ":"
    // and put all lines till the indentation of the current line is > of the def one
    // do this for all function name inside the arrayOfFunctionNames and put the result inside a HashTable <funName, funBody>
    private void initArrayOfFunctionBodyByNames() {
        List<List<String>> target = this.arrayOfLinesOfSingleWordsForQuality;
        Hashtable<String, List<List<String>>> funNameAndBody = new Hashtable<>();
        for (String funName: this.arrayOfFunctionNames){
            List<List<String>> funBody = new ArrayList<>();
            for (int i = 0; i < target.size(); i++) {
                if (target.get(i).contains(funName) && target.get(i).contains("def") && target.get(i).contains(":")){
                    Integer funIndentation = indentationLevelOfALine(target.get(i));
                    funBody.add(target.get(i));
                    int y = i + 1;
                    while((indentationLevelOfALine(target.get(y)) > funIndentation || target.get(y).size() == 0)){
                        if (target.get(y).equals(this.END_OF_CODE_TAG)){
                            break;
                        }
                        funBody.add(target.get(y));
                        y++;
                    }
                }
            }
            funNameAndBody.put(funName, funBody);
        }
        this.arrayOfFunctionBodyByNames = funNameAndBody;
    }

    //init the arrayOfImports attribute
    // collect every import from arrayOfLinesOfSingleWords by getting everything that contains "import" and that has an indentation == 0
    // and store them in the arrayOfImports attribute
    private void initImportsFromLinesOfSingleWord(){
        List<List<String>> res = new ArrayList<>();
        List<List<String>> lines = this.arrayOfLinesOfSingleWords;
        for (List<String> line: lines) {
            if (line.contains("import") && indentationLevelOfALine(line) == 0){
                res.add(line);
            }
        }
        this.arrayOfImports = res;
    }

    private void initArrayOfClassName() {
        List<String> res = new ArrayList<>();
        List<List<String>> lines = this.arrayOfLinesOfSingleWords;
        for (List<String> line: lines) {
            if (line.contains("class") && indentationLevelOfALine(line) == 0){
                res.add(line.get(1).split(":")[0]);
            }
        }
        this.arrayOfClassName = res;
    }

    private void initArrayOfMaxDepthByFunctionName() {
        Hashtable<String, List<List<String>>> target = this.arrayOfFunctionBodyByNames;
        Hashtable<String, Integer> res = new Hashtable<>();

        for (Map.Entry<String, List<List<String>>> set:
                target.entrySet()) {
            res.put(set.getKey(), maxDepthOfFunction(set.getValue()));
        }

        this.arrayOfMaxDepthByFunctionName = res;
    }

    // return the maximum depth inside a function
    private Integer maxDepthOfFunction(List<List<String>> funBodyLines){
        List<Integer> depthValues = new ArrayList<>();
        List<String> wantedKeyWords = Arrays.asList("for", "while");

        for (String keyWord : wantedKeyWords){
            for (List<String> line: funBodyLines){
                Integer currDepth = indentationLevelOfALine(line);
                if (!depthValues.contains(currDepth)){
                    depthValues.add(currDepth);
                }
            }
        }

        return depthValues.size() - 2;
    }

    // return an Integer that is the number of space before the fist word in a line
    private Integer indentationLevelOfALine(List<String> line){
        if (line.size() == 0){
            return 0;
        }
        for (int i = 0; i < line.size(); i++) {
            if (!line.get(i).equals("")){
                return i;
            }
        }
        return 0;
    }

    //init the allCodeIndentationValues attribute
    // get every indentation value from arrayOfLinesOfSingleWords
    // and put them in the allCodeIndentationValues attribute
    private void initAllCodeIndentationValues(){
        List<List<String>> lines = this.arrayOfLinesOfSingleWords;
        List<Integer> indentationValues = new ArrayList<>();

        for (List<String> line: lines){
            if (!line.contains(this.END_OF_CODE_TAG)){
                indentationValues.add(indentationLevelOfALine(line));
            }
        }
        this.allCodeIndentationValues = indentationValues;
    }

    // delete all duplicate in arrayOfVariables, arrayOfParams and arrayOfFunctionNames
    private void deleteDuplicationFromLists() {
        this.arrayOfVariables = deleteDuplicationFromAList(this.arrayOfVariables);
        this.arrayOfParams = deleteDuplicationFromAList(this.arrayOfParams);
        this.arrayOfFunctionNames = deleteDuplicationFromAList(this.arrayOfFunctionNames);
    }

    // delete all duplicate in a list
    private List<String> deleteDuplicationFromAList(List<String> strings){
        List<String> arrayOfStringDuplicationLess = new ArrayList<>();
        for (String str: strings) {
            if (!arrayOfStringDuplicationLess.contains(str)){
                arrayOfStringDuplicationLess.add(str);
            }
        }
        return arrayOfStringDuplicationLess;
    }

    //add spaces to every char that is useful to parse the code : ("(", ")", "=", ",", "\"")
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

    //make escaped char visible and manageable : ("\t")("\n")("\b")("\r")("\f")
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

    //replace print() value by a specific value
    private List<List<String>> reformatStringsPrint(List<List<String>> lines, String replaceWith) {
        List<List<String>> finalList = new ArrayList<>();
        for (List<String> line: lines){
            if (line.contains("print")){
                List<String> newLine = new ArrayList<>();
                Integer indent = indentationLevelOfALine(line);
                for (int i = 0; i < indent; i++) {
                    newLine.add("");
                }
                newLine.add("print");
                newLine.add("(");
                for (int i = indent + 2; i < line.size()-1; i++) {
                    newLine.add(replaceWith);
                }
                newLine.add(")");
                finalList.add(newLine);
            } else {
                finalList.add(line);
            }
        }
        return finalList;
    }

    private List<List<String>> trimEmptyLines(List<List<String>> lines) {
        List<List<String>> res = new ArrayList<>();

        for (List<String> line: lines){
            if (line.size() != 0){
                res.add(line);
            }
        }
        return res;
    }

    private List<List<String>> trimIndentationFromLines(List<List<String>> lines) {
        List<List<String>> res = new ArrayList<>();

        for (List<String> line: lines){
            List<String> tempList = new ArrayList<>();
            for (String word: line){
                if (word.length() != 0){
                    tempList.add(word);
                }
            }
            res.add(tempList);
        }
        return res;
    }

    //remove spaces created by reformatStringsSpaces()
    private List<String> trimSpecificSpacesFromArray(List<String> strings){
        List<String> finalList = new ArrayList<>();
        finalList.add(strings.get(0));
        int i = 1;
        while(!strings.get(i).equals(this.END_OF_CODE_TAG)){
            if (!((!strings.get(i-1).equals("") && !strings.get(i+1).equals("")) && strings.get(i).equals(""))){
                finalList.add(strings.get(i));
            }
            i++;
        }
        finalList.add(this.END_OF_CODE_TAG);
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

    //remove from a List<List<String>> every List<String> that contains a string
    private List<List<String>> reformatLinesByStringElement(List<List<String>> lines, String elementToRemove) {
        List<List<String>> finalLines = new ArrayList<>();
        for (List<String> line: lines){
            boolean shouldAdd = true;
            for (String word: line){
                if (word.contains(elementToRemove)){
                    shouldAdd = false;
                }
            }
            if (shouldAdd){
                finalLines.add(line);
            }
        }
        return finalLines;
    }

    public static void main(String[] args) throws Exception {
        Code code = Code.builder()
                .language("python")
                .extensionEnd("csv")
                .extensionEnd("json")
                .codeEncoded("aW1wb3J0IHN5cwoKZGVmIEhlbGxvKCk6CiAgICBwcmludCgiIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIyIikKICAgIHByaW50KCIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIjIiKQogICAgcHJpbnQoIiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCiAgICBwcmludCgiMiIpCgpwcmludCgiaGVsbG8iKQpqZVN1c2kgPSAxCkhlbGxvKCkKIA==")
//                .codeEncoded("aW1wb3J0IHN5cwoKd2l0aCBvcGVuKHN5cy5hcmd2WzFdKSBhcyBmaWxlOgogIHByaW50KGZpbGUucmVhZCgpKQog")
//                .codeEncoded("aW1wb3J0IGpzb24NCmltcG9ydCBjc3YNCmltcG9ydCBzeXMNCiANCndpdGggb3BlbihzeXMuYXJndlsxXSkgYXMganNvbl9maWxlOg0KICAgIGpzb25fZGF0YSA9IGpzb24ubG9hZChqc29uX2ZpbGUpDQoNCndpdGggb3Blbignb3V0LmNzdicsICdhJykgYXMgZl9vYmplY3Q6DQogICAgZHcgPSBjc3YuRGljdFdyaXRlcihmX29iamVjdCwgZGVsaW1pdGVyPSc7JywgZmllbGRuYW1lcz1qc29uX2RhdGEua2V5cygpKQ0KICAgIGR3LndyaXRlaGVhZGVyKCkNCiAgICBkdy53cml0ZXJvdyhqc29uX2RhdGEpDQogICANCg==")
//                .codeEncoded("ZnJvbSBWYWxsZWdhbWVvZmxpZmUuZ2FtZS5DZWxsIGltcG9ydCBDZWxsCmZyb20gVmFsbGVnYW1lb2ZsaWZlLmdhbWUuRm9ybSBpbXBvcnQgRm9ybQppbXBvcnQgcmFuZG9tCgoKY2xhc3MgUnVubmVyOgogICAgJycnQ2xhc3Mgd2ljaCBwcm92aWRlIGEgbWFwIG9mIGNlbGxzIGFuZAogICAgIG1hbmFnZSB0aGVpciBiZWhhdmlvdXIgdGhyb3VnaCBydWxlcycnJwoKICAgIHRvX3Jldml2ZSA9IFtdCiAgICB0b19raWxsID0gW10KICAgIGZvcm1zID0gRm9ybQoKICAgIGRlZiBfX2luaXRfXyhzZWxmLCBmb3Jtc19hbW91bnQ9NTAsIGRlbnNpdHk9MTAwLCBzaXplPTUwLCBpbml0aWFsX3N0YXR1cz0iZGVhZCIsIHJvdW5kcz01MDAwMCk6CiAgICAgICAgc2VsZi5kZW5zaXR5ID0gZGVuc2l0eQogICAgICAgIHNlbGYuc2l6ZSA9IHNpemUKICAgICAgICBzZWxmLm1hcCA9IHNlbGYuZ2VuZXJhdGVfbWFwKGluaXRpYWxfc3RhdHVzLCBzaXplKQogICAgICAgIHNlbGYuc2V0X2FkZHJlc3NlcygpCiAgICAgICAgc2VsZi5nZW5lcmF0ZV9mb3Jtcyhmb3Jtc19hbW91bnQpCiAgICAgICAgc2VsZi5yb3VuZHMgPSByb3VuZHMKCiAgICBkZWYgZ2VuZXJhdGVfbWFwKHNlbGYsIGluaXRpYWxfc3RhdHVzLCBzaXplKToKICAgICAgICByZXR1cm4gW1tDZWxsKChpICsgMSkgKiAoXyArIDEpLCBpbml0aWFsX3N0YXR1cykgZm9yIGkgaW4gcmFuZ2Uoc2l6ZSldIGZvciBfIGluIHJhbmdlKHNpemUpXQoKICAgIGRlZiBnZW5lcmF0ZV9mb3JtcyhzZWxmLCBhbW91bnQpOgogICAgICAgIGZvciBfIGluIHJhbmdlKGFtb3VudCk6CiAgICAgICAgICAgIHRyaWVzID0gMTAKICAgICAgICAgICAgc2hhcGUgPSBGb3JtLmdldF9zaGFwZSgpCiAgICAgICAgICAgIHBsYWNhYmxlID0gRmFsc2UKICAgICAgICAgICAgd2hpbGUgcGxhY2FibGUgaXMgbm90IFRydWUgYW5kIHRyaWVzID4gMDoKICAgICAgICAgICAgICAgIHRyaWVzIC09IDEKICAgICAgICAgICAgICAgIHJhbmRvbV9wb2ludCA9IHNlbGYuZ2V0X3JhbmRvbV9wb2ludCgpCiAgICAgICAgICAgICAgICBwbGFjYWJsZSA9IHNlbGYuY2hlY2tfcGxhY2UocmFuZG9tX3BvaW50LCBzaGFwZSkKICAgICAgICAgICAgICAgIGlmIHBsYWNhYmxlOgogICAgICAgICAgICAgICAgICAgIHNlbGYucGxhY2UocmFuZG9tX3BvaW50LCBzaGFwZSkKCiAgICAgICAgcmV0dXJuCgogICAgZGVmIGNoZWNrX3BsYWNlKHNlbGYsIG9yaWdpbiwgc2hhcGUpOgogICAgICAgIGZvciB4X2luZGV4IGluIHJhbmdlKEZvcm0uZ2V0X3NoYXBlX2Zvcm0oc2hhcGUpWyJ4Il0pOgogICAgICAgICAgICBmb3IgeV9pbmRleCBpbiByYW5nZShGb3JtLmdldF9zaGFwZV9mb3JtKHNoYXBlKVsieSJdKToKICAgICAgICAgICAgICAgIHggPSAob3JpZ2luWyd4J10gKyB4X2luZGV4KSAlIHNlbGYuc2l6ZQogICAgICAgICAgICAgICAgeSA9IChvcmlnaW5bJ3knXSArIHlfaW5kZXgpICUgc2VsZi5zaXplCiAgICAgICAgICAgICAgICBpZiBzZWxmLmlzX2ZyZWUoeCwgeSkgaXMgbm90IFRydWU6CiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIEZhbHNlCiAgICAgICAgcmV0dXJuIFRydWUKCiAgICBkZWYgcGxhY2Uoc2VsZiwgb3JpZ2luLCBzaGFwZSk6CiAgICAgICAgZm9yIHhfaW5kZXggaW4gcmFuZ2UoRm9ybS5nZXRfc2hhcGVfZm9ybShzaGFwZSlbIngiXSk6CiAgICAgICAgICAgIGZvciB5X2luZGV4IGluIHJhbmdlKEZvcm0uZ2V0X3NoYXBlX2Zvcm0oc2hhcGUpWyJ5Il0pOgogICAgICAgICAgICAgICAgeCA9IChvcmlnaW5bJ3gnXSArIHhfaW5kZXgpICUgc2VsZi5zaXplCiAgICAgICAgICAgICAgICB5ID0gKG9yaWdpblsneSddICsgeV9pbmRleCkgJSBzZWxmLnNpemUKICAgICAgICAgICAgICAgIHNlbGYubWFwW3ldW3hdLnN0YXR1cyA9ICdhbGl2ZScgaWYgc2hhcGVbeV9pbmRleF1beF9pbmRleF0gPT0gJ2EnIGVsc2UgJ2RlYWQnCgogICAgICAgIHJldHVybgoKICAgIGRlZiBpc19mcmVlKHNlbGYsIHgsIHkpOgogICAgICAgIHggJT0gc2VsZi5zaXplCiAgICAgICAgeSAlPSBzZWxmLnNpemUKICAgICAgICByZXR1cm4gVHJ1ZSBpZiBzZWxmLm1hcFt5XVt4XS5zdGF0dXMgPT0gJ2RlYWQnIGVsc2UgRmFsc2UKCiAgICBkZWYgZ2V0X3JhbmRvbV9wb2ludChzZWxmKToKICAgICAgICByZXR1cm4gewogICAgICAgICAgICAneCc6IHJhbmRvbS5yYW5kaW50KDAsIHNlbGYuc2l6ZSAtIDEpLAogICAgICAgICAgICAneSc6IHJhbmRvbS5yYW5kaW50KDAsIHNlbGYuc2l6ZSAtIDEpCiAgICAgICAgfQoKICAgIGRlZiBzZXRfYWRkcmVzc2VzKHNlbGYpOgogICAgICAgIGZvciBpIGluIHJhbmdlKGxlbihzZWxmLm1hcCkpOgogICAgICAgICAgICB5ID0gaQogICAgICAgICAgICBmb3IgaiBpbiByYW5nZShsZW4oc2VsZi5tYXBbaV0pKToKICAgICAgICAgICAgICAgIGNlbGwgPSBzZWxmLm1hcFtpXVtqXQogICAgICAgICAgICAgICAgeCA9IGoKICAgICAgICAgICAgICAgIGNlbGwuc2V0X2FkZHJlc3MoeCwgeSkKCiAgICBkZWYgcnVuKHNlbGYpOgogICAgICAgIGlmIHNlbGYucm91bmRzIDwgMDoKICAgICAgICAgICAgcmV0dXJuCgogICAgICAgIGZvciBjZWxsX2xpbmUgaW4gc2VsZi5tYXA6CiAgICAgICAgICAgIGZvciBjZWxsIGluIGNlbGxfbGluZToKICAgICAgICAgICAgICAgIHNlbGYudHJ5X2tpbGwoY2VsbCkgaWYgY2VsbC5zdGF0dXMgPT0gJ2FsaXZlJyBlbHNlIHNlbGYudHJ5X3Jldml2ZShjZWxsKQogICAgICAgIHNlbGYudXBkYXRlKCkKICAgICAgICBzZWxmLnJlc2V0KCkKICAgICAgICBzZWxmLnJvdW5kcyAtPSAxCgogICAgZGVmIHRyeV9raWxsKHNlbGYsIGNlbGwpOgogICAgICAgIG5laWdoYm91cmhvb2QgPSBzZWxmLmdldF9uZWlnaGJvcmhvb2QoY2VsbCkKICAgICAgICBpZiBsZW4obmVpZ2hib3VyaG9vZCkgIT0gMiBhbmQgbGVuKG5laWdoYm91cmhvb2QpICE9IDM6CiAgICAgICAgICAgIHNlbGYudG9fa2lsbC5hcHBlbmQoY2VsbCkKCiAgICBkZWYgdHJ5X3Jldml2ZShzZWxmLCBjZWxsKToKICAgICAgICBuZWlnaGJvdXJob29kID0gc2VsZi5nZXRfbmVpZ2hib3Job29kKGNlbGwpCiAgICAgICAgaWYgbGVuKG5laWdoYm91cmhvb2QpID09IDM6CiAgICAgICAgICAgIHNlbGYudG9fcmV2aXZlLmFwcGVuZChjZWxsKQoKICAgIGRlZiBnZXRfbmVpZ2hib3Job29kKHNlbGYsIGNlbGwpOgogICAgICAgIG5laWdiaG9ycyA9IFtdCiAgICAgICAgdG9wX3lfaW5kZXggPSBjZWxsLmFkZHJlc3NbJ3knXSAtIFwKICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3knXSA+IDAgZWxzZSBzZWxmLnNpemUgLSAxCiAgICAgICAgYm90X3lfaW5kZXggPSBjZWxsLmFkZHJlc3NbJ3knXSArIFwKICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3knXSA8IHNlbGYuc2l6ZSAtIDEgZWxzZSAwCgogICAgICAgIGZvciB5IGluIFt0b3BfeV9pbmRleCwgY2VsbC5hZGRyZXNzWyd5J10sIGJvdF95X2luZGV4XToKICAgICAgICAgICAgbGVmdF94X2luZGV4ID0gY2VsbC5hZGRyZXNzWyd4J10gLSBcCiAgICAgICAgICAgICAgICAxIGlmIGNlbGwuYWRkcmVzc1sneCddID4gMCBlbHNlIHNlbGYuc2l6ZSAtIDEKICAgICAgICAgICAgcmlnaHRfeF9pbmRleCA9IGNlbGwuYWRkcmVzc1sneCddICsgXAogICAgICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3gnXSA8IHNlbGYuc2l6ZSAtIDEgZWxzZSAwCiAgICAgICAgICAgIGZvciB4IGluIFtsZWZ0X3hfaW5kZXgsIGNlbGwuYWRkcmVzc1sneCddLCByaWdodF94X2luZGV4XToKICAgICAgICAgICAgICAgIG5laWdiaG9ycy5hcHBlbmQoc2VsZi5tYXBbeV1beF0pCgogICAgICAgIHJldHVybiBbbiBmb3IgbiBpbiBuZWlnYmhvcnMgaWYgbi5zdGF0dXMgPT0gJ2FsaXZlJyBhbmQgbiBpcyBub3QgY2VsbF0KCiAgICBkZWYgdXBkYXRlKHNlbGYpOgogICAgICAgIGZvciBjZWxsIGluIHNlbGYudG9fcmV2aXZlOgogICAgICAgICAgICBjZWxsLnJldml2ZSgpCgogICAgICAgIGZvciBjZWxsIGluIHNlbGYudG9fa2lsbDoKICAgICAgICAgICAgY2VsbC5kaWUoKQoKICAgIGRlZiByZXNldChzZWxmKToKICAgICAgICBzZWxmLnRvX2tpbGwgPSBbXQogICAgICAgIHNlbGYudG9fcmV2aXZlID0gW10KCiAgICBkZWYgX19zdHJfXyhzZWxmKToKICAgICAgICByZXR1cm4gKCdcbmRlbnNpdHkgPSAnICsgc3RyKHNlbGYuZGVuc2l0eSkgKyAnXG5zaXplID0gJyArIHN0cihzZWxmLnNpemUpICsgJ1xucG9wdWxhdGlvbiA9IFxuJyArIHNlbGYuc3RyaW5naWZ5X21hcCgpKQoKICAgIGRlZiBzdHJpbmdpZnlfbWFwKHNlbGYpOgogICAgICAgIGZ1bGxfY2VsbHMgPSBbW3N0cihjZWxsKSBmb3IgY2VsbCBpbiBjZWxsX2FycmF5XQogICAgICAgICAgICAgICAgICAgICAgZm9yIGNlbGxfYXJyYXkgaW4gc2VsZi5tYXBdCiAgICAgICAgZm9yIGkgaW4gcmFuZ2UobGVuKGZ1bGxfY2VsbHMpKToKICAgICAgICAgICAgcHJpbnQoZnVsbF9jZWxsc1tpXSkKCiAgICAgICAgcmV0dXJuIHN0cihmdWxsX2NlbGxzKQ==")
//                                .codeEncoded("IyEvdXNyL2Jpbi9lbnYgcHl0aG9uMjcKaW1wb3J0IHVybGxpYjIKaW1wb3J0IGJhc2U2NAppbXBvcnQganNvbgppbXBvcnQgb3MKaW1wb3J0IHN5cwppbXBvcnQgcmUKCm9zLnN5c3RlbSgiY2xlYXIiKQpwcmludCAiLSIgKiA4MApwcmludCAiQ29tbWFuZCBMaW5lIFNlYXJjaCBUb29sIgpwcmludCAiLSIgKiA4MAoKZGVmIEJhbm5lcih0ZXh0KToKICAgIHByaW50ICI9IiAqIDcwCiAgICBwcmludCB0ZXh0CiAgICBwcmludCAiPSIgKiA3MAogICAgc3lzLnN0ZG91dC5mbHVzaCgpCgpkZWYgc29ydEJ5Vm90ZXMoKToKICAgIEJhbm5lcignU29ydCBCeSBWb3RlcycpCiAgICB1cmwgPSAiaHR0cDovL3d3dy5jb21tYW5kbGluZWZ1LmNvbS9jb21tYW5kcy9icm93c2Uvc29ydC1ieS12b3Rlcy9qc29uIgogICAgcmVxdWVzdCA9IHVybGxpYjIuUmVxdWVzdCh1cmwpCiAgICByZXNwb25zZSA9IGpzb24ubG9hZCh1cmxsaWIyLnVybG9wZW4ocmVxdWVzdCkpCiAgICAjcHJpbnQganNvbi5kdW1wcyhyZXNwb25zZSxpbmRlbnQ9MikKICAgIGZvciBjIGluIHJlc3BvbnNlOgogICAgICAgIHByaW50ICItIiAqIDYwCiAgICAgICAgcHJpbnQgY1snY29tbWFuZCddCgpkZWYgc29ydEJ5Vm90ZXNUb2RheSgpOgogICAgQmFubmVyKCdQcmludGluZyBBbGwgY29tbWFuZHMgdGhlIGxhc3QgZGF5IChTb3J0IEJ5IFZvdGVzKSAnKQogICAgdXJsID0gImh0dHA6Ly93d3cuY29tbWFuZGxpbmVmdS5jb20vY29tbWFuZHMvYnJvd3NlL2xhc3QtZGF5L3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlWb3Rlc1dlZWsoKToKICAgIEJhbm5lcignUHJpbnRpbmcgQWxsIGNvbW1hbmRzIHRoZSBsYXN0IHdlZWsgKFNvcnQgQnkgVm90ZXMpICcpCiAgICB1cmwgPSAiaHR0cDovL3d3dy5jb21tYW5kbGluZWZ1LmNvbS9jb21tYW5kcy9icm93c2UvbGFzdC13ZWVrL3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlWb3Rlc01vbnRoKCk6CiAgICBCYW5uZXIoJ1ByaW50aW5nOiBBbGwgY29tbWFuZHMgZnJvbSB0aGUgbGFzdCBtb250aHMgKFNvcnRlZCBCeSBWb3RlcykgJykKICAgIHVybCA9ICJodHRwOi8vd3d3LmNvbW1hbmRsaW5lZnUuY29tL2NvbW1hbmRzL2Jyb3dzZS9sYXN0LW1vbnRoL3NvcnQtYnktdm90ZXMvanNvbiIKICAgIHJlcXVlc3QgPSB1cmxsaWIyLlJlcXVlc3QodXJsKQogICAgcmVzcG9uc2UgPSBqc29uLmxvYWQodXJsbGliMi51cmxvcGVuKHJlcXVlc3QpKQogICAgZm9yIGMgaW4gcmVzcG9uc2U6CiAgICAgICAgcHJpbnQgIi0iICogNjAKICAgICAgICBwcmludCBjWydjb21tYW5kJ10KCmRlZiBzb3J0QnlNYXRjaCgpOgogICAgI2ltcG9ydCBiYXNlNjQKICAgIEJhbm5lcigiU29ydCBCeSBNYXRjaCIpCiAgICBtYXRjaCA9IHJhd19pbnB1dCgiUGxlYXNlIGVudGVyIGEgc2VhcmNoIGNvbW1hbmQ6ICIpCiAgICBiZXN0bWF0Y2ggPSByZS5jb21waWxlKHInICcpCiAgICBzZWFyY2ggPSBiZXN0bWF0Y2guc3ViKCcrJywgbWF0Y2gpCiAgICBiNjRfZW5jb2RlZCA9IGJhc2U2NC5iNjRlbmNvZGUoc2VhcmNoKQogICAgdXJsID0gImh0dHA6Ly93d3cuY29tbWFuZGxpbmVmdS5jb20vY29tbWFuZHMvbWF0Y2hpbmcvIiArIHNlYXJjaCArICIvIiArIGI2NF9lbmNvZGVkICsgIi9qc29uIgogICAgcmVxdWVzdCA9IHVybGxpYjIuUmVxdWVzdCh1cmwpCiAgICByZXNwb25zZSA9IGpzb24ubG9hZCh1cmxsaWIyLnVybG9wZW4ocmVxdWVzdCkpCiAgICBmb3IgYyBpbiByZXNwb25zZToKICAgICAgICBwcmludCAiLSIgKiA2MAogIHByaW50IGNbJ2NvbW1hbmQnXQoKcHJpbnQgIiIiCjEuIFNvcnQgQnkgVm90ZXMgKEFsbCB0aW1lKQoyLiBTb3J0IEJ5IFZvdGVzIChUb2RheSkKMy4gU29ydCBieSBWb3RlcyAoV2VlaykKNC4gU29ydCBieSBWb3RlcyAoTW9udGgpCjUuIFNlYXJjaCBmb3IgYSBjb21tYW5kCiAKUHJlc3MgZW50ZXIgdG8gcXVpdAoiIiIKCndoaWxlIFRydWU6CiAgYW5zd2VyID0gcmF3X2lucHV0KCJXaGF0IHdvdWxkIHlvdSBsaWtlIHRvIGRvPyAiKQoKIGlmIGFuc3dlciA9PSAiIjoKICAgIHN5cy5leGl0KCkKICAKICBlbGlmIGFuc3dlciA9PSAiMSI6CiAgIHNvcnRCeVZvdGVzKCkKIAogIGVsaWYgYW5zd2VyID09ICIyIjoKICAgcHJpbnQgc29ydEJ5Vm90ZXNUb2RheSgpCiAgCiAgZWxpZiBhbnN3ZXIgPT0gIjMiOgogICBwcmludCBzb3J0QnlWb3Rlc1dlZWsoKQogCiAgZWxpZiBhbnN3ZXIgPT0gIjQiOgogICBwcmludCBzb3J0QnlWb3Rlc01vbnRoKCkKICAKICBlbGlmIGFuc3dlciA9PSAiNSI6CiAgIHByaW50IHNvcnRCeU1hdGNoKCkKIAogIGVsc2U6CiAgIHByaW50ICJOb3QgYSB2YWxpZCBjaG9pY2Ui")
                .build();

        KeyFinderService keyFinderService = new KeyFinderService(code);

//        System.out.println(keyFinderService.getArrayOfLinesOfSingleWords());
//        System.out.println(keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism());
//        System.out.println(keyFinderService.getArrayOfClassName());

        System.out.println(keyFinderService.decodeCode(code.getCodeEncoded()));
        System.out.println("keyFinderService.formattedCode = " + keyFinderService.getFormattedCode());
        System.out.println();
        System.out.println("keyFinderService.arrayOfSingleWords = " + keyFinderService.getArrayOfSingleWords());
        System.out.println();
        System.out.println("keyFinderService.arrayOfLinesOfSingleWords = " + keyFinderService.getArrayOfLinesOfSingleWords());
        System.out.println();
        System.out.println("keyFinderService.getArrayOfLinesOfSingleWordsForQuality = " + keyFinderService.getArrayOfLinesOfSingleWordsForQuality());
        System.out.println();
        System.out.println("keyFinderService.getArrayOfSingleWordsForQuality = " + keyFinderService.getArrayOfSingleWordsForQuality());
        System.out.println();

        System.out.println("keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism() = " + keyFinderService.getArrayOfLinesOfSingleWordsForPlagiarism());

//
//        for (List<String> line: keyFinderService.getArrayOfLinesOfSingleWords()){
//            System.out.println(keyFinderService.indentationLevelOfALine(line));
//        }


//        System.out.println("keyFinderService.arrayOfVariables = " + keyFinderService.getArrayOfVariables());
//        System.out.println("keyFinderService.arrayOfParams = " + keyFinderService.getArrayOfParams());
//        System.out.println("keyFinderService.arrayOfFunctionNames = " + keyFinderService.getArrayOfFunctionNames());
        System.out.println("keyFinderService.getArrayOfFunctionBodyByNames = " + keyFinderService.getArrayOfFunctionBodyByNames());
//
//        System.out.println(Arrays.toString("self.test".split("self.")));
//        System.out.println(Arrays.toString("test".split("self.")));
//
        System.out.println("depth = " + keyFinderService.maxDepthOfFunction(keyFinderService.getArrayOfFunctionBodyByNames().get("Hello")));

//
//        for (Map.Entry<String, List<List<String>>> set:
//                keyFinderService.getArrayOfFunctionBodyByNames().entrySet()) {
//            System.out.println(set.getKey() + " :");
//            System.out.println(keyFinderService.maxDepthOfFunction(set.getValue()));
//            System.out.println();
//        }
//
//        System.out.println(keyFinderService.getAllCodeIndentationValues());
//        System.out.println(keyFinderService.getArrayOfImports());

//        keyFinderService.debugDisplayOfArray(keyFinderService.getArrayOfSingleWordsForPlagiarism());
//        System.out.println("keyFinderService.debugDisplayOfHashTable(keyFinderService.getArrayOfFunctionBodyByNames()) : ///////////////////////////////");
//        keyFinderService.debugDisplayOfHashTable(keyFinderService.getArrayOfFunctionBodyByNames());
    }
}
