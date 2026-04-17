package Presentation_Layer;

import java.util.Scanner;

public class InputReaderScanner implements InputReader {
    private Scanner scanner;
    public InputReaderScanner(Scanner scanner){
        this.scanner=scanner;
    }
    @Override
    public String readString() {
        return scanner.nextLine().trim();
    }
    @Override
    public int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Return an invalid option to trigger the default case
        }
    }
}
