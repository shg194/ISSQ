package myshgs.ZSearch;

public class ZBDataNode extends ZBNode {
    private long[][] data;
    private int N;


    public ZBDataNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.N = N;
        this.data = new long[N + 1][d];
    }

    public long[][] getData() {
        return data;
    }

    public long[] getData(int index) {
        return data[index];
    }

    public void addData(long[] data) {
        int space = this.getUsedSpace();
        this.data[space] = data;
        this.setDatas(space++, new RZRegion(data.length, data));
        this.setUsedSpace(space);
    }

    public void deletes(int i) {
        System.arraycopy(data, i + 1, data, i, this.getUsedSpace() - i - 1);
        delete(i);
    }

    public void addAndDelete(ZBNode cur,int position){
        System.arraycopy(((ZBDataNode)cur).getData(),position,this.data,0,cur.getUsedSpace()-position);
        System.arraycopy(cur.getRZRegion(),position,this.getRZRegion(),0,cur.getUsedSpace()-position);
        this.setUsedSpace(cur.getUsedSpace()-position);
        cur.setUsedSpace(position);
    }
}
