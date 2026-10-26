package com.epam.stream.task;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTasks {

    // Domain record definition
    public record Prop(UUID id, String name, int value) {}

    // Task 1: Stream Generation
    public Stream<Prop> generate(int n) {
        return java.util.stream.IntStream.range(0, n)
                .mapToObj(i -> new Prop(UUID.randomUUID(), "Prop_" + i, i));
    }

    // Task 2: Flattening
    public Stream<UUID> toIds(List<List<Prop>> nestedList) {
        return nestedList.stream()
                .flatMap(Collection::stream)
                .map(Prop::id);
    }

    // Task 3: Counts
    public long countEven(Stream<Integer> stream) {
        return stream
                .filter(Objects::nonNull)
                .filter(n -> n % 2 == 0)
                .count();
    }

    // Task 4: Shortening Operations
    public Stream<Character> extractFirstWord(Stream<Character> stream) {
        return stream
                .dropWhile(Character::isWhitespace)
                .takeWhile(c -> !Character.isWhitespace(c));
    }

    // Task 5: Indirect Mapping
    public Stream<String> extractNonNullProperties(Stream<Prop> stream) {
        return stream.flatMap(prop -> Stream.of(
                prop.id() != null ? prop.id().toString() : null,
                prop.name(),
                Integer.toString(prop.value())
        )).filter(Objects::nonNull);
    }

    // Task 6: Sorting
    public List<Prop> sortByValueThenName(Stream<Prop> stream) {
        return stream
                .sorted(Comparator.comparingInt(Prop::value)
                        .thenComparing(Prop::name, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    // Task 7: Filter by Property
    public List<Prop> filterNonNullName(Stream<Prop> stream) {
        return stream
                .filter(prop -> prop.name() != null)
                .toList();
    }

    // Task 8: Stateful filter
    public Stream<Prop> removeDuplicateIds(Stream<Prop> stream) {
        Set<UUID> seenIds = java.util.concurrent.ConcurrentHashMap.newKeySet();
        return stream.filter(prop -> prop.id() != null && seenIds.add(prop.id()));
    }

    // Task 9: Aggregation
    public Optional<String> getNameWithHighestValue(Stream<Prop> stream) {
        return stream
                .max(Comparator.comparingInt(Prop::value))
                .map(Prop::name);
    }

    // Task 10: Combining Collectors
    public Map<String, List<Prop>> findNameConflicts(Stream<Prop> stream) {
        return stream
                .filter(p -> p.name() != null && p.id() != null)
                .collect(Collectors.groupingBy(Prop::name))
                .entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(Prop::id).distinct().count() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Task 11: Stateful Collectors
    public Map<String, Integer> sumValuesByName(Stream<Prop> stream) {
        return stream
                .filter(p -> p.name() != null)
                .collect(Collectors.groupingBy(
                        Prop::name,
                        Collectors.summingInt(Prop::value)
                ));
    }

    // Task 12: Collector Chaining
    public Map<Boolean, Integer> sumEvenAndOdd(Stream<Integer> stream) {
        return stream
                .filter(Objects::nonNull)
                .collect(Collectors.partitioningBy(
                        n -> n % 2 == 0,
                        Collectors.summingInt(Integer::intValue)
                ));
    }

    // Task 13: Custom Aggregation
    public record MinMaxNames(String lowestName, String highestName) {}

    public Optional<MinMaxNames> getLowestAndHighestNames(Stream<Prop> stream) {
        return stream.collect(Collectors.teeing(
                Collectors.minBy(Comparator.comparingInt(Prop::value)),
                Collectors.maxBy(Comparator.comparingInt(Prop::value)),
                (min, max) -> (min.isPresent() && max.isPresent())
                        ? Optional.of(new MinMaxNames(min.get().name(), max.get().name()))
                        : Optional.empty()
        ));
    }

}
