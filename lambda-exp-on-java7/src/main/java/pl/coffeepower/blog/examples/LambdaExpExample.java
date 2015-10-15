package pl.coffeepower.blog.examples;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.Random;

import static pl.coffeepower.blog.examples.NumberUtils.isEvenNumber;
import static pl.coffeepower.blog.examples.NumberUtils.isOddNumber;

@Log
public final class LambdaExpExample {

    @Getter
    private final List<Integer> numbers;
    private final int numbersSize = 20;

    private LambdaExpExample() {
        Random random = new Random();
        ImmutableList.Builder<Integer> listBuilder = ImmutableList.builder();
        for (int i = 0; i < numbersSize; i++) {
            listBuilder.add(random.nextInt(numbersSize));
        }
        numbers = listBuilder.build();
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
}
