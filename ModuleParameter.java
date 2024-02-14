
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ModuleParameter {

    enum Direction {
        IN,
        OUT,
        INOUT
    };

    String name;
    int length;
    String lengthStr = "";
    boolean signed = false;
    Direction direction; //it can be one of in, out, inout

    boolean isClock;
    boolean isReset;

    public ModuleParameter(String lineContent) {
        //remove comment part of the line
        if (lineContent.contains("//")) {
            int beginIndex = lineContent.indexOf("//");
            lineContent = lineContent.substring(0, beginIndex);
        }

        //if we split line content, then last part is name of parameter
        String[] splittedLineContent = lineContent.split(" ");
        name = splittedLineContent[splittedLineContent.length - 1].replace(",", "");

        if (lineContent.contains("input")) {
            direction = Direction.IN;
        } else if (lineContent.contains("output")) {
            direction = Direction.OUT;
        } else if (lineContent.contains("inout")) {
            direction = Direction.INOUT;
        }

        if (lineContent.contains("signed")) {
            signed = true;
        }

        if (lineContent.contains("[") && lineContent.contains("]")) {
            int beginIndex = lineContent.indexOf("[");
            int endIndex = lineContent.indexOf("]");
            lengthStr = lineContent.substring(beginIndex, endIndex + 1);
        }

        isClock = (lineContent.toLowerCase().contains("clock"))
                || lineContent.toLowerCase().contains("clk");
        isReset = (lineContent.toLowerCase().contains("reset"))
                || lineContent.toLowerCase().contains("rst");
    }

    @Override
    public String toString() {
        String outputStr = "";
        outputStr += (direction == Direction.IN ? "reg " : "wire ");

        outputStr += (signed ? "signed " : "");
        outputStr += (lengthStr.equals("") ? "" : (lengthStr + " "));
        outputStr += name;
        outputStr += ";\n";

        return outputStr;

//        return "Name:" + name + "\n"
//                + "Direction: " + direction + "\n"
//                + "LengthStr:" + (lengthStr.equals("") ? "1" : lengthStr) + "\n"
//                + "isClock:" + isClock + "\n"
//                + "isReset:" + isReset + "\n";
        //...
    }
}
