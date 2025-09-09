import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Math.abs;

public class Attack {
    static final int KEY_LENGTHS = 15;
    static final int ALPHABET_SIZE = 26;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter file to analyze: ");
        String analyzeName = in.next();

        try {
            Map<Integer, ArrayList<int[]>> lengthsMap = new HashMap<>();
            for (int i = 1; i <= KEY_LENGTHS; ++i) {
                ArrayList<int[]> alphabet = new ArrayList<>();
                for (int j = 0; j < i; ++j) {
                    alphabet.add(new int[ALPHABET_SIZE]);
                }
                lengthsMap.put(i, alphabet);
            }
            File analyzeFile = new File(analyzeName);
            Scanner reader = new Scanner(analyzeFile);
            int index = 0;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                for (int i = 0; i < line.length(); ++i) {
                    for (int j = 1; j <= KEY_LENGTHS; ++j) {
                        ++lengthsMap.get(j).get(index % j)[line.charAt(i) - 'A'];
                    }
                    ++index;
                }
            }
            reader.close();

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
                    for (int k = 0; k < alphabet.length; ++k) {
                        letterCount += alphabet[k] * (alphabet[k] - 1);
                        totalLetters += alphabet[k];
                    }
                    icSum += (double) letterCount / (totalLetters * (totalLetters - 1)) * ALPHABET_SIZE;
                }
                averageIc[i - 1] = icSum / i;
            }

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
            //System.out.println("Key Length: " + keyLength);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
