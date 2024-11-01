package myshgs;

import java.util.*;

public class Utils {
    public static long[][] generateAntiCorrelatedData(int numberOfDimensions, int numberOfData, long maxDataValue, double spread) {
        long[][] data = new long[numberOfData][numberOfDimensions];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < numberOfData; i++) {
            data[i][0] = random.nextLong(maxDataValue);
            long relatedValue = maxDataValue - data[i][0];
            for (int j = 1; j < numberOfDimensions; j++) {
                data[i][j] = (long) (relatedValue + (relatedValue * (random.nextDouble() - 0.5) * spread));
            }
        }
        return data;
    }

    public static long[][] generateIndependentData(int numberOfDimensions, int numberOfData, int maxDataValue) {
        long[][] data = new long[numberOfData][numberOfDimensions];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < numberOfData; i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                data[i][j] = random.nextLong(maxDataValue);
            }
        }
        return data;
    }

    public static boolean isDominatedBy(long[] a, long[] b, long[] count) {
        count[0]++;
        boolean isDominated = false;
        for (int i = 0; i < a.length; i++) {
            if (b[i] > a[i]) {
                isDominated = true;
            } else if (b[i] < a[i]) {
                return false;
            }
        }
        return isDominated;
    }

    public static long[] fromZtoP(BitSet z, int d) {
        //100
        long[] point = new long[d];
        int bit = z.length();
        for (int i = 0; i < bit; i++)
            if (z.get(i)) {
                point[d - i % d - 1] |= 1L << (i / d);
            }
        return point;
    }
    public static BitSet fromPtoZ(long[] point) {
        int d = point.length;
        int maxLength = 0;
        for (long k : point) {
            maxLength = Math.max(maxLength, Long.SIZE - Long.numberOfLeadingZeros(k));
        }
        BitSet result = new BitSet();
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < d; j++) {
                if ((point[j] & (1L << i)) != 0)
                    result.set(d * i + d - j - 1);
            }
        }
        return result;
    }

    public static int DtDev(long[] p1, long[] p2, long[] count) {
        count[0]++;
        boolean t1_better = false, t2_better = false;
        for (int d = 0; d < p1.length; d++) {
            t1_better = p1[d] < p2[d] || t1_better;
            t2_better = p1[d] > p2[d] || t2_better;
            if (t1_better && t2_better) {
                return 0;
            }
        }
        if (!t1_better && t2_better) {
            return 1;
        }
        if (t1_better) {
            return -1;
        }
        return 0;
    }

    public static int compare(BitSet bitSet1, BitSet bitSet2) {
        int length1 = bitSet1.length();
        int length2 = bitSet2.length();
        if (length1 > length2)
            return 1;
        else if (length1 < length2)
            return -1;
        else {
            for (int i = length1 - 1; i >= 0; i--) {
                boolean bit1 = bitSet1.get(i);
                boolean bit2 = bitSet2.get(i);
                if (bit1 != bit2) {
                    return bit1 ? 1 : -1;
                }
            }
            return 0;
        }
    }

    public static List<long[]> findSkyline(long[][] points) {
        List<long[]> skyline = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            long[] current = points[i];
            boolean isSkyline = true;
            for (int j = 0; j < points.length; j++) {
                if (i != j) {
                    long[] other = points[j];
                    if (isDominatedBy(other, current, new long[]{0, 0})) {
                        isSkyline = false;
                        break;
                    }
                }
            }
            if (isSkyline) {
                skyline.add(current);
            }
        }
        return skyline;
    }
}
