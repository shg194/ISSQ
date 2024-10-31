// The rectangle that cover several points
// In this program, data point is denoted by rectangle with the same low and high coordinate

package myshgs.MBRSKY;

import java.util.Arrays;

public class MBR implements Comparable<MBR> {
    private final long[] min;
    private final long[] max;
    public long[][] datas;
    public int space;
    public int usedSpace;
    public int d;
    public long minValue;
    public long maxValue;
    public boolean isDominate;

    public MBR(int d, int C) { // Create rectangle that covers an area
        this.min = new long[d];
        this.max = new long[d];
        this.d = d;
        this.space = C;
        this.isDominate = false;
        Arrays.fill(min, Integer.MAX_VALUE);
        Arrays.fill(max, Integer.MIN_VALUE);
        this.datas = new long[C + 1][d];
        this.usedSpace = 0;
    }

    public long[] getMin() {
        return min.clone();
    }


    public long[] getMax() {
        return max.clone();
    }

    public void setDominate(boolean dominate) {
        isDominate = dominate;
    }

    protected void addData(long[] rec) {
        datas[usedSpace++] = rec;

        for (int j = 0; j < d; j++) {
            min[j] = Math.min(rec[j], min[j]);
            max[j] = Math.max(rec[j], max[j]);
        }
        this.minValue = Arrays.stream(min).sum();
        this.maxValue = Arrays.stream(max).sum();
    }

    protected void delete(int i) {
        long[] tmp = datas[i];
        datas[i] = datas[--usedSpace];
        datas[usedSpace] = tmp;
    }

    @Override
    public String toString() {
        if (!Arrays.equals(min, max)) {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < usedSpace; i++) {
                str.append(Arrays.toString(datas[i]));
            }

            return " {MBR min: " + Arrays.toString(min) + ", max: " + Arrays.toString(max) + ", points = {" + str + "} }";
        } // For rectangle
        return " { points: " + Arrays.toString(min) + ", " + usedSpace + " }"; // For single point
    }

    public long getDistance() { // Calculate the square of mindist of the point (distance to point o)
        long res = 0;
        for (long datum : min) {
            res += datum;
        }
        return res;
    }

    public long getDSMax() { // Calculate the square of mindist of the point (distance to point o)
        long res = 0;
        for (long datum : max) {
            res += datum;
        }
        return res;
    }

    @Override
    public int compareTo(MBR o) {
        int compare = Long.compare(getDistance(), o.getDistance());
        if (compare == 0) {
            for (int i = 0; i < d; i++) {
                compare = Long.compare(min[i], o.getMin()[i]);
                if (compare != 0)
                    return compare;
            }
        }
        return compare;
    }
}