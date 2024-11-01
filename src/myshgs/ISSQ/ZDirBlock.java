package myshgs.ISSQ;

import java.util.Arrays;

public class ZDirBlock extends ZBlock {
    ZBlock[] child;

    public ZDirBlock(int d, int pos) {
        super(d,pos);
        zmbr = new ZIMBRA(d);
        child = new ZBlock[1 << d];
    }

    public void setChild(int index, ZNode zBlock) {
        for (int pos : zmbr.bit) {
            ZBlock block = child[pos];
            if(block instanceof ZNode b){
                boolean flag = false;
                for (int q:b.merge){
                    for (int p:zBlock.merge){
                        if((p&q)==p){
                            block.addDG(index);
                            flag = true;
                            break;
                        }
                    }
                    if(flag)
                        break;
                }
            }else {
                for (int p:zBlock.merge){
                    if((p&pos)==p){
                        block.addDG(index);
                        break;
                    }
                }
            }
        }
        this.zmbr.bit.add(index);
        zBlock.setParent(this);

        child[index] = zBlock;
    }

    public void alterChild(int index, ZDirBlock zBlock) {
        ZBlock block = child[index];
        zBlock.pos = index;
        zBlock.setNext(block.getNext());
        zBlock.setParent(this);
        zBlock.dG = block.dG;


        child[index] = zBlock;

        if (child[index].dG.isEmpty()) {
            for (Integer c : zBlock.zmbr.bit) {
                zBlock.child[c].setNext(child[index].getNext());
            }
        } else {
            for (Integer c : zBlock.zmbr.bit) {
                zBlock.child[c].setNext(zBlock);
            }
        }
    }

    public void alterMin(long[] p) {
        long[] min = zmbr.min;
        for (int i = 0; i < min.length; i++) {
            min[i] = Math.min(p[i], min[i]);
        }
    }

    @Override
    public String toString() {
        return "ZBlock{" +
                ", pos =" + pos +
                ", min =" + Arrays.toString(zmbr.min) +
                ", DG :" + dG +
                ", arr :" + zmbr.bit +
                ", size = " + (skyline[1] - skyline[0]) +
                '}';
    }


}
