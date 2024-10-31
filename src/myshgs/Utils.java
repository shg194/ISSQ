package myshgs;

import myshgs.MBRSKY.MBRSky;

import java.io.*;
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

    public static int getC(int num, int d, int F) {
        return (int) Math.ceil(num / Math.pow(Math.ceil(Math.pow((double) num / F, (1d / d))), d));
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

    public static void writePointsToFile(String fileName, long[][] points) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

        for (long[] point : points) {
            for (int i = 0; i < point.length; i++) {
                writer.write(Long.toString(point[i]));
                if (i < point.length - 1) {
                    writer.write(","); // separate values with commas
                }
            }
            writer.newLine(); // new line for the next point
        }

        writer.close();
    }

    public static long[][] readPointsFromFile(String fileName, int d) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<long[]> pointsList = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            long[] point = new long[d];
            for (int i = 0; i < d; i++) {
                point[i] = Long.parseLong(values[i]);
            }
            pointsList.add(point);
        }

        reader.close();
        return pointsList.toArray(new long[0][d]);
    }

    public static void testAlgorithmAndLog(long[][] points, int d, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath,true))) {
            writer.write("d: " + d + "\n mbr : LeafCapacity,ExecutionTime(ms)\n"); // 写入表头

            for (int leafCapacity = 50; leafCapacity < 120; leafCapacity += 5) {
                System.out.println(leafCapacity);
                MBRSky algorithm = new MBRSky(leafCapacity, points[0].length);
                algorithm.init(points);
                long startTime = System.nanoTime();
                algorithm.skyline(new long[]{0, 0});
                long endTime = System.nanoTime();
                long executionTime = (endTime - startTime) / 1_000_000; // 转换为毫秒
                // 写入每个组合的执行时间
                writer.write(leafCapacity + "," + executionTime + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("erro");
        }
    }
}
