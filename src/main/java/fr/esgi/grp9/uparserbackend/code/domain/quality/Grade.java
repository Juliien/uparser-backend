package fr.esgi.grp9.uparserbackend.code.domain.quality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.esgi.grp9.uparserbackend.code.domain.Code;
import fr.esgi.grp9.uparserbackend.code.service.keyfinder.KeyFinderService;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

@Data
@Document(collection = "grades")
public class Grade {
    @JsonIgnore
    private final KeyFinderService keyFinderService;

    @Id
    private String id;
    @Field(value = "code_id")
    private String codeId;
    @Field(value = "run_id")
    private String runId;

    @Field(value = "indentation_grade")
    private Float indentationGrade;
    @Field(value = "indentation_err_messages")
    private List<String> indentationErrMessages;

    @Field(value = "function_length_grade")
    private Float functionLengthGrade;
    @Field(value = "function_length_err_messages")
    private List<String> functionLengthErrMessages;

    @Field(value = "line_length_grade")
    private Float lineLengthGrade;
    @Field(value = "line_length_err_messages")
    private List<String> lineLengthErrMessages;

    @Field(value = "naming_var_grade")
    private Float namingVarGrade;
    @Field(value = "naming_var_err_messages")
    private List<String> namingVarErrMessages;

    @Field(value = "naming_class_grade")
    private Float namingClassGrade;
    @Field(value = "naming_class_err_messages")
    private List<String> namingClassErrMessages;

    @Field(value = "import_grade")
    private Float importGrade;
    @Field(value = "import_err_messages")
    private List<String> importErrMessages;

    @Field(value = "function_depth_grade")
    private Float functionDepthGrade;
    @Field(value = "function_depth_err_messages")
    private List<String> functionDepthMessages;

