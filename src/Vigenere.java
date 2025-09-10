/* This is program gives the user the option to encrypt a file and decrypt a file using a known key.
 * It also gives the user the option to break the encryption of an encrypted file by figuring out the key used to encrypt it
 *
 * Author:	Samuel Costa and Ashley Gutierrez
 * Course:	COMP 4290
 * Assignment:	Project 1
 * Date:	9/12/2025
 */

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
            Scanner reader = new Scanner(inputFile);

            FileWriter writer = new FileWriter(outputName);

            String upperKeyPhrase = keyPhrase.toUpperCase();
            int index = 0; // Index to track position in key phrase
            int newLetter; // Will store the encrypted/decrypted letter

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                StringBuilder newLine = new StringBuilder();

                // Process each character in the line
                for (int i = 0; i < line.length(); ++i) {
                    if (Character.isLetter(line.charAt(i))) { // Only letters
                        // Convert letter to uppercase and get 0-25 index
                        int upperLetter = Character.toUpperCase(line.charAt(i)) - 'A';
                        int keyLetter = upperKeyPhrase.charAt(index) - 'A';

                        // Encrypt or decrypt based on user choice
                        if (choice.equals("e")) {
                            newLetter = (upperLetter + keyLetter) % 26; // Encryption
                        } else {
                            newLetter = (upperLetter - keyLetter + 26) % 26; // Decryption
                        }

                        // Convert back to character and append to new line
                        newLine.append((char) (newLetter + 'A'));

                        // Move to next character in key
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
