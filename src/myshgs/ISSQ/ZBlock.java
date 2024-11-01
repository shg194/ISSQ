package myshgs.ISSQ;

import java.util.ArrayList;

public abstract class ZBlock {
    public ZIMBRA zmbr;
    private ZDirBlock parent = null;
    private ZBlock next;
    private int from;
    private boolean isDominate = false;
    public ArrayList<Integer> dG;
    public int[] skyline;
    public int pos;

    public ZBlock(int d,int pos) {
        zmbr = new ZIMBRA(d);
        this.from = -1;
        this.skyline = new int[2];
        this.dG = new ArrayList<>();
        this.pos = pos;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }


    public boolean isDominate() {
        return isDominate;
    }

    public void setDominate(boolean dominate) {
        isDominate = dominate;
    }

    public ZDirBlock getParent() {
        return parent;
    }

    public void setParent(ZDirBlock parent) {
        this.parent = parent;
    }

    public ZBlock getNext() {
        return this.next;
    }

    public void setNext(ZBlock next) {
        this.next = next;
    }

    public void addDG(int pos){
        this.dG.add(pos) ;
    }
}
