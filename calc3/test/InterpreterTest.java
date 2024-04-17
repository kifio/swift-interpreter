package calc3.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import calc3.Interpreter;

public class InterpreterTest {

    @Test
    public void testAnyMulDivSequence() {
        Interpreter interpreter = new Interpreter();
        assertEquals(6, interpreter.expr("1*2*3"));
        assertEquals(1, interpreter.expr("10 * 10 / 100"));
        assertEquals(81, interpreter.expr("9 * 1 / 1 * 9"));
        assertEquals(150, interpreter.expr("10 * 1 * 2 * 3 / 6 * 15"));
        assertEquals(13, interpreter.expr("13 13"));
    }
}
