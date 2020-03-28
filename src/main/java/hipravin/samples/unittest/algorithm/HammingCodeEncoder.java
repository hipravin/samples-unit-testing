package hipravin.samples.unittest.algorithm;

public class HammingCodeEncoder implements ExcessEncoder {
    @Override
    public byte[] encode(byte[] source) {
        int sourceBitCount = 8 * source.length;
        //may have couple more bytes than needed but this is not important
        byte[] encoded = new byte[source.length + extraBits(sourceBitCount) / 8 + 1];

        copyAddingControlBits0(sourceBitCount, source, encoded);

        for (int p = 1; p < encoded.length * 8; p *= 2) {
            setBit(encoded, p - 1, controlBit(encoded, p));
        }

        return encoded;
    }

    @Override
    public byte[] decode(byte[] encoded, int sourceSizeBytes) {
        int incorrectBitPos = 0;

        for (int p = 1; p < encoded.length * 8; p *= 2) {
            int cb = controlBit(encoded, p);
            if (cb != bitAt(encoded, p - 1)) {
                incorrectBitPos += p;
            }
        }
        if (incorrectBitPos > 0) {
            setBit(encoded, incorrectBitPos - 1, bitAt(encoded, incorrectBitPos - 1) ^ 1);
        }

        byte[] decoded = new byte[sourceSizeBytes];

        copyRemovingControlBits(sourceSizeBytes, encoded, decoded);
        return decoded;
    }

    private static void copyAddingControlBits0(int sourceBitCount, byte[] source, byte[] encoded) {
        int pow = 1;
        int sourceBitPos = 0;

        while (sourceBitPos < sourceBitCount) {
            for (int targetBitPos = pow; (targetBitPos != 2 * pow - 1) && (sourceBitPos < sourceBitCount); targetBitPos++) {
                setBit(encoded, targetBitPos, bitAt(source, sourceBitPos));
                sourceBitPos++;
            }
            pow *= 2;
        }
    }

    private static void copyRemovingControlBits(int sourceSizeBytes, byte[] encoded, byte[] decoded) {
        int pow = 1;
        int decodedBitPos = 0;
        int sourceBitCount = sourceSizeBytes * 8;

        while (decodedBitPos < sourceBitCount) {
            for (int encodedBitPos = pow; encodedBitPos != 2 * pow - 1 && decodedBitPos < sourceBitCount; encodedBitPos++) {
                setBit(decoded, decodedBitPos, bitAt(encoded, encodedBitPos));
                decodedBitPos++;
            }
            pow *= 2;
        }
    }

    static int controlBit(byte[] encoded, int pow) {
        int res = 0;
        for (int i = pow + 1; i < encoded.length * 8; i++) {
            if ((i & pow) > 0) {//bit is controlled
                res ^= bitAt(encoded, i - 1);
            }
        }
        return res;
    }

    static int extraBits(int bits) {
        return 32 - Integer.numberOfLeadingZeros(bits) + 1;// to avoid using log function
    }

    static int bitAt(byte[] source, int bitPos) {
        int bytePos = bitPos >> 3;//bitPos / 8
        int bitBytePos = 7 - bitPos & 0b111;

        return (source[bytePos] >> bitBytePos) & 1;
    }

    static void setBit(byte[] source, int bitPos, int bit) {
        int bytePos = bitPos >> 3;//bitPos / 8
        int bitBytePos = 7 - bitPos & 0b111;

        if (bit == 1) {
            source[bytePos] |= (1 << bitBytePos);
        } else if (bit == 0) {
            source[bytePos] &= (0xFF ^ (1 << bitBytePos));
        }
    }
}
