package fr.esgi.grp9.uparserbackend.code.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvSchema.Builder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.io.StringWriter;


public class PythonParser {

//    public String csv_to_json(List<String> csv) {
//        //csv is empty or have declared only columns
//        if (csv.size() <= 1) {
//            return "[]";
//        }
//        //get first line
//        String[] columns = csv.get(0).split(";");
//        //get all rows
//        StringBuilder json = new StringBuilder("[\n");
//        csv.subList(1, csv.size()) //substring without first row(columns)
//                .stream()
//                .map(e -> e.split(";"))
//                .filter(e -> e.length == columns.length) //values size should match with columns size
//                .forEach(row -> {
//
//                    json.append("\t{\n");
//
//                    for (int i = 0; i < columns.length; i++) {
//                        json.append("\t\t\"")
//                                .append(columns[i])
//                                .append("\" : \"")
//                                .append(row[i])
//                                .append("\",\n"); //comma-1
//                    }
//
//                    //replace comma-1 with \n
//                    json.replace(json.lastIndexOf(";"), json.length(), "\n");
//
//                    json.append("\t},"); //comma-2
//
//                });
//        //remove comma-2
//        json.replace(json.lastIndexOf(";"), json.length(), "");
//        json.append("\n]");
//        return json.toString();
//    }

   /* public String csv_to_xml(List<String> csv) {
        //csv is empty or have declared only columns
        if (csv.size() <= 1) {
            return "[]";
        }
        //get first line
        String[] columns = csv.get(0).split(";");
        //get all rows
        StringBuilder xml = new StringBuilder("[\n");
        csv.subList(1, csv.size()) //substring without first row(columns)
                .stream()
                .map(e -> e.split(";"))
                .filter(e -> e.length == columns.length) //values size should match with columns size
                .forEach(row -> {

                    xml.append("\t{\n");


                });
        return xml.toString();
    }*/

    public String json_to_xml(String json){
        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = jsonMapper.readValue(json, JsonNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, true);
        StringWriter w = new StringWriter();
        try {
            xmlMapper.writeValue(w, node);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return w.toString();
    }


    public String xml_to_json(String xml) {
        JSONObject json = null;
        try {
            json = XML.toJSONObject(xml);
            String jsonString = json.toString(4);
            return jsonString;
        } catch (JSONException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public String json_to_csv(String json) throws JsonProcessingException {
        if(json.charAt(0) != '[') {
            StringBuilder sb = new StringBuilder(json);
            sb.insert(0, '[');
            sb.insert(json.length() + 1, ']');
            json = sb.toString();
        }
        JsonNode jsonTree = new ObjectMapper().readTree(json);
        StringWriter w = new StringWriter();
        //ReadCsv
        Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {
            csvSchemaBuilder.addColumn(fieldName);} );
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        //WriteCSV
        CsvMapper csvMapper = new CsvMapper();
        try {
            csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema).writeValue(w,jsonTree);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return w.toString().replace(",",";");
    }
}