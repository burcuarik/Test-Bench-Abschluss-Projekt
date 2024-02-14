import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class mainProgram {
    public static void main(String[] args) throws FileNotFoundException { //baslangic noktasi

        if (args.length != 1) {
            System.out.println("Usage: java mainProgram <outputFilePath>");
            return;
        }

        String outputFilePath = args[0];


        FPGAModule fpgaModule = new FPGAModule(outputFilePath);
        System.out.println(fpgaModule);
        try(PrintWriter out = new PrintWriter("./demo_test.v")){
            out.println(fpgaModule);
        }
    }
}
