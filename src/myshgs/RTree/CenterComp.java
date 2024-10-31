package myshgs.RTree;

import java.util.Comparator;

public class CenterComp implements Comparator<Object> {
    int dim = -1;

    void setDim(int dim) {
        this.dim = dim;
    }

    @Override
    public int compare(Object a, Object b) {
        MBR o1,o2;
        if(a instanceof MBR){
            o1 = (MBR) a;
            o2 = (MBR) b;
        }else {
            o1 = ((RTNode) a).getNodeRectangle();
            o2 = ((RTNode) b).getNodeRectangle();
        }
        long c1 = o1.getMax()[dim] + o1.getMin()[dim]; // *0.5
        long c2 = o2.getMax()[dim] + o2.getMin()[dim]; // *0.5
        return Long.compare(c1, c2);
    }
}
