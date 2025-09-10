/* This is program gives the user the option to encrypt a file and decrypt a file using a known key.
 * It also gives the user the option to break the encryption of an encrypted file by figuring out the key used to encrypt it
 *
 * Author:	Samuel Costa and Ashley Gutierrez
 * Course:	COMP 4290
 * Assignment:	Project 1
 * Date:	9/12/2025
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Attack {
    // Maximum key length to try
    static final int KEY_LENGTHS = 15;
    // Size of alphabet (A-Z)
    static final int ALPHABET_SIZE = 26;
    // Expected English letter frequencies
    static final double[] ENGLISH_FREQ = {
            0.08167, // A
            0.01492, // B
            0.02782, // C
            0.04253, // D
            0.12702, // E
            0.02228, // F
            0.02015, // G
            0.06094, // H
            0.06966, // I
            0.00153, // J
            0.00772, // K
            0.04025, // L
            0.02406, // M
            0.06749, // N
            0.07507, // O
            0.01929, // P
            0.00095, // Q
            0.05987, // R
            0.06327, // S
            0.09056, // T
            0.02758, // U
            0.00978, // V
            0.02360, // W
            0.00150, // X
            0.01974, // Y
            0.00074  // Z
    };

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter file to analyze: ");
        String analyzeName = in.next(); // get input filename from user
        System.out.println();

        try {
            // Map to store letter counts for each possible key length
            Map<Integer, ArrayList<int[]>> lengthsMap = new HashMap<>();
            for (int i = 1; i <= KEY_LENGTHS; ++i) {
                ArrayList<int[]> alphabet = new ArrayList<>();
                // Create i arrays of size 26, one for each column of key length
                for (int j = 0; j < i; ++j) {
                    alphabet.add(new int[ALPHABET_SIZE]);
                }
                lengthsMap.put(i, alphabet); // store in map
            }

            // Read the ciphertext file
            File analyzeFile = new File(analyzeName);
            Scanner reader = new Scanner(analyzeFile);
            int index = 0;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                for (int i = 0; i < line.length(); ++i) {
                    // For each possible key length, increment counts in the correct column
                    for (int j = 1; j <= KEY_LENGTHS; ++j) {
                        ++lengthsMap.get(j).get(index % j)[line.charAt(i) - 'A'];
                    }
                    ++index; // move to next letter position
                }
            }
            reader.close();

            // Compute average Index of Coincidence (IC) for each key length
            double[] averageIc = new double[KEY_LENGTHS];
            int letterCount;
            int totalLetters;
            double icSum;
            for (int i = 1; i <= KEY_LENGTHS; ++i) {
                icSum = 0.0;
                for (int j = 0; j < i; ++j) {
                    int[] alphabet = lengthsMap.get(i).get(j);
                    letterCount = 0;
                    totalLetters = 0;
                    for (int value : alphabet) {
                        // Count pairwise combinations of letters in column
                        letterCount += value * (value - 1);
                        totalLetters += value;
                    }
                    // IC for this column
                    icSum += (double) letterCount / (totalLetters * (totalLetters - 1)) * ALPHABET_SIZE;
                }
                averageIc[i - 1] = icSum / i; // average IC for this key length
            }

            // Determine the key length with IC closest to 1.73 (English)
            int keyLength = 1;
            double current;
            double closest = abs(averageIc[0] - 1.73);
            for (int i = 0; i < averageIc.length; ++i) {
                System.out.printf("Length: %d\t\tIC: %.2f%n", i + 1, averageIc[i]);
                current = abs(averageIc[i] - 1.73);
                if (current < closest) {
                    closest = current;
                    keyLength = i + 1;
                }
            }
            System.out.println();

            // Recover the key using chi-squared analysis
            StringBuilder key = new StringBuilder();
            for(int pos = 0; pos < keyLength; ++pos) {

                int total = 0;
                int[] observedCounts = lengthsMap.get(keyLength).get(pos % keyLength);

                // Sum total letters in this column
                for (int c : observedCounts)
                    total += c;

                int bestShift = 0;
                double minChi2 = Double.MAX_VALUE;

                // Test all 26 possible shifts for this column
                for(int shift = 0; shift < 26; ++shift) {

                    int[] shiftedCounts = new int[26];
                    // Rotate counts to simulate applying shift
                    for (int i = 0; i < 26; i++) {
                        shiftedCounts[i] = observedCounts[(i + shift) % 26];
                    }

                    // Convert counts to frequencies
                    double[] observedFrequency = new double[26];
                    for (int i = 0; i < 26; i++) {
                        observedFrequency[i] = (double) shiftedCounts[i] / total;
                    }

                    // Compute chi-squared statistic
                    double chi2 = 0.0;
                    for (int i = 0; i < 26; i++) {
                        chi2 += Math.pow(observedFrequency[i] - ENGLISH_FREQ[i], 2) / ENGLISH_FREQ[i];
                    }

                    // Keep track of the minimum chi-squared (best shift)
                    if (chi2 < minChi2) {
                        minChi2 = chi2;
                        bestShift = shift;
                    }
                }

                // Append the recovered key letter for this column
                key.append((char)('A' + bestShift));
            }

            System.out.println("Recovered key: " + key);

            // Decrypt the file using the recovered key
            try {
                File inputFile = new File(analyzeName);
                reader = new Scanner(inputFile);
                String outputName = analyzeName + ".cracked";
                FileWriter writer = new FileWriter(outputName);
                index = 0;
                int newLetter;

                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    StringBuilder newLine = new StringBuilder();

                    for (int i = 0; i < line.length(); ++i) {
                        if (Character.isLetter(line.charAt(i))) {
                            int upperLetter = Character.toUpperCase(line.charAt(i)) - 'A';
                            int keyLetter = key.charAt(index) - 'A';

                            // Decrypt using Vigenère formula
                            newLetter = (upperLetter - keyLetter + 26) % 26;

                            newLine.append((char) (newLetter + 'A'));
                            index = (index + 1) % key.length(); // cycle key
                        }
                    }

                    writer.write(String.valueOf(newLine));
                    writer.write(System.lineSeparator());
                }

                reader.close();
                writer.close();
                System.out.println("Decrypted content written to " + outputName);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
