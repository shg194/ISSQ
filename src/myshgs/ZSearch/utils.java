package myshgs.ZSearch;

import java.util.BitSet;

public class utils {
    public static BitSet[] getRZRegion(BitSet bitSet1, BitSet bitSet2, int d) {
        BitSet[] bitSet = new BitSet[2];
        bitSet[0] = new BitSet();
        bitSet[1] = (BitSet) bitSet2.clone();

        int index1 = bitSet1.length();
        int index2 = bitSet2.length();
        if (index1 == index2) {
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k - d : k;
            while (k >= 0 && bitSet1.get(k, k + d).equals(bitSet2.get(k, k + d))) {
                k -= d;
            }
            k += d;
            bitSet[1].set(0, k, false);
            bitSet[0] = (BitSet) bitSet[1].clone();
            bitSet[1].set(0, k, true);
        }else {
            index1 = Math.max(Math.max(index1, index2) - 1, 0);
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k : k + d;
            bitSet[1].set(0, k, true);
        }
        return bitSet;
    }

    public static BitSet getArea(BitSet a, BitSet b, int d) {
        BitSet target = new BitSet();

        int index1 = a.length();
        int index2 = b.length();
        if (index1 == index2) {
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k - d : k;
            while (k >= 0 && a.get(k, k + d).equals(b.get(k, k + d))) {
                k -= d;
            }
            k += d;
            target.set(0, k, true);
        }else {
            index1 = Math.max(Math.max(index1, index2) - 1, 0);
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k : k + d;
            target.set(0, k, true);
        }

        return target;
    }
}