    public Grade(KeyFinderService keyFinderService) {
        this.keyFinderService = keyFinderService;

        this.codeId = this.keyFinderService.getCode().getId();

        initIndentationGradePlusErrMessages();
        initFunctionLengthGradePlusErrMessages();
        initLineLengthGradePlusErrMessages();
        initNamingVarGradePlusErrMessages();
        initNamingClassGradePlusErrMessages();
        initImportGradePlusErrMessages();
        initFunctionDepthGradePlusErrMessages();

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Float getIndentationGrade() {
        return indentationGrade;
    }

    public List<String> getIndentationErrMessages() {
        return indentationErrMessages;
    }

    public Float getFunctionLengthGrade() {
        return functionLengthGrade;
    }

    public List<String> getFunctionLengthErrMessages() {
        return functionLengthErrMessages;
    }

    public Float getLineLengthGrade() {
        return lineLengthGrade;
    }

    public List<String> getLineLengthErrMessages() {
        return lineLengthErrMessages;
    }

    public Float getNamingVarGrade() {
        return namingVarGrade;
    }

    public List<String> getNamingVarErrMessages() {
        return namingVarErrMessages;
    }

    public Float getImportGrade() {
        return importGrade;
    }

    public List<String> getImportErrMessages() {
        return importErrMessages;
    }

    public Float getNamingClassGrade() {
        return namingClassGrade;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getCodeId() {
        return codeId;
    }

    public Float getFunctionDepthGrade() {
        return functionDepthGrade;
    }

    public List<String> getFunctionDepthMessages() {
        return functionDepthMessages;
    }

    public List<String> getNamingClassErrMessages() {
        return namingClassErrMessages;
    }

    public int getAverage() {

        List<Float> average = new ArrayList<>();

        average.add(indentationGrade);
        average.add(functionLengthGrade);
        average.add(lineLengthGrade);
        average.add(importGrade);
        average.add(namingVarGrade);
        average.add(namingClassGrade);
        average.add(functionDepthGrade);

        Float res = (float) 0;
        int dividedBy = average.size();

        for (Float rate: average){
            if (!Float.isNaN(rate)){
                res = res + rate;
            } else {
                dividedBy--;
            }
        }
        if (dividedBy == 0){
            return 0;
        } else {
            return Math.round(res/dividedBy);
        }
    }

    private static String errMsgCreator(String typeOfError, String errorValue, String atElement) {
        return "Wrong " + typeOfError + " for value " + errorValue + " at " + atElement + ".";
    }

    private void initIndentationGradePlusErrMessages(){
        List<Integer> indentationValues = this.keyFinderService.getAllCodeIndentationValues();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (int i = 0; i < indentationValues.size(); i++) {
            if (indentationValues.get(i) % 4 != 0){
                cptWrong++;
                errMessages.add(errMsgCreator("indentation", indentationValues.get(i).toString(), "line " + i));
            }
        }
        this.indentationErrMessages = errMessages;
        this.indentationGrade = ((float)indentationValues.size()-(float)cptWrong)/(float)indentationValues.size()*10;
    }

    private void initFunctionLengthGradePlusErrMessages(){
        Hashtable<String, List<List<String>>> arrayOfFunctionBodyByNames = this.keyFinderService.getArrayOfFunctionBodyByNames();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (Map.Entry<String, List<List<String>>> set:
                keyFinderService.getArrayOfFunctionBodyByNames().entrySet()) {
            if (set.getValue().size() > 40){
                cptWrong++;
                Integer size = set.getValue().size();
                errMessages.add(errMsgCreator("function length", size.toString(), "function " + set.getKey()));
            }
        }

        this.functionLengthErrMessages = errMessages;
        this.functionLengthGrade = ((float)arrayOfFunctionBodyByNames.size()-(float)cptWrong)/(float)arrayOfFunctionBodyByNames.size()*10;
    }

    private void initLineLengthGradePlusErrMessages(){
        List<List<String>> arrayOfLinesOfSingleWordsForQuality = this.keyFinderService.getArrayOfLinesOfSingleWordsForQuality();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (int i = 0; i < arrayOfLinesOfSingleWordsForQuality.size(); i++) {
            if (arrayOfLinesOfSingleWordsForQuality.get(i).size() > 79){
                cptWrong++;
                Integer size = arrayOfLinesOfSingleWordsForQuality.get(i).size();
                errMessages.add(errMsgCreator("line length", size.toString(), "line " + i));
            }
        }

        this.lineLengthErrMessages = errMessages;
        this.lineLengthGrade = ((float)arrayOfLinesOfSingleWordsForQuality.size()-(float)cptWrong)/(float)arrayOfLinesOfSingleWordsForQuality.size()*10;
    }

    private void initNamingVarGradePlusErrMessages(){
        List<String> arrayOfVarName = this.keyFinderService.getArrayOfVariables();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (String varName: arrayOfVarName) {
            if (varName.equals("l") || varName.equals("O") || varName.equals("I")){
                cptWrong++;
                errMessages.add(errMsgCreator("indistinguishable variable naming", varName, "naming"));
            }
            if (!varName.matches("(?:[a-z](?:_[a-z])*)+")){
                cptWrong++;
                errMessages.add(errMsgCreator("variable naming convention", varName, "naming"));
            }
        }
        this.namingVarErrMessages = errMessages;
        this.namingVarGrade = ((float)arrayOfVarName.size()-(float)cptWrong)/(float)arrayOfVarName.size()*10;
    }

    private void initNamingClassGradePlusErrMessages(){
        List<String> arrayOfClassName = this.keyFinderService.getArrayOfClassName();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (String varClass: arrayOfClassName) {
            if (!varClass.matches("[A-Z]([a-zA-Z]*)")){
                cptWrong++;
                errMessages.add(errMsgCreator("CapWords class naming convention", varClass, "naming"));
            }
        }
        this.namingClassErrMessages = errMessages;
        this.namingClassGrade = ((float)arrayOfClassName.size()-(float)cptWrong)/(float)arrayOfClassName.size()*10;
    }

    private void initImportGradePlusErrMessages(){
        List<List<String>> arrayOfImport = this.keyFinderService.getArrayOfImports();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();

        for (List<String> line: arrayOfImport){
            if (!line.contains("from") && line.contains(",")) {
                cptWrong++;
                StringBuilder concatenate = new StringBuilder();
                for (String str: line){
                    concatenate.append(str);
                }
                List<String> imports = Arrays.asList(concatenate.toString().split("import")[1].split(","));
                errMessages.add(errMsgCreator("multi import", imports.toString(), "imports"));
            }
        }
        this.importErrMessages = errMessages;
        this.importGrade = ((float)arrayOfImport.size()-(float)cptWrong)/(float)arrayOfImport.size()*10;
    }

    private void initFunctionDepthGradePlusErrMessages(){
        Hashtable<String, Integer> arrayOfMaxDepthByFunctionName = this.keyFinderService.getArrayOfMaxDepthByFunctionName();
        int cptWrong = 0;
        List<String> errMessages = new ArrayList<>();


        for (Map.Entry<String, Integer> set:
                arrayOfMaxDepthByFunctionName.entrySet()) {
            if (set.getValue() > 4) {
                cptWrong++;
                errMessages.add(errMsgCreator("function depth", set.getValue().toString(), set.getKey()));
            }
        }

        this.functionDepthMessages = errMessages;
        this.functionDepthGrade = ((float)arrayOfMaxDepthByFunctionName.size()-(float)cptWrong)/(float)arrayOfMaxDepthByFunctionName.size()*10;
    }
}
