package util;

import com.sun.istack.internal.Nullable;

/**
 * @className: Preconditions
 * @description: a collection of static utility to validate input
 * @author: hone
 * @create: 2022/8/23 22:51
 */
public class Preconditions {
    /**
     * Ensures that the given object reference is not null.
     * Upon violation, a {@code NullPointerException} with no message is thrown.
     *
     * @param reference The object reference
     * @return The object reference itself (generically typed).
     *
     * @throws NullPointerException Thrown, if the passed reference was null.
     */
    public static <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    /**
     * Ensures that the given object reference is not null.
     * Upon violation, a {@code NullPointerException} with the given message is thrown.
     *
     * @param reference The object reference
     * @param errorMessage The message for the {@code NullPointerException} that is thrown if the check fails.
     * @return The object reference itself (generically typed).
     *
     * @throws NullPointerException Thrown, if the passed reference was null.
     */
    public static <T> T checkNotNull(@Nullable T reference, @Nullable String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
