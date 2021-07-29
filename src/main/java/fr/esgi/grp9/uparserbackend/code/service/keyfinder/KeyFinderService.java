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
        lines = anonymizeLinesOfCode(lines, this.arrayOfClassName, "CLASS");

        this.arrayOfLinesOfSingleWordsForPlagiarism = lines;
    }

    private List<List<String>> anonymizeLinesOfCode(List<List<String>> linesOfCode, List<String> wordsToAnonymize, String replacement){
        List<List<String>> endRes = new ArrayList<>();

        for (List<String> line: linesOfCode){
            List<String> tempList = new ArrayList<>();
            for (String wordFromCode: line){
                boolean shouldAdd = true;
                for (int i = 0; i < wordsToAnonymize.size(); i++) {
                    if (wordFromCode.equals(wordsToAnonymize.get(i))){
                        tempList.add(replacement + "n" + i);
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd){
                    tempList.add(wordFromCode);
                }
            }
            endRes.add(tempList);
        }



//            for (List<String> line: linesOfCode){
//                List<String> tempList = new ArrayList<>();
//                for (String wordFromCode: line){
//                        if (wordFromCode.equals(wordsToAnonymize.get(i))){
//                            tempList.add(replacement + "n" + i);
//                        } else {
//                            tempList.add(wordFromCode);
//                        }
//                    }
//                }
//                endRes.add(tempList);
//            }
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
                res.add(line.get(1));
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
        List<String> keyChars = Arrays.asList("(", ")", "=", ",", "\"", ":");
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
}
