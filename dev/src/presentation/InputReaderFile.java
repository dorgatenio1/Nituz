package Presentation_Layer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;

public class InputReaderFile implements InputReader {
    private Stack<String> inputs;

    public InputReaderFile(String pathToTestFile) {
        inputs = new Stack<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(pathToTestFile));
            for(int i=lines.size()-1;i>=0;i--) {
                if (lines.get(i).length()>=1 && !lines.get(i).startsWith("/")) {
                    inputs.push(lines.get(i));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readString() {
        if( !inputs.isEmpty())
            return inputs.pop();
        else
            return "";
    }

    public int readInt() {
        return Integer.parseInt(readString());
    }
}
