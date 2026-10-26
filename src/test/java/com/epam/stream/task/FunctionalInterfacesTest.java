package com.epam.stream.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalInterfacesTest {

    @Test
    public void a_predicate1() {
        Predicate<String> pred = s -> s != null && s.length() > 4;

        Assertions.assertTrue(pred.test("abcde"));
        Assertions.assertFalse(pred.test("abcd"));
        Assertions.assertFalse(pred.test(""));
    }

    @Test
    public void function1() {
        Function<String, String> func = s -> "(" + s + ")";

        assertEquals("(abc)", func.apply("abc"));
        assertEquals("()", func.apply(""));
    }

    @Test
    void c_consumer1() {
        StringBuilder sb = new StringBuilder("xyz");
        Consumer<StringBuilder> appenderConsumer = b -> b.append("abc");

        appenderConsumer.accept(sb);

        assertEquals("xyzabc", sb.toString());
    }

    @Test
    public void d_supplier1() {
        Supplier<StringBuilder> sup = () -> new StringBuilder("abc");

        assertEquals("abc", sup.get().toString());
    }
}
