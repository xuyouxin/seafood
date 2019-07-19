package top.xuyx.seafood.common;

import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T> List<T> filter(Collection<T> unfiltered, Predicate<T> predicate) {
        return unfiltered.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> filter(T[] unfiltered, Predicate<T> predicate) {
        return Arrays.stream(unfiltered).filter(predicate).collect(Collectors.toList());
    }

    public static <T> List<T> reject(Collection<T> unfiltered, Predicate<T> predicate) {
        return filter(unfiltered, predicate.negate());
    }

    public static <F, T> Set<T> mapToSet(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).collect(Collectors.toSet());
    }

    public static <F, T> List<T> map(Collection<F> from, Function<? super F, T> mapper) {
        return from.stream().map(mapper).collect(Collectors.toList());
    }

    public static <F, T> List<T> map(F[] from, Function<? super F, T> mapper) {
        return Arrays.stream(from).map(mapper).collect(Collectors.toList());
    }

    public static <F, T> List<T> map(Stream<F> from, Function<? super F, T> mapper) {
        return from.map(mapper).collect(Collectors.toList());
    }

    public static <K, V> Map<K, V> toMap(Stream<V> from, Function<V, K> keyMapper) {
        return toMap(from, keyMapper, v -> v);
    }

    public static <K, V> Map<K, V> toMap(Collection<V> from, Function<V, K> keyMapper) {
        return toMap(from.stream(), keyMapper, v -> v);
    }

    public static <K, V, F> Map<K, V> toMap(Collection<F> from, Function<F, K> keyMapper, Function<F, V> valueMapper) {
        return toMap(from.stream(), keyMapper, valueMapper);
    }

    public static <K, V, F> Map<K, V> toMap(Stream<F> from, Function<F, K> keyMapper, Function<F, V> valueMapper) {
        return from.collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <K, V> Map<K, List<V>> groupingBy(Collection<V> from, Function<V, K> classifier) {
        return groupingBy(from.stream(), classifier);
    }

    public static <K, V> Map<K, List<V>> groupingBy(Stream<V> fromStream, Function<V, K> classifier) {
        return fromStream.collect(Collectors.groupingBy(classifier));
    }

    public static <S> BigDecimal sum(Collection<S> src, Function<S, BigDecimal> valueFn) {
        return src.stream().map(valueFn).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <S> BigDecimal sum(Collection<S> src, ToIntFunction<S> valueFn) {
        return src.stream().map(t -> new BigDecimal(valueFn.applyAsInt(t))).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static <T> List<T> flatMap(List<List<T>> from) {
        return from.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static <R, T> List<T> flatMap(Collection<R> from, Function<R, Stream<T>> mapper) {
        return from.stream().flatMap(mapper).collect(Collectors.toList());
    }

    @Nullable
    public static <T extends Comparable> T naturalOrderingMax(Stream<T> stream) {
        return stream.max(Ordering.natural()).orElse(null);
    }

    @Nullable
    public static <T extends Comparable> T naturalOrderingMin(Stream<T> stream) {
        return stream.min(Ordering.natural()).orElse(null);
    }

    public static long distinctCount(Stream<?> stream) {
        return stream.distinct().count();
    }

    public static <T> List<T> sorted(Collection<T> from, Comparator<T> comparator) {
        return from.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static <K, V> Map<K, V> zipToMap(Stream<K> keyStream, Stream<V> valueStream) {
        //noinspection UnstableApiUsage
        return Streams.zip(keyStream, valueStream, Pair::of)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }
}
