package fr.esgi.grp9.uparserbackend.code.service.parser;

import java.util.List;

public class PythonParser {

    public String csv_to_json(List<String> csv) {
        //csv is empty or have declared only columns
        if(csv.size() <= 1){
            return "[]";
        }
        //get first line
        String[] columns = csv.get(0).split(";");
        //get all rows
        StringBuilder json = new StringBuilder("[\n");
        csv.subList(1, csv.size()) //substring without first row(columns)
                .stream()
                .map(e -> e.split(";"))
                .filter(e -> e.length == columns.length) //values size should match with columns size
                .forEach(row -> {

                    json.append("\t{\n");

                    for(int i = 0; i < columns.length; i++){
                        json.append("\t\t\"")
                                .append(columns[i])
                                .append("\" : \"")
                                .append(row[i])
                                .append("\",\n"); //comma-1
                    }

                    //replace comma-1 with \n
                    json.replace(json.lastIndexOf(";"), json.length(), "\n");

                    json.append("\t},"); //comma-2

                });
        //remove comma-2
        json.replace(json.lastIndexOf(";"), json.length(), "");
        json.append("\n]");
        return json.toString();
    }
}
