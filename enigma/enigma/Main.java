package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Yuhan Xie
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);


        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        if (!_input.hasNextLine()) {
            throw new EnigmaException("Missing config.");
        }
        String first = _input.nextLine();
        if (first.charAt(0) != '*') {
            throw new EnigmaException("Missing config.");
        }
        this.setUp(machine, first);

        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (next.length() == 0) {
                _output.println();
                continue;
            }
            if (next.charAt(0) == '*') {
                this.setUp(machine, next);

            } else {
                next = next.toUpperCase().replaceAll("\\s", "");
                printMessageLine(machine.convert(next));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String range = _config.nextLine();
            if (range.charAt(1) != '-') {
                _alphabet = new AlterAlphabet(range);
            } else {
                char start = range.charAt(0);
                _alphabet = new CharacterRange(start, range.charAt(2));
            }

            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            _config.nextLine();

            ArrayList<Rotor> rotors = new ArrayList<>();

            String curr, next = "";

            curr = _config.nextLine().trim();
            while (_config.hasNextLine()) {
                next = _config.nextLine().trim();
                if (next.length() == 0) {
                    if (_config.hasNextLine()) {
                        continue;
                    }
                    rotors.add(readRotor(curr));
                    break;
                }
                while (next.charAt(0) == '(') {
                    curr = curr + " " + next;
                    if (_config.hasNextLine()) {
                        next = _config.nextLine().trim();
                    } else {
                        break;
                    }
                }
                rotors.add(readRotor(curr));
                curr = next;
            }

            if (next.length() > 0 && !(next.charAt(0) == '(')) {
                rotors.add(readRotor(next));
            }
            return new Machine(_alphabet, numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config.
     * @param input is config.nextLine that describes Rotor
     * */
    private Rotor readRotor(String input) {
        try {

            String[] arrOfStr = input.trim().split(" ");
            String name = arrOfStr[0].toUpperCase();
            String cycle = input.substring(input.indexOf('('));


            Permutation permutation = new Permutation(cycle, _alphabet);
            char type = arrOfStr[1].charAt(0);
            if (type == 'M') {
                String notches = arrOfStr[1].substring(1);
                return new MovingRotor(name, permutation, notches);
            } else if (type == 'N') {
                return new FixedRotor(name, permutation);
            } else if (type == 'R') {
                return new Reflector(name, permutation);
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] rotors = new String[M.numRotors()];
        int index;
        settings = settings.trim().substring(2);
        try {
            for (int i = 0; i < M.numRotors(); i++) {
                index = settings.indexOf(' ');
                rotors[i] = settings.substring(0, index);
                settings = settings.substring(index + 1);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new EnigmaException("Wrong number of rotors.");
        }
        M.insertRotors(rotors);

        if (settings.indexOf('(') < 0) {
            if (settings.length() == 0) {
                throw new EnigmaException("Missing settings.");
            }
            M.setRotors(settings);
            return;
        }

        settings = settings.trim();
        index = settings.indexOf(' ');
        if (settings.charAt(0) == '(') {
            throw new EnigmaException("Missing settings.");
        }
        M.setRotors(settings.substring(0, index));
        String cycle = settings.substring(index + 1);
        M.setPlugboard(new Permutation(cycle, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String nowhitemsg = msg.trim();
        int len = nowhitemsg.length();
        for (int i = 0; i < len; i++) {
            _output.print(msg.charAt(i));
            if (i % 5 == 4) {
                _output.print(' ');
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
