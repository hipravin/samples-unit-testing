package hipravin.samples.unittest.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Random;

import static hipravin.samples.unittest.algorithm.HammingCodeEncoder.bitAt;
import static hipravin.samples.unittest.algorithm.HammingCodeEncoder.setBit;
import static org.junit.jupiter.api.Assertions.*;

class HammingCodeEncoderTest {

    Random random;
    ExcessEncoder encoder = new HammingCodeEncoder();

    @BeforeEach
    void setUp() {
        random = new Random(0);
    }

    @RepeatedTest(100)
    void encodeDecodeNoBrokenBits() {
        int length = random.nextInt(10_000) + 1;
        byte[] original = randomByteArray(length);
        byte[] thereAndBackAgain = encoder.decode(encoder.encode(original), original.length);

        assertArrayEquals(original, thereAndBackAgain);
    }

    @RepeatedTest(100)
    void encodeDecodeRandomBrokenBits() {
        int length = random.nextInt(10_000) + 1;
        byte[] original = randomByteArray(length);
        byte[] encoded = encoder.encode(original);

        invertRandomBit(encoded);
        byte[] thereAndBackAgain = encoder.decode(encoded, original.length);

        assertArrayEquals(original, thereAndBackAgain);
    }

    @RepeatedTest(100)
    void encodeDecodeRandom2BrokenBits() {
        int length = random.nextInt(10_000) + 1;
        byte[] original = randomByteArray(length);
        byte[] encoded = encoder.encode(original);

        setBit(encoded, 0, bitAt(encoded, 0) ^ 1);
        setBit(encoded, 1, bitAt(encoded, 1) ^ 1);

        byte[] thereAndBackAgain = encoder.decode(encoded, original.length);
        assertThrows(AssertionFailedError.class, () -> {
            assertArrayEquals(original, thereAndBackAgain);
        });
    }

    void invertRandomBit(byte[] bytes) {
        int bytePos = random.nextInt(bytes.length);
        int bitPos = random.nextInt(bytes.length);

        bytes[bytePos] = invertBit(bytes[bytePos], bitPos);
    }

    byte invertBit(byte b, int bitPos) {

        return (byte) (b ^ (1 << bitPos));
    }

    byte[] randomByteArray(int size) {
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);

        return bytes;
    }

    @Test
    @Disabled
    void testExtraBits() {
        for (int i = 1; i < 128; i++) {
            System.out.println(i + "\t" + HammingCodeEncoder.extraBits(i));
        }
    }

    @Test
    void testGetSetBit() {
        byte[] original = randomByteArray(100);

        for (int bitPos = 0; bitPos < 800; bitPos++) {
            setBit(original, bitPos, 1);
            assertEquals(1, bitAt(original, bitPos));

            setBit(original, bitPos, 0);
            assertEquals(0, bitAt(original, bitPos));
        }
    }

    @Test
    @Disabled
    void testEncodeSampleHabr() {
        byte[] source = new byte[]{0b0100_0100, 0b0011_1101};

        printBits(source);
        byte[] encoded = encoder.encode(source);

        printBits(encoded);
        printBits(encoder.decode(encoded, source.length));
    }

    @Test
    void testHighestOneBit() {
        for (int i = 0; i < 1024; i++) {
            System.out.println(i + "\t" + (32 - Integer.numberOfLeadingZeros(i)));

        }
    }

    @Test
    @Disabled
    void testInvert() {
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            byte b = (byte) i;

            System.out.println(Integer.toBinaryString(b));
            System.out.println(Integer.toBinaryString(invertBit(b, 3)));

            System.out.println();
        }
    }

    void printBits(byte[] bytes) {
        for (int i = 0; i < bytes.length * 8; i++) {
            System.out.print(bitAt(bytes, i));
        }
        System.out.println();
    }
}