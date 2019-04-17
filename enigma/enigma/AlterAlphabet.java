package enigma;

import static enigma.EnigmaException.error;

/** An Alphabet of the characters of different order.
 * @author Yuhan Xie
 */
public class AlterAlphabet extends Alphabet {

    /**
     * @param input the string that contains alphabet. */
    AlterAlphabet(String input) {
        alphabet = input;
    }

    @Override
    int size() {
        return alphabet.length();
    }

    @Override
    boolean contains(char ch) {
        for (int i = 0; i < alphabet.length(); i++) {
            if (ch == alphabet.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    char toChar(int index) {
        if (index < 0 || index >= alphabet.length()) {
            throw error("character index out of range");
        }
        return alphabet.charAt(index);
    }

    @Override
    int toInt(char ch) {
        if (!contains(ch)) {
            throw error("character out of range");
        }
        return alphabet.indexOf(ch);
    }
    /** Range of characters in this Alphabet. */
    private String alphabet;
}
