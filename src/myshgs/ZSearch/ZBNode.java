package myshgs.ZSearch;

import java.util.BitSet;

public abstract class ZBNode {
    private RZRegion[] datas;
    protected ZBNode parent;
    private int usedSpace;
    private int d;

    public ZBNode(ZBNode parent, int d, int N) {
        this.datas = new RZRegion[N + 2];
        this.parent = parent;
        this.usedSpace = 0;
        this.d = d;
    }

    public RZRegion getDatas(int index) {
        return datas[index];
    }
    public RZRegion[] getRZRegion(){
        return this.datas;
    }

    public void setDatas(int index, RZRegion region) {
        this.datas[index] = region;
    }

    public ZBNode getParent() {
        return parent;
    }

    public RZRegion getCurRzRegion() {
        return new RZRegion(d, getMinzt(), getMaxzt());
    }

    public void setParent(ZBNode parent) {
        this.parent = parent;
    }

    public int getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    public BitSet getMinzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(0);
            return rz.getMinzt();
        }
        return new BitSet();
    }

    public BitSet getMaxzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(usedSpace - 1);
            return rz.getMaxzt();
        }
        return new BitSet();
    }

    public void delete(int i) {
        if (datas[i + 1] != null) {
            System.arraycopy(datas, i + 1, datas, i, usedSpace - i - 1);
            datas[usedSpace - 1] = null;
        } else {
            datas[i] = null;
        }
        usedSpace--;
    }

    @Override
    public String toString() {
        return "ZBNode{" +
                "datas=" + getCurRzRegion() +
//                ", parent=" + parent +
                ", usedSpace=" + usedSpace +
                '}';
    }
}