package pl.coffeepower.blog.examples;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import lombok.extern.java.Log;

import java.util.List;
import java.util.Random;

import java8.util.stream.Collectors;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

@Log
public final class StreamsExample {

    private final List<Integer> numbers;
    private final int numbersSize = 10;

    private StreamsExample() {
        Random random = new Random();
        ImmutableList.Builder<Integer> listBuilder = ImmutableList.builder();
        for (int i = 0; i < numbersSize; i++) {
            listBuilder.add(random.nextInt(numbersSize));
        }
        numbers = listBuilder.build();
    }

    public static void main(String[] args) {
        StreamsExample example = new StreamsExample();
        log.info("odd numbers: " + Joiner.on(", ").join(example.getOddNumbers()));
        example.changeOddToEvenNumbers()
                .forEach(number -> log.info("odd value changed to even:" + (++number)));
    }

    public final List<Integer> getOddNumbers() {
        return StreamSupport.stream(numbers)
                .filter(number -> NumberUtils.isOddNumber(number))
                .collect(Collectors.toList());
    }

    public final Stream<Integer> changeOddToEvenNumbers() {
        return StreamSupport.stream(numbers)
                .filter(number -> NumberUtils.isOddNumber(number));
    }
}
