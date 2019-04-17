package enigma;

import java.util.Collection;


/** Class that represents a complete enigma machine.
 *  @author Yuhan Xie
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotor = numRotors;
        _numPawl = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[_numRotor];

    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotor;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawl;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {

        _rotors = new Rotor[_numRotor];

        for (int i = 0; i < rotors.length; i++) {
            for (Rotor r : _allRotors) {
                if (rotors[i].equals(r.name())) {
                    _rotors[i] = r;
                    break;
                }
            }
            if (_rotors[i] == null) {
                throw new EnigmaException("Illegal Rotor Name.");
            }
        }
        if (!_rotors[0].reflecting()) {
            throw new EnigmaException("Wrong position of reflector.");
        }
        int count = 0;
        for (int i = 0; i < _rotors.length; i++) {
            if (_rotors[i].rotates()) {
                count += 1;
            }
        }
        if (count > numPawls()) {
            throw new EnigmaException("Wrong number of moving rotors");
        }
    }
    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            _rotors[i + 1].set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int len = _rotors.length;
        boolean[] rotated = new boolean[len];
        for (int i = 0; i < len; i++) {
            if (i >= numRotors() - numPawls() && _rotors[i].atNotch()) {
                if (_rotors[i - 1].rotates()) {
                    _rotors[i].advance();
                    rotated[i] = true;
                    if (!rotated[i - 1]) {
                        _rotors[i - 1].advance();
                    }
                }
            }
        }
        if (!rotated[len - 1]) {
            _rotors[len - 1].advance();
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        for (int i = len - 1; i >= 0; i--) {
            c = _rotors[i].convertForward(c);
        }
        for (int j = 1; j < len; j++) {
            c = _rotors[j].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;
    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        int len = msg.length();
        char[] result = new char[len];
        for (int i = 0; i < len; i++) {
            result[i] = _alphabet.toChar
                    (this.convert(_alphabet.toInt(msg.charAt(i))));
        }
        return new String(result);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotor;

    /** Number of pawls. */
    private int _numPawl;

    /** Create collection of all rotor.*/
    private Collection<Rotor> _allRotors;

    /** Create collection of my rotor. */
    private Rotor[] _rotors;

    /** Create a plugboard that is a permutation. */
    private Permutation _plugboard;

}
