package myshgs.ZSearch;

import myshgs.Utils;

import java.util.BitSet;

public class RZRegion {
    private int d;
    private long[] minpt;
    private long[] maxpt;

    private BitSet minzt;
    private BitSet maxzt;

    public RZRegion(int d, BitSet minzt, BitSet maxzt) {
        this.d = d;
        this.minzt = (BitSet) minzt.clone();
        this.maxzt = (BitSet) maxzt.clone();
        BitSet[] rzRegion = utils.getRZRegion(minzt, maxzt, d);
        minpt = Utils.fromZtoP(rzRegion[0], d);
        maxpt = Utils.fromZtoP(rzRegion[1], d);
    }

    public RZRegion(int d, long[] pt) {
        this.d = d;
        BitSet zt = Utils.fromPtoZ(pt);
        this.minzt = (BitSet) zt.clone();
        this.maxzt = (BitSet) zt.clone();
        minpt = pt.clone();
        maxpt = pt.clone();
    }


    public int getD() {
        return d;
    }

    public long[] getMinpt() {
        return minpt;
    }

    public long[] getMaxpt() {
        return maxpt;
    }

    public BitSet getMinzt() {
        return minzt;
    }

    public BitSet getMaxzt() {
        return maxzt;
    }

    protected String bitsetToBinaryString(BitSet bitSet) {
        int size = bitSet.length();
        StringBuilder binaryStr = new StringBuilder();
        for (int i = 0; i < size; i++) {
            binaryStr.append(bitSet.get(i) ? '1' : '0');
        }
        return binaryStr.reverse().toString();
    }


    @Override
    public String toString() {
        return "RZRegion{" +
                "minzt=" + bitsetToBinaryString(minzt) +
//                ",minpt=" + Arrays.toString(minpt) +
                ", maxzt=" + bitsetToBinaryString(maxzt) +
//                ", maxpt=" + Arrays.toString(maxpt) +
                '}';
    }

    public static void main(String[] args) {
        System.out.println(-1 / 2);
    }
}
