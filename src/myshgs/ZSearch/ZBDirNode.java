package myshgs.ZSearch;


public class ZBDirNode extends ZBNode {
    protected ZBNode[] children;

    public ZBDirNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.children = new ZBNode[N+1];
    }

    public ZBNode[] getChildren() {
        return children;
    }

    public ZBNode getChildren(int index) {
        return children[index];
    }

    public void addChildren(ZBNode rtNode, RZRegion region) {
        int space = this.getUsedSpace();
        this.setDatas(space, region);
        this.children[space] = rtNode;
        this.setUsedSpace(++space);
        rtNode.setParent(this);
    }
    public void deletes(int i) {
        System.arraycopy(children, i + 1, children, i, this.getUsedSpace() - i - 1);
        delete(i);
    }
    public void addAndDelete(ZBNode cur,int position){
        System.arraycopy(((ZBDirNode)cur).getChildren(),position,this.children,0,cur.getUsedSpace()-position);
        System.arraycopy(cur.getRZRegion(),position,this.getRZRegion(),0,cur.getUsedSpace()-position);
        this.setUsedSpace(cur.getUsedSpace()-position);
        cur.setUsedSpace(position);
        for (int i = 0; i < this.getUsedSpace(); i++) {
            children[i].setParent(this);
        }

    }
}
