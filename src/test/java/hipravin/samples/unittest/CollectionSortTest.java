package hipravin.samples.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionSortTest {

    Random random;

    @BeforeEach
    void setUp() {
        //обязательно фиксируем seed, чтобы тест можно было повторить
        random = new Random(0);
    }

    @RepeatedTest(100)
    void testArraysNotSorted() {
        long[] original = randomLongArray(100_000);
        long[] sorted = copy(original);
        Arrays.sort(sorted);
        sorted[1] = sorted[0];//имитируем ошибку в результате

        assertThrows(AssertionFailedError.class, () -> {
            checkArraySortedCorrectly(original, sorted);
        });
    }

    @RepeatedTest(100)
    void testArraysSort() {
        long[] original = randomLongArray(100_000);
        long[] sorted = copy(original);
        Arrays.sort(sorted);

        checkArraySortedCorrectly(original, sorted);
    }

    void checkArraySortedCorrectly(long[] original, long[] sorted) {
        long sumOriginal = LongStream.of(original).sum();
        long sumSorted = LongStream.of(sorted).sum();

        assertEquals(original.length, sorted.length);
        assertEquals(sumOriginal, sumSorted);

        for (int i = 1; i < sorted.length; i++) {
            assertTrue(sorted[i-1] <= sorted[i]);
        }
    }

    long[] randomLongArray(long size) {
        return LongStream.generate(() -> random.nextLong()).limit(size).toArray();
    }

    long[] copy(long[] array) {
        return Arrays.copyOf(array, array.length);
    }

    int bitAt(byte[] source, int bitPos) {
        int bytePos = bitPos >> 3;//bitPos / 8
        int bitBytePos = bitPos & 0b111;

        return (source[bytePos] >> bitBytePos) & 1;
    }


    @Test
    void bitAt() {

        byte[] b = new byte[]{1,0,0,-128};


        for (int i = 0; i < b.length * 8; i++) {
            System.out.println(i + "\t" + bitAt(b, i));

        }

    }
}
