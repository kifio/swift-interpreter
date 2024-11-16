package calc4.test;

import calc4.Interpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpreterTest {

    @Test
    public void testAnyMulDivSequence() {
        Interpreter interpreter = new Interpreter();
        assertEquals(30, interpreter.interpret("10 * 4  * 2 * 3 / 8"));
        assertEquals(17, interpreter.interpret("14 + 2 * 3 - 6 / 2"));
        assertEquals(940, interpreter.interpret("2 * 3 + 4 + 18 - 87 +111 * 9"));
        assertEquals(858, interpreter.interpret("2 * 3 + 4 / 2 + 18 - 80 * 2 + 110 * 9 + 2"));
        assertEquals(650, interpreter.interpret("1/1/1/1/1/1/1/1 + 2/2*2/2 + 3 * 3 * 3 * 3 * 4 * 2"));
    }
}
