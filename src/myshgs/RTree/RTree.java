package myshgs.RTree;


import java.util.ArrayList;
import java.util.List;

public class RTree {
    public RTNode root;
    private final int treeType;
    private int C = 0;
    private int F = 0;
    private final double fillFactor;
    private final int dims;


    public RTree(int C, int F, int dim) {
        this.C = C;
        this.F = F;
        this.fillFactor = 0.6;
        this.treeType = Constants.RTREE_QUADRATIC;
        this.dims = dim;
        root = new RTDataNode(this, Constants.NULL);
    }

    public double getFillFactor() {
        return fillFactor;
    }

    public int getTreeType() {
        return treeType;
    }

    public int getDims() {
        return dims;
    }

    public void setRoot(RTNode root) {
        this.root = root;
    }

    public int getCap() {
        return C;
    }
    public int getFanout() {
        return F;
    }


    public boolean insert(MBR rec) {
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rec.getMin().length != getDims()) {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }
        RTDataNode leaf = root.chooseLeaf(rec);
        boolean res = leaf.insert(rec);

        return res;
    }

    public void STRLoad(long[][] points) {
        STRLoad strLoad = new STRLoad();
        this.root = strLoad.Load(this, points, this.C, this.F);
    }

    public List<RTNode> traversePost(RTNode root) { // Acquire all nodes from this tree
        if (root == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }
        List<RTNode> list = new ArrayList<>();
        list.add(root);
        if (!root.isLeaf()) {
            for (int i = 0; i < root.usedSpace; i++) {
                list.addAll(traversePost(((RTDirNode) root).getChild(i)));
            }
        }
        return list;
    }

    public static void main(String[] args) {
        int d = 3;
        int C = 5;
        int F = 8;
        long[][] points = {{5, 5}, {5, 0}, {2, 3}, {0, 4}, {0, 9}, {2, 6}, {7, 8}, {4, 5}, {1, 9}, {9, 5}, {0, 9}, {8, 7}, {0, 7}, {0, 2}, {1, 4}, {4, 5}, {5, 0}, {4, 9}, {2, 1}, {2, 8}};
        RTree tree = new RTree(C, F, d);
        tree.STRLoad(points);
    }
}