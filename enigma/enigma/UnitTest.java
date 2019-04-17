package enigma;
import ucb.junit.textui;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

/** The suite of all JUnit tests for the enigma package.
 *  @author Yuhan Xie
 */
public class UnitTest {

    /**
     * Run the JUnit tests in this package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(PermutationTest.class, MovingRotorTest.class);
    }

    @Test
    public void testInvertChar() {
        CharacterRange range = new CharacterRange('A', 'Z');
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)", range);
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');

    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        ArrayList<Rotor> list = new ArrayList<>(Arrays.asList(machineRotors));
        Machine mach = new Machine(ac, 4, 3, list);
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert("ABC");
        assertEquals("AABA", getSetting(ac, machineRotors));
    }

    /** Helper method to get the String representation.*/
    private String getSetting(Alphabet alph, Rotor[]machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }
}


