package calc7.test;

import calc7.Interpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    @Test
    public void testAnyMulDivSequence() {
        Interpreter interpreter = new Interpreter();
        assertEquals(160, interpreter.interpret("10 * ((4 * 2)) * 2").calculate());
        assertEquals(160, interpreter.interpret("-10 * +(-(4 * 2)) * 2").calculate());
        assertEquals(30, interpreter.interpret("10 * 4  * (2 * 3) / 8").calculate());
        assertEquals(72, interpreter.interpret("(14 + 2) * (3 - -6) / 2").calculate());
        assertEquals(72, interpreter.interpret("(+14 + +2) * (+3 - -6) / +2").calculate());
        assertEquals(962, interpreter.interpret("2 * (3 + ((4 + 18))) - 87 +111 * 9").calculate());
        assertEquals(1076, interpreter.interpret("2 * 3 + 4 / 2 + 18 - 80 * 2 + 110 * (9 + 2)").calculate());
    }
}
