package myshgs.ISSQ;

import myshgs.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

public class ZNode extends ZBlock {
    ArrayList<long[]> data;
    ArrayList<BitSet> tmp;
    int d;
    ArrayList<Integer> merge;

    public ZNode(int d, int pos) {
        super(d,pos);
        this.d = d;
        this.merge = new ArrayList<>();
        merge.add(pos);
        this.tmp = new ArrayList<>();
        this.data = new ArrayList<>();
        zmbr = new ZIMBRA(d);
    }

    public ArrayList<long[]> getData() {
        return this.data;
    }

    public ArrayList<BitSet> getBitSet() {
        return this.tmp;
    }

    public void clear() {
        long[] min = this.zmbr.min;
        for (int i = this.tmp.size() - 1; i >= 0; i--) {
            BitSet p = this.tmp.get(i);
            long[] ztoP = Utils.fromZtoP(p, d);
            for (int j = 0; j < d; j++) {
                min[j] = Math.min(min[j], ztoP[j]);
            }
            data.add(ztoP);
        }
        this.tmp = null;
    }

    public void addBitset(BitSet a) {
        this.tmp.add(a);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (long[] p : data)
            sb.append(Arrays.toString(p));
        return "ZNode{" +
                ", pos =" + this.pos +
//                ", flag =" + isDominate() +
//                ", min =" + Arrays.toString(zmbr.min) +
//                ", max =" + Arrays.toString(zmbr.max) +
                ", points=" + data.size() +
                ", DG :" + dG +
//                ", size = " + (skyline[1] - skyline[0]) +
                '}';
    }
}
