// Class for the node of R Tree. Two subclasses for data node and index node

package myshgs.RTree;


import java.util.Arrays;
import java.util.List;

public abstract class RTNode {
    protected RTree rtree;
    public int level;
    public MBR[] datas;
    protected RTNode parent;
    protected int usedSpace; // How many data are in this node currently
    protected int insertIndex; // The next index to be inserted a new element
    protected int deleteIndex; // The next index to be deleted

    public RTNode(RTree rtree, RTNode parent, int level) {
        this.rtree = rtree;
        this.parent = parent;
        this.level = level;
        usedSpace = 0;
    }

    public RTNode getParent() {
        return parent;
    }

    public int getUsedSpace() {
        return usedSpace;
    }

    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    protected void addData(MBR rec) {
        if (usedSpace == rtree.getCap()) {
            throw new IllegalArgumentException("Node is full.");
        }
        datas[usedSpace++] = rec;
    }

    public void deleteData(int i) {
        if (datas[i + 1] != null) {
            System.arraycopy(datas, i + 1, datas, i, usedSpace - i - 1);
            datas[usedSpace - 1] = null;
        } else {
            datas[i] = null;
        }
        usedSpace--;
    }


    // Condense the tree after a deletion if there's too little datas in it
    // Relocate data to other nodes if this happens
    // Might be done recursively till root if needed
    protected void condenseTree(List<RTNode> list) {
        if (isRoot()) { // only one child for root, set it to new root
            if (!isLeaf() && usedSpace == 1) {
                RTDirNode root = (RTDirNode) this;
                RTNode child = root.getChild(0);
                root.children.remove(this);
                child.parent = null;
                rtree.setRoot(child);
            }
        } else {
            RTNode parent = getParent();
            int min = (int) Math.round(rtree.getCap() * rtree.getFillFactor()); // If the data capacity has reached its minimum
            if (usedSpace < min) {
                parent.deleteData(parent.deleteIndex);
                ((RTDirNode) parent).children.remove(this);
                this.parent = null;
                list.add(this);
            } else {
                parent.datas[parent.deleteIndex] = getNodeRectangle();
            }
            parent.condenseTree(list);
        }
    }

    // Split the node into two depends on the area, stop when one group reaches the minimum datas
    // Divide data to two groups by making their corresponding rectangles have larger difference of area
    protected int[][] quadraticSplit(MBR rec) {
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        datas[usedSpace] = rec;
        int total = usedSpace + 1;
        int[] mask = new int[total];
        Arrays.fill(mask, 1);

        int c = total / 2 + 1;
        int minSize = (int) Math.round(rtree.getCap() * rtree.getFillFactor()); // Minimum data
        if (minSize < 2) {
            minSize = 2;
        }
        int rem = total;
        int[] group1 = new int[c];
        int[] group2 = new int[c];
        int i1 = 0, i2 = 0;
        int[] seed = pickSeeds();
        group1[i1++] = seed[0];
        group2[i2++] = seed[1];
        rem -= 2;
        mask[group1[0]] = -1;
        mask[group2[0]] = -1;

        while (rem > 0) {
            if (minSize - i1 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        group1[i1++] = i;
                        mask[i] = -1;
                        rem--;
                    }
                }
            } else if (minSize - i2 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        group2[i2++] = i;
                        mask[i] = -1;
                        rem--;
                    }
                }
            } else {
                MBR r1 = datas[group1[0]].clone();
                for (int i = 1; i < i1; i++) {
                    r1 = r1.getUnion(datas[group1[i]]);
                }
                MBR r2 = datas[group1[0]].clone();
                for (int i = 1; i < i2; i++) {
                    r2 = r2.getUnion(datas[group2[i]]);
                }

                // Get next splitting index
                double dif = Double.NEGATIVE_INFINITY;
                double areaDiff1 = 0, areaDiff2 = 0;
                int sel = -1;
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        MBR a = r1.getUnion(datas[i]);
                        areaDiff1 = a.getArea() - r1.getArea();
                        MBR b = r2.getUnion(datas[i]);
                        areaDiff2 = b.getArea() - r2.getArea();
                        if (Math.abs(areaDiff1 - areaDiff2) > dif) {
                            dif = Math.abs(areaDiff1 - areaDiff2);
                            sel = i;
                        }
                    }
                }

                if (areaDiff1 < areaDiff2) {
                    group1[i1++] = sel;
                } // Firstly, area difference
                else if (areaDiff1 > areaDiff2) {
                    group2[i2++] = sel;
                } else if (r1.getArea() < r2.getArea()) {
                    group1[i1++] = sel;
                } // Secondly, area
                else if (r1.getArea() > r2.getArea()) {
                    group2[i2++] = sel;
                } else if (i1 < i2) {
                    group1[i1++] = sel;
                } // Lastly, amount of data
                else if (i1 > i2) {
                    group2[i2++] = sel;
                } else {
                    group1[i1++] = sel;
                }

                mask[sel] = -1;
                rem--;
            }
        }

        int[][] res = new int[2][];
        res[0] = new int[i1];
        res[1] = new int[i2];
        System.arraycopy(group1, 0, res[0], 0, i1);
        System.arraycopy(group2, 0, res[1], 0, i2);
        return res;
    }

    // Calculate the area of U - R1 - R2, where U is the union rectangle of R1 and R2
    // Pick the 2 rectangles with the largest area of U - R1 - R2 as seeds
    protected int[] pickSeeds() {
        double inefficiency = Double.NEGATIVE_INFINITY;
        int i1 = 0, i2 = 0;
        for (int i = 0; i < usedSpace; i++) {
            for (int j = i + 1; j <= usedSpace; j++) {
                MBR rec = datas[i].getUnion(datas[j]);
                double d = rec.getArea() - datas[i].getArea() - datas[j].getArea();
                if (d > inefficiency) {
                    inefficiency = d;
                    i1 = i;
                    i2 = j;
                }
            }
        }
        return new int[]{i1, i2};
    }

    public MBR getNodeRectangle() { // Get the minimum rectangle that covers all of its data
        if (usedSpace > 0) {
            MBR[] rec = new MBR[usedSpace];
            System.arraycopy(datas, 0, rec, 0, usedSpace);
            return MBR.getUnion(rec);
        }
        return new MBR(new long[]{0, 0}, new long[]{0, 0});
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");
        res.append(this.getNodeRectangle());
//        for (MBR data : datas) {
//            res.append(data).append(", ");
//        }
        res.append("}");
        return res.toString();
    }

    public boolean isRoot() {
        return parent == Constants.NULL;
    }

    public boolean isIndex() {
        return level != 0;
    }

    public boolean isLeaf() {
        return level == 0;
    }

    protected abstract RTDataNode chooseLeaf(MBR rec);

    protected abstract RTDataNode findLeaf(MBR rec);

    protected abstract List<MBR> searchLeaf(MBR rec);
}