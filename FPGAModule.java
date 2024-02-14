

import java.io.FileWriter;
import java.io.IOException;


import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FPGAModule {

    String name;
    ArrayList<String> fileContent;
    ArrayList<ModuleParameter> moduleParameters;
    Boolean clockReset;

    public FPGAModule(String outputFilePath) {
        fileContent = readFile(outputFilePath);
        name = getModuleName();
        //getparameters needs to come here...
        moduleParameters = getModuleParameters();

    }

    public ArrayList<ModuleParameter> getModuleParameters() {
        moduleParameters = new ArrayList<>();
        boolean insideDeclarations = false;

        for (String lineContent : fileContent) {
            if (lineContent.contains(")")) {
                insideDeclarations = false;
            }

            if (lineContent.startsWith("input")
                    || lineContent.startsWith("output")
                    || lineContent.startsWith("inout")) {
                ModuleParameter moduleParameter = new ModuleParameter(lineContent);

                moduleParameters.add(moduleParameter);

            }
            if (lineContent.contains("(")) {
                insideDeclarations = true;
            }
        }
        return moduleParameters;
    }

    public ArrayList<String> readFile(String outputFilePath) {
        ArrayList<String> lines = new ArrayList<>();

        try ( Scanner fileReader = new Scanner(Paths.get(outputFilePath))) {
            while (fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine().trim());
            }
        } catch (Exception e) {
            System.out.println("Reading the file failed.");
        }

        return lines;
    }

//        public void writeFile(String filePath, ArrayList<String> lines){
//            
//            try{
//                FileWriter writer = new FileWriter(filePath);
//                
//                for(String line : lines){
//                    writer.write(line);
//                    writer.write("\n");
//                }
//                writer.close(); // Dosya yazmayı bitirdikten sonra kapatmayı unutmayın
//            System.out.println("File written successfully: " + filePath);
//        } catch (IOException e) {
//            System.out.println("Failed to write the file: " + e.getMessage());            
//            }
//        return ;
//        }
        


    public String getModuleName() {
        String targetWord = "module";
        String moduleNameFound = "";

        //amacimiz bunda sonra gelen kelimeyi bulmak
        for (String line : fileContent) {
            if (line.contains(targetWord)) {
                String[] moduleWords = line.split(" ");
                if (moduleWords.length >= 2) {
                    moduleNameFound = moduleWords[1].replace("(", "");
                    //System.out.println(moduleNameFound);
                }
                break;// we have already found module_name!!!
            }
        }

        return moduleNameFound;
    }

    public ArrayList<Boolean> getClockReset(String outputFilePath) {
        ArrayList<Boolean> clockResetValues = new ArrayList<>();
        try ( Scanner fileReader = new Scanner(Paths.get(outputFilePath))) {
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine().toLowerCase();
                boolean hasClock = line.contains("clock") || line.contains("clk");
                boolean hasReset = line.contains("reset") || line.contains("rst");
                clockResetValues.add(hasClock || hasReset);

            }
        } catch (Exception e) {
            System.out.println("Reading the file failed.");
        }
        return clockResetValues;
    }

    public String getClockName() {
        String clockName = "";

        for (ModuleParameter moduleParameter : moduleParameters) {
            if (moduleParameter.isClock) {
                clockName = moduleParameter.name;
            }
        }

        return clockName;
    }

    public String getResetName() {
        String resetName = "";

        for (ModuleParameter moduleParameter : moduleParameters) {
            if (moduleParameter.isReset) {
                resetName = moduleParameter.name;
            }
        }

        return resetName;
    }

    public String assignValueToParameter(String parameterName, int value) {
        StringBuilder result = new StringBuilder();
        result.append("\t");
        result.append(parameterName);
        result.append(" = ");
        result.append(value);
        result.append(";");
        result.append("\n");

        return result.toString();
    }
   
    public String addClockCycles(int cycleCount) { //(String parameterName, int cycleCount)
        StringBuilder result = new StringBuilder();
        result.append("\n\t");
        result.append("repeat (10) @(posedge ");
        result.append(getClockName());
        result.append(");\n");
        
        return result.toString();
    }
    
    @Override
public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("`timescale 1ns / 1ps\n\n");
        result.append("module tb_" + name + ";" + "\n");

        for (ModuleParameter moduleParameter : moduleParameters) {
            result.append(moduleParameter);
        }

        result.append(name);
        result.append(" ttb_");
        result.append(name);
        result.append("\n");
        result.append("(");
        result.append("\n");

        for (ModuleParameter moduleParameter : moduleParameters) {
            result.append("\t");
            result.append(".");
            result.append(moduleParameter.name);
            result.append("(");
            result.append(moduleParameter.name);
            result.append("),");
            result.append("\n");
        }

        result.deleteCharAt(result.length() - 2);

        result.append(");\n");

        //"initial \n" +
        //"begin"
        result.append("\n");
        result.append("initial\n");
        result.append("begin\n");

        // her bir parametreyi initial olarak 0'a esitle!
        //sin_val_min = 0;
        //tek istisna rst olani 1'e esitle
        for (ModuleParameter moduleParameter : moduleParameters) {
            if (moduleParameter.isReset) {
                String createdLine= assignValueToParameter(moduleParameter.name, 1);
                result.append(createdLine);
                
            } else {
                String createdLine= assignValueToParameter(moduleParameter.name, 0);
                result.append(createdLine);
                //result.append(assignValueToParameter(moduleParameter.name, 0));
            }
        }
        
        for(ModuleParameter moduleParameter : moduleParameters){
            if(moduleParameter.isClock){
               String createdLine = addClockCycles(10);
               result.append(createdLine);
//            }else {
//                String createdLine = addClockCycles(moduleParameter.name);
//                result.append(createdLine);
//            }
        }

        //TODO : make it function
//        result.append("\n\t");
//        result.append("repeat (10) @(posedge ");
//        result.append(getClockName());
//        result.append(");\n");

        //rst_in = 1;
        result.append(assignValueToParameter(getResetName(), 0));//reset state
        result.append("\n\t");
        result.append("repeat (10) @(posedge ");
        result.append(getClockName());
        result.append(");\n");
       result.append(addClockCycles(10));
        result.append(assignValueToParameter(getResetName(), 1));//reset release state
        
        result.append("\n");
        result.append("end\n\n");
        
        
        result.append("always #20 ");
        result.append(getClockName());
        result.append(" <= ~");
        result.append(getClockName());
        result.append(";\n");
        
        
        result.append("\nendmodule\n");
        
        return result.toString();
    }
        return null;
       
    }

    
}
