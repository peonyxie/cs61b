package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Yuhan Xie
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        size = alphabet.size();
        pointlist = new int[size];
        for (int i = 0; i < size; i++) {
            pointlist[i] = -1;
        }
        while (cycles.trim().length() != 0) {
            int front = cycles.indexOf('(');
            int back = cycles.indexOf(')');
            if (front < 0 || back < 0) {
                throw new EnigmaException("Missing parenthesis");
            }
            String cycle = cycles.substring(front + 1, back);
            addCycle(cycle);
            cycles = cycles.substring(cycles.indexOf(')') + 1);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        int[] number = new int[cycle.length()];
        for (int i = 0; i < cycle.length(); i++) {
            number[i] = _alphabet.toInt(cycle.charAt(i));
        }
        for (int i = 0; i < cycle.length() - 1; i++) {
            pointlist[number[i]] = number[i + 1];
        }
        pointlist[number[cycle.length() - 1]] = number[0];
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return size;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);
        if (pointlist[p] == -1) {
            return p;
        }
        return pointlist[p];
    }


    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);
        if (pointlist[c] == -1) {
            return c;
        }
        for (int i = 0; i < pointlist.length; i++) {
            if (pointlist[i] == c) {
                return i;
            }
        }
        return -1;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));

    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < pointlist.length; i++) {
            if ((pointlist[i]) == -1) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** intlist of the wanted permutation value. */
    private int[] pointlist;
    /** size of the alphabet. */
    private int size;

}
