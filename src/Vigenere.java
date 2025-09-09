import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Vigenere {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Encrypt or decrypt a file (e/d): ");
        String choice = in.next();
        if (choice.equals("e")) {
            System.out.print("Please enter file to encrypt: ");
        } else {
            System.out.print("Please enter file to decrypt: ");
        }
        String inputName = in.next();
        System.out.print("Enter key phrase (no spaces): ");
        String keyPhrase = in.next();
        System.out.print("Enter output file: ");
        String outputName = in.next();

        try {
            File inputFile = new File(inputName);
            System.out.println("Looking in: " + new File(".").getAbsolutePath());
            Scanner reader = new Scanner(inputFile);
            FileWriter writer = new FileWriter(outputName);
            String upperKeyPhrase = keyPhrase.toUpperCase();
            int index = 0;
            int newLetter;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                StringBuilder newLine = new StringBuilder();
                for (int i = 0; i < line.length(); ++i) {
                    if (Character.isLetter(line.charAt(i))) {
                        int upperLetter = Character.toUpperCase(line.charAt(i)) - 'A';
                        int keyLetter = upperKeyPhrase.charAt(index) - 'A';
                        if (choice.equals("e")) {
                            newLetter = (upperLetter + keyLetter) % 26;
                        } else {
                            newLetter = (upperLetter - keyLetter + 26) % 26;
                        }
                        newLine.append((char) (newLetter + 'A'));
                        index = (index + 1) % upperKeyPhrase.length();
                    }
                }
                writer.write(String.valueOf(newLine));
                writer.write(System.lineSeparator());
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
