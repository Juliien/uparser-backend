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

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
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
//        for (List<String> line:  arrayOfLinesOfSingleWordsForQuality) {
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

    public static void main(String[] args) {
        Code code = Code.builder()
                .language("python")
                .extensionEnd("csv")
                .extensionEnd("json")
                .codeEncoded("ZnJvbSBWYWxsZWdhbWVvZmxpZmUuZ2FtZS5DZWxsIGltcG9ydCBDZWxsCmZyb20gVmFsbGVnYW1lb2ZsaWZlLmdhbWUuRm9ybSBpbXBvcnQgRm9ybQppbXBvcnQgcmFuZG9tCgoKY2xhc3MgUnVubmVyOgogICAgJycnQ2xhc3Mgd2ljaCBwcm92aWRlIGEgbWFwIG9mIGNlbGxzIGFuZAogICAgIG1hbmFnZSB0aGVpciBiZWhhdmlvdXIgdGhyb3VnaCBydWxlcycnJwoKICAgIHRvX3Jldml2ZSA9IFtdCiAgICB0b19raWxsID0gW10KICAgIGZvcm1zID0gRm9ybQoKICAgIGRlZiBfX2luaXRfXyhzZWxmLCBmb3Jtc19hbW91bnQ9NTAsIGRlbnNpdHk9MTAwLCBzaXplPTUwLCBpbml0aWFsX3N0YXR1cz0iZGVhZCIsIHJvdW5kcz01MDAwMCk6CiAgICAgICAgc2VsZi5kZW5zaXR5ID0gZGVuc2l0eQogICAgICAgIHNlbGYuc2l6ZSA9IHNpemUKICAgICAgICBzZWxmLm1hcCA9IHNlbGYuZ2VuZXJhdGVfbWFwKGluaXRpYWxfc3RhdHVzLCBzaXplKQogICAgICAgIHNlbGYuc2V0X2FkZHJlc3NlcygpCiAgICAgICAgc2VsZi5nZW5lcmF0ZV9mb3Jtcyhmb3Jtc19hbW91bnQpCiAgICAgICAgc2VsZi5yb3VuZHMgPSByb3VuZHMKCiAgICBkZWYgZ2VuZXJhdGVfbWFwKHNlbGYsIGluaXRpYWxfc3RhdHVzLCBzaXplKToKICAgICAgICByZXR1cm4gW1tDZWxsKChpICsgMSkgKiAoXyArIDEpLCBpbml0aWFsX3N0YXR1cykgZm9yIGkgaW4gcmFuZ2Uoc2l6ZSldIGZvciBfIGluIHJhbmdlKHNpemUpXQoKICAgIGRlZiBnZW5lcmF0ZV9mb3JtcyhzZWxmLCBhbW91bnQpOgogICAgICAgIGZvciBfIGluIHJhbmdlKGFtb3VudCk6CiAgICAgICAgICAgIHRyaWVzID0gMTAKICAgICAgICAgICAgc2hhcGUgPSBGb3JtLmdldF9zaGFwZSgpCiAgICAgICAgICAgIHBsYWNhYmxlID0gRmFsc2UKICAgICAgICAgICAgd2hpbGUgcGxhY2FibGUgaXMgbm90IFRydWUgYW5kIHRyaWVzID4gMDoKICAgICAgICAgICAgICAgIHRyaWVzIC09IDEKICAgICAgICAgICAgICAgIHJhbmRvbV9wb2ludCA9IHNlbGYuZ2V0X3JhbmRvbV9wb2ludCgpCiAgICAgICAgICAgICAgICBwbGFjYWJsZSA9IHNlbGYuY2hlY2tfcGxhY2UocmFuZG9tX3BvaW50LCBzaGFwZSkKICAgICAgICAgICAgICAgIGlmIHBsYWNhYmxlOgogICAgICAgICAgICAgICAgICAgIHNlbGYucGxhY2UocmFuZG9tX3BvaW50LCBzaGFwZSkKCiAgICAgICAgcmV0dXJuCgogICAgZGVmIGNoZWNrX3BsYWNlKHNlbGYsIG9yaWdpbiwgc2hhcGUpOgogICAgICAgIGZvciB4X2luZGV4IGluIHJhbmdlKEZvcm0uZ2V0X3NoYXBlX2Zvcm0oc2hhcGUpWyJ4Il0pOgogICAgICAgICAgICBmb3IgeV9pbmRleCBpbiByYW5nZShGb3JtLmdldF9zaGFwZV9mb3JtKHNoYXBlKVsieSJdKToKICAgICAgICAgICAgICAgIHggPSAob3JpZ2luWyd4J10gKyB4X2luZGV4KSAlIHNlbGYuc2l6ZQogICAgICAgICAgICAgICAgeSA9IChvcmlnaW5bJ3knXSArIHlfaW5kZXgpICUgc2VsZi5zaXplCiAgICAgICAgICAgICAgICBpZiBzZWxmLmlzX2ZyZWUoeCwgeSkgaXMgbm90IFRydWU6CiAgICAgICAgICAgICAgICAgICAgcmV0dXJuIEZhbHNlCiAgICAgICAgcmV0dXJuIFRydWUKCiAgICBkZWYgcGxhY2Uoc2VsZiwgb3JpZ2luLCBzaGFwZSk6CiAgICAgICAgZm9yIHhfaW5kZXggaW4gcmFuZ2UoRm9ybS5nZXRfc2hhcGVfZm9ybShzaGFwZSlbIngiXSk6CiAgICAgICAgICAgIGZvciB5X2luZGV4IGluIHJhbmdlKEZvcm0uZ2V0X3NoYXBlX2Zvcm0oc2hhcGUpWyJ5Il0pOgogICAgICAgICAgICAgICAgeCA9IChvcmlnaW5bJ3gnXSArIHhfaW5kZXgpICUgc2VsZi5zaXplCiAgICAgICAgICAgICAgICB5ID0gKG9yaWdpblsneSddICsgeV9pbmRleCkgJSBzZWxmLnNpemUKICAgICAgICAgICAgICAgIHNlbGYubWFwW3ldW3hdLnN0YXR1cyA9ICdhbGl2ZScgaWYgc2hhcGVbeV9pbmRleF1beF9pbmRleF0gPT0gJ2EnIGVsc2UgJ2RlYWQnCgogICAgICAgIHJldHVybgoKICAgIGRlZiBpc19mcmVlKHNlbGYsIHgsIHkpOgogICAgICAgIHggJT0gc2VsZi5zaXplCiAgICAgICAgeSAlPSBzZWxmLnNpemUKICAgICAgICByZXR1cm4gVHJ1ZSBpZiBzZWxmLm1hcFt5XVt4XS5zdGF0dXMgPT0gJ2RlYWQnIGVsc2UgRmFsc2UKCiAgICBkZWYgZ2V0X3JhbmRvbV9wb2ludChzZWxmKToKICAgICAgICByZXR1cm4gewogICAgICAgICAgICAneCc6IHJhbmRvbS5yYW5kaW50KDAsIHNlbGYuc2l6ZSAtIDEpLAogICAgICAgICAgICAneSc6IHJhbmRvbS5yYW5kaW50KDAsIHNlbGYuc2l6ZSAtIDEpCiAgICAgICAgfQoKICAgIGRlZiBzZXRfYWRkcmVzc2VzKHNlbGYpOgogICAgICAgIGZvciBpIGluIHJhbmdlKGxlbihzZWxmLm1hcCkpOgogICAgICAgICAgICB5ID0gaQogICAgICAgICAgICBmb3IgaiBpbiByYW5nZShsZW4oc2VsZi5tYXBbaV0pKToKICAgICAgICAgICAgICAgIGNlbGwgPSBzZWxmLm1hcFtpXVtqXQogICAgICAgICAgICAgICAgeCA9IGoKICAgICAgICAgICAgICAgIGNlbGwuc2V0X2FkZHJlc3MoeCwgeSkKCiAgICBkZWYgcnVuKHNlbGYpOgogICAgICAgIGlmIHNlbGYucm91bmRzIDwgMDoKICAgICAgICAgICAgcmV0dXJuCgogICAgICAgIGZvciBjZWxsX2xpbmUgaW4gc2VsZi5tYXA6CiAgICAgICAgICAgIGZvciBjZWxsIGluIGNlbGxfbGluZToKICAgICAgICAgICAgICAgIHNlbGYudHJ5X2tpbGwoY2VsbCkgaWYgY2VsbC5zdGF0dXMgPT0gJ2FsaXZlJyBlbHNlIHNlbGYudHJ5X3Jldml2ZShjZWxsKQogICAgICAgIHNlbGYudXBkYXRlKCkKICAgICAgICBzZWxmLnJlc2V0KCkKICAgICAgICBzZWxmLnJvdW5kcyAtPSAxCgogICAgZGVmIHRyeV9raWxsKHNlbGYsIGNlbGwpOgogICAgICAgIG5laWdoYm91cmhvb2QgPSBzZWxmLmdldF9uZWlnaGJvcmhvb2QoY2VsbCkKICAgICAgICBpZiBsZW4obmVpZ2hib3VyaG9vZCkgIT0gMiBhbmQgbGVuKG5laWdoYm91cmhvb2QpICE9IDM6CiAgICAgICAgICAgIHNlbGYudG9fa2lsbC5hcHBlbmQoY2VsbCkKCiAgICBkZWYgdHJ5X3Jldml2ZShzZWxmLCBjZWxsKToKICAgICAgICBuZWlnaGJvdXJob29kID0gc2VsZi5nZXRfbmVpZ2hib3Job29kKGNlbGwpCiAgICAgICAgaWYgbGVuKG5laWdoYm91cmhvb2QpID09IDM6CiAgICAgICAgICAgIHNlbGYudG9fcmV2aXZlLmFwcGVuZChjZWxsKQoKICAgIGRlZiBnZXRfbmVpZ2hib3Job29kKHNlbGYsIGNlbGwpOgogICAgICAgIG5laWdiaG9ycyA9IFtdCiAgICAgICAgdG9wX3lfaW5kZXggPSBjZWxsLmFkZHJlc3NbJ3knXSAtIFwKICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3knXSA+IDAgZWxzZSBzZWxmLnNpemUgLSAxCiAgICAgICAgYm90X3lfaW5kZXggPSBjZWxsLmFkZHJlc3NbJ3knXSArIFwKICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3knXSA8IHNlbGYuc2l6ZSAtIDEgZWxzZSAwCgogICAgICAgIGZvciB5IGluIFt0b3BfeV9pbmRleCwgY2VsbC5hZGRyZXNzWyd5J10sIGJvdF95X2luZGV4XToKICAgICAgICAgICAgbGVmdF94X2luZGV4ID0gY2VsbC5hZGRyZXNzWyd4J10gLSBcCiAgICAgICAgICAgICAgICAxIGlmIGNlbGwuYWRkcmVzc1sneCddID4gMCBlbHNlIHNlbGYuc2l6ZSAtIDEKICAgICAgICAgICAgcmlnaHRfeF9pbmRleCA9IGNlbGwuYWRkcmVzc1sneCddICsgXAogICAgICAgICAgICAgICAgMSBpZiBjZWxsLmFkZHJlc3NbJ3gnXSA8IHNlbGYuc2l6ZSAtIDEgZWxzZSAwCiAgICAgICAgICAgIGZvciB4IGluIFtsZWZ0X3hfaW5kZXgsIGNlbGwuYWRkcmVzc1sneCddLCByaWdodF94X2luZGV4XToKICAgICAgICAgICAgICAgIG5laWdiaG9ycy5hcHBlbmQoc2VsZi5tYXBbeV1beF0pCgogICAgICAgIHJldHVybiBbbiBmb3IgbiBpbiBuZWlnYmhvcnMgaWYgbi5zdGF0dXMgPT0gJ2FsaXZlJyBhbmQgbiBpcyBub3QgY2VsbF0KCiAgICBkZWYgdXBkYXRlKHNlbGYpOgogICAgICAgIGZvciBjZWxsIGluIHNlbGYudG9fcmV2aXZlOgogICAgICAgICAgICBjZWxsLnJldml2ZSgpCgogICAgICAgIGZvciBjZWxsIGluIHNlbGYudG9fa2lsbDoKICAgICAgICAgICAgY2VsbC5kaWUoKQoKICAgIGRlZiByZXNldChzZWxmKToKICAgICAgICBzZWxmLnRvX2tpbGwgPSBbXQogICAgICAgIHNlbGYudG9fcmV2aXZlID0gW10KCiAgICBkZWYgX19zdHJfXyhzZWxmKToKICAgICAgICByZXR1cm4gKCdcbmRlbnNpdHkgPSAnICsgc3RyKHNlbGYuZGVuc2l0eSkgKyAnXG5zaXplID0gJyArIHN0cihzZWxmLnNpemUpICsgJ1xucG9wdWxhdGlvbiA9IFxuJyArIHNlbGYuc3RyaW5naWZ5X21hcCgpKQoKICAgIGRlZiBzdHJpbmdpZnlfbWFwKHNlbGYpOgogICAgICAgIGZ1bGxfY2VsbHMgPSBbW3N0cihjZWxsKSBmb3IgY2VsbCBpbiBjZWxsX2FycmF5XQogICAgICAgICAgICAgICAgICAgICAgZm9yIGNlbGxfYXJyYXkgaW4gc2VsZi5tYXBdCiAgICAgICAgZm9yIGkgaW4gcmFuZ2UobGVuKGZ1bGxfY2VsbHMpKToKICAgICAgICAgICAgcHJpbnQoZnVsbF9jZWxsc1tpXSkKCiAgICAgICAgcmV0dXJuIHN0cihmdWxsX2NlbGxzKQ==")

//                .codeEncoded("IyBQeXRob24gcHJvZ3JhbSB0byBkaXNwbGF5IHRoZSBGaWJvbmFjY2kgc2VxdWVuY2UKCmRlZiByZWN1cl9maWJvICggbiApIDoKICAgaWYgbiA8PSAxOgogICAgICAgcmV0dXJuIG4KICAgZWxzZToKICAgICAgIHJldHVybihyZWN1cl9maWJvKG4tMSkgKyByZWN1cl9maWJvKG4tMikpCgpudGVybXMgPSAxMAoKIyBjaGVjayBpZiB0aGUgbnVtYmVyIG9mIHRlcm1zIGlzIHZhbGlkCmlmIG50ZXJtcyA8PSAwOgogICBwcmludCgiUGxlc2UgZW50ZXIgYSBwb3NpdGl2ZSBpbnRlZ2VyIikKZWxzZToKICAgcHJpbnQoIkZpYm9uYWNjaSBzZXF1ZW5jZToiKQogICBmb3IgaSBpbiByYW5nZShudGVybXMpOgogICAgICAgcHJpbnQocmVjdXJfZmlibyhpKSkK")
                .build();

        KeyFinderService keyFinderService = new KeyFinderService(code);

        Grade grade = new Grade(keyFinderService);
        System.out.println(grade.getIndentationErrMessages());
        System.out.println(grade.getIndentationGrade());
        System.out.println(grade.getFunctionLengthErrMessages());
        System.out.println(grade.getFunctionLengthGrade());
        System.out.println(grade.getLineLengthErrMessages());
        System.out.println(grade.getLineLengthGrade());
        System.out.println(grade.getImportErrMessages());
        System.out.println(grade.getImportGrade());
        System.out.println(grade.getNamingVarErrMessages());
        System.out.println(grade.getNamingVarGrade());
        System.out.println(grade.getNamingClassErrMessages());
        System.out.println(grade.getNamingClassGrade());
        System.out.println(grade.getFunctionDepthMessages());
        System.out.println(grade.getFunctionDepthGrade());
    }
    
}
