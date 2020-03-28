package hipravin.samples.unittest.algorithm;

public interface ExcessEncoder {
    byte[] encode(byte[] source);
    byte[] decode(byte[] encoded, int sourceSizeBytes);
}
