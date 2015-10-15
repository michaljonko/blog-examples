package pl.coffeepower.blog.examples;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Random;

@Log
public final class LambdaExpExample {

    @Getter
    private final List<Integer> numbers = Lists.newArrayList();
    private final int numbersSize = 20;

    private LambdaExpExample() {
        Random random = new Random();
        for (int i = 0; i < numbersSize; i++) {
            numbers.add(random.nextInt(2 * numbersSize) - numbersSize);
        }
    }

    public static void main(String[] args) {
        LambdaExpExample example = new LambdaExpExample();
        Joiner joiner = Joiner.on(", ");
        log.info("numbers: " + joiner.join(example.getNumbers()));
        log.info("strange sort numbers: " + joiner.join(example.getStrangeSortedNumbers()));
        log.info("odd numbers: " + joiner.join(example.getOddNumbers()));
        log.info("even numbers: " + joiner.join(example.getEvenNumbers()));
    }

    public final Iterable<Integer> getStrangeSortedNumbers() {
        return Ordering.<Integer>from((number1, number2) -> number2 * 2 - number1 / 2).sortedCopy(numbers);
    }

    public final Iterable<Integer> getOddNumbers() {
        return Iterables.filter(numbers, number -> isOddNumber(number));
    }

    public final Iterable<Integer> getEvenNumbers() {
        return Iterables.filter(numbers, number -> isEvenNumber(number));
    }

    private boolean isOddNumber(int number) {
        return number % 2 != 0;
    }

    private boolean isEvenNumber(int number) {
        return number % 2 == 0;
    }
}
