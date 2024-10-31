package myshgs.MBRSKY;


import java.util.Comparator;

public class CenterComp implements Comparator<long[]> {
    int dim = -1;

    void setDim(int dim) {
        this.dim = dim;
    }
    @Override
    public int compare(long[] o1, long[] o2) {
        long c1 = o1[dim] ; // *0.5
        long c2 = o2[dim] ; // *0.5
        return Long.compare(c1, c2);
    }
}
