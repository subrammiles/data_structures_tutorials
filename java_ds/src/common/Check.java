package common;

import java.util.Arrays;
import java.util.Objects;

/*
 * Small helper so every tutorial checks itself.
 * Run any main(): you see PASS lines, or an AssertionError if something is wrong.
 */
public final class Check {

    private Check() {
    }

    public static void eq(String label, Object actual, Object expected) {
        if (!Objects.deepEquals(actual, expected)) {
            throw new AssertionError("FAIL " + label + " -> expected " + str(expected) + " but got " + str(actual));
        }
        System.out.println("PASS " + label + " = " + str(actual));
    }

    public static void isTrue(String label, boolean condition) {
        eq(label, condition, true);
    }

    public static void throwsException(String label, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            System.out.println("PASS " + label + " threw " + e.getClass().getSimpleName());
            return;
        }
        throw new AssertionError("FAIL " + label + " -> expected an exception");
    }

    private static String str(Object o) {
        String s = Arrays.deepToString(new Object[]{o});
        return s.substring(1, s.length() - 1);
    }
}
