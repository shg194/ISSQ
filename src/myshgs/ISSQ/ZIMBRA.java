package myshgs.ISSQ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZIMBRA {
    public long[] min;
    public List<Integer> bit;

    public ZIMBRA(int d) {
        this.bit = new ArrayList<>();
        this.min = new long[d];
        Arrays.fill(this.min, Integer.MAX_VALUE);
    }
}
