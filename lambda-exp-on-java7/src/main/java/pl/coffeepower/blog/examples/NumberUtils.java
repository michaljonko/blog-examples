package pl.coffeepower.blog.examples;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NumberUtils {

    public static final boolean isOddNumber(Number number) {
        return number.intValue() % 2 != 0;
    }

    public static final boolean isEvenNumber(Number number) {
        return number.intValue() % 2 == 0;
    }
}
