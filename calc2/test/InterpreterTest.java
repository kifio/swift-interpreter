package calc2.test;

import calc2.Interpreter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class InterpreterTest {

    @Test
    public void testSum() {
        Interpreter interpreter = new Interpreter();
        assertEquals(7, interpreter.expr("3+4"));
        assertEquals(0, interpreter.expr("0+0"));
        assertEquals(18, interpreter.expr("9+9"));
    }

    @Test
    public void testSub() {
        Interpreter interpreter = new Interpreter();
        assertEquals(9, interpreter.expr("13-4"));
        assertEquals(0, interpreter.expr("0-0"));
        assertEquals(0, interpreter.expr("9-9"));
        assertEquals(81, interpreter.expr("90-9"));
    }

    @Test
    public void testMul() {
        Interpreter interpreter = new Interpreter();
        assertEquals(52, interpreter.expr("13*4"));
        assertEquals(0, interpreter.expr("0 * 0"));
        assertEquals(81, interpreter.expr("9 * 9"));
        assertEquals(810, interpreter.expr("90 * 9"));
    }

    @Test
    public void testDiv() {
        Interpreter interpreter = new Interpreter();
        assertEquals(3, interpreter.expr("13/4"));
        assertEquals(1, interpreter.expr("9 / 9"));
        assertEquals(10, interpreter.expr("90 / 9"));
        assertEquals(0, interpreter.expr("4 / 5"));
    }

    @Test
    public void testMultidigits() {
        Interpreter interpreter = new Interpreter();
        assertEquals(70, interpreter.expr("30+40"));
        assertEquals(100, interpreter.expr("0+100"));
        assertEquals(1000, interpreter.expr("999+1"));

        assertEquals(Integer.MAX_VALUE + 1, interpreter.expr(String.format("%d+%d", 1, Integer.MAX_VALUE)));
        assertEquals(Integer.MAX_VALUE, interpreter.expr(String.format("%d+%d", Integer.MAX_VALUE, 0)));
    }

    @Test
    public void testAnySumSubSequence() {
        Interpreter interpreter = new Interpreter();
        assertEquals(6, interpreter.expr("1+2+3"));
        assertEquals(-80, interpreter.expr("10 + 10 - 100"));
        assertEquals(0, interpreter.expr("9 + 1 - 1 - 9"));
    }

    @Test
    public void testWhitespaces() {
        Interpreter interpreter = new Interpreter();
        assertEquals(2, interpreter.expr(" 1  + 1 "));
        assertEquals(1, interpreter.expr("0   +1"));
        assertEquals(10, interpreter.expr("9+   1"));
        assertEquals(10, interpreter.expr("9   +   1"));
    }

    @Test
    public void testBadInput() {
        Interpreter interpreter = new Interpreter();
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("");
        });
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("   ");
        });
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("0");
        });
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("1000");
        });
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("+");
        });
        assertThrows(IllegalStateException.class, () -> {
            interpreter.expr("9 + 1 - 1 - - 9");
        });
        assertThrows(ArithmeticException.class, () -> {
            interpreter.expr("9/0");
        });
    }
}
