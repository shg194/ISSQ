package myshgs.ZSearch;

import myshgs.Utils;

import java.util.*;

public class ZBTree {
    public ZBNode root;
    private int C = -1;
    private int F = -1;
    private int d = 0;
    private ZBNode skyline;

    public ZBTree(int C, int F, int d) {
        this.C = C;
        this.F = F;
        this.d = d;
        this.root = new ZBDataNode(null, d, C);
        this.skyline = new ZBDataNode(null, d, C);
    }

    public void init(long[][] points) {
        RZLoad load = new RZLoad(d, C, F);
        this.root = load.Loading(points);
    }

    private void insert(long[] pointers, long[] count) {
        ZBNode current = this.skyline;
        count[1]++;
        while (current instanceof ZBDirNode) {
            current = ((ZBDirNode) current).getChildren(current.getUsedSpace() - 1);
            count[1]++;
        }
        ZBDataNode currentRegion = (ZBDataNode) current;
        if (currentRegion.getUsedSpace() == 0) {
            currentRegion.addData(pointers);
            return;
        }
        //判断当前是否需要拆分
        if (currentRegion.getUsedSpace() >= C) {
            BitSet area = currentRegion.getMinzt();
            int position = currentRegion.getUsedSpace() - 1;
            int M = (int) (0.4 * C);
            for (int i = M; i < currentRegion.getUsedSpace() - 1; i++) {
                BitSet area1 = currentRegion.getDatas(i).getMaxzt();
                if (area.length() < area1.length()) {
                    position = i;
                    break;
                }
            }

            ZBDirNode parent = (ZBDirNode) currentRegion.getParent();
            count[1]++;

            ZBDataNode rz = new ZBDataNode(parent, d, C);

            rz.addAndDelete(currentRegion, position + 1);
            rz.addData(pointers);

            if (parent == null) {
                parent = new ZBDirNode(null, d, F);

                parent.addChildren(currentRegion, currentRegion.getCurRzRegion());
                parent.addChildren(rz, rz.getCurRzRegion());

                this.skyline = parent;
            } else {
                parent.setDatas(parent.getUsedSpace() - 1, currentRegion.getCurRzRegion());

                parent.addChildren(rz, rz.getCurRzRegion());

                ZBDirNode curNode = parent;
                //更新区间
                while (curNode.getUsedSpace() > F) {
                    area = curNode.getMinzt();
                    position = curNode.getUsedSpace() - 2;
                    M = (int) (0.4 * F);
                    for (int i = M; i < curNode.getUsedSpace() - 2; i++) {
                        BitSet area1 = curNode.getDatas(i).getMaxzt();
                        if (area.length() < area1.length()) {
                            position = i;
                            break;
                        }
                    }

                    count[1]++;
                    ZBDirNode region = (ZBDirNode) curNode.getParent();

                    ZBDirNode rzx = new ZBDirNode(region, d, F);

                    rzx.addAndDelete(curNode, position + 1);

                    if (region == null) {

                        region = new ZBDirNode(null, d, F);

                        region.addChildren(curNode, curNode.getCurRzRegion());
                        region.addChildren(rzx, rzx.getCurRzRegion());

                        this.skyline = region;

                        break;
                    } else {
                        region.setDatas(region.getUsedSpace() - 1, curNode.getCurRzRegion());
                        region.addChildren(rzx, rzx.getCurRzRegion());
                        curNode = region;
                    }
                }
                while (curNode != null) {
                    int len = curNode.getUsedSpace() - 1;
                    curNode.setDatas(len, curNode.getChildren(len).getCurRzRegion());
                    curNode = (ZBDirNode) curNode.getParent();
                    count[1]++;
                }
            }
        } else {
            currentRegion.addData(pointers);

            ZBDirNode curNode = (ZBDirNode) currentRegion.getParent();
            count[1]++;
            while (curNode != null) {
                curNode.setDatas(curNode.getUsedSpace() - 1, curNode.getChildren(curNode.getUsedSpace() - 1).getCurRzRegion());
                curNode = (ZBDirNode) curNode.getParent();
                count[1]++;
            }
        }
    }

    public List<long[]> skyline(long[] count) {
        //0 支配次数 1 访问节点次数
        ZBNode SRC = this.root;
        count[1]++;
        List<long[]> skyline = new ArrayList<>();
        if (SRC instanceof ZBDataNode) { // Only one node in the tree
            for (int i = 0; i < SRC.getUsedSpace(); i++) {
                long[] p = ((ZBDataNode) SRC).getData(i);
                if (!Dominate(p, p, count)) {
                    insert(p, count);
                    skyline.add(p);
                }
            }
            return skyline;
        }
        Stack<RZRegion> deque = new Stack<>();
        Map<RZRegion, ZBNode> record = new HashMap<>();

        ZBDirNode r = (ZBDirNode) SRC;
        for (int i = r.getUsedSpace() - 1; i >= 0; i--) { // Include all data from root in the heap
            count[1]++;
            deque.add(r.getDatas(i));
            record.put(r.getDatas(i), r.getChildren(i));
        }

        while (!deque.isEmpty()) {
            RZRegion pop = deque.pop();
            count[1]++;
            if (!Dominate(pop.getMinpt(), pop.getMaxpt(), count)) {
                ZBNode cur = record.get(pop);
                record.remove(pop);
                if (cur instanceof ZBDirNode) {
                    for (int i = cur.getUsedSpace() - 1; i >= 0; i--) {
                        count[1]++;
                        deque.add(cur.getDatas(i));
                        record.put(cur.getDatas(i), ((ZBDirNode) cur).getChildren(i));
                    }
                } else {
                    for (int i = 0; i < cur.getUsedSpace(); i++) {
                        long[] p = ((ZBDataNode) cur).getData(i);
                        if (!Dominate(p, p, count)) {
                            insert(p, count);
                            skyline.add(p);
                        }
                    }
                }
            }
        }
        return skyline;
    }

    public void cache() {
        this.skyline = new ZBDataNode(null, d, C);
        this.root = null;
    }

    public void levelOrderTraversal(ZBNode skyline) {
        if (root == null) {
            return;
        }

        Queue<ZBNode> queue = new LinkedList<>();
        queue.add(skyline);

        while (!queue.isEmpty()) {
            ZBNode currentNode = queue.poll();

            if (currentNode instanceof ZBDirNode zbDirNode) {
                System.out.println("cur : " + zbDirNode);
                for (int i = 0; i < currentNode.getUsedSpace(); i++) {
                    System.out.print(zbDirNode.getDatas(i) + " ");
                }
                System.out.println();
            } else if (currentNode instanceof ZBDataNode zbDataNode) {

                for (int i = 0; i < currentNode.getUsedSpace(); i++) {
                    System.out.print(Arrays.toString(zbDataNode.getData(i)) + " ");
                }
                System.out.print(currentNode);
                System.out.println();
            }

            if (currentNode instanceof ZBDirNode) {
                for (int i = 0; i < currentNode.getUsedSpace(); i++) {
                    queue.add(((ZBDirNode) currentNode).getChildren(i));
                }
            }
        }
        System.out.println();
    }

    private Boolean Dominate(long[] minpt, long[] maxpt, long[] count) {
        if (this.skyline instanceof ZBDataNode) { // Only one node in the tree
            count[1]++;
            for (int i = 0; i < this.skyline.getUsedSpace(); i++) {
                long[] p = ((ZBDataNode) this.skyline).getData(i);
                if (Utils.isDominatedBy(p, minpt, count))
                    return true;
            }
            return false;
        }

        Queue<RZRegion> queue = new ArrayDeque<>();
        Map<RZRegion, ZBNode> record = new HashMap<>();

        ZBDirNode r = (ZBDirNode) this.skyline;
        count[1]++;
        for (int i = 0; i < r.getUsedSpace(); i++) { // Include all data from root in the heap
            count[1]++;
            queue.add(r.getDatas(i));
            record.put(r.getDatas(i), r.getChildren(i));
        }
        while (!queue.isEmpty()) {
            RZRegion region = queue.poll();
            ZBNode poll = record.get(region);
            count[1]++;
            record.remove(region);
            if (poll instanceof ZBDirNode rz) {
                for (int i = 0; i < poll.getUsedSpace(); i++) {
                    count[1]++;
                    if (Utils.isDominatedBy(rz.getDatas(i).getMaxpt(), minpt, count)) {
                        return true;
                    } else if (Utils.isDominatedBy(rz.getDatas(i).getMinpt(), maxpt, count)) {
                        queue.add(rz.getDatas(i));
                        record.put(rz.getDatas(i), rz.getChildren(i));
                    }
                }
            } else {
                for (int i = 0; i < poll.getUsedSpace(); i++) {
                    if (Utils.isDominatedBy(((ZBDataNode) poll).getData(i), minpt, count))
                        return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        for (int iii = 0; iii < 1000000; iii++) {
//            for (int i = 0; i <= 10; i++) {
            int d = 2;
            int N = 4;
            int M = 2;
            int num = 10;
            int maxNum = 10;
            long[][] points = Utils.generateAntiCorrelatedData(d, 10000, 100000, 0.1);
            ZBTree zbTree = new ZBTree(N, M, d);
            zbTree.init(points);
            System.out.println("init!");

            long l = System.currentTimeMillis();
            List<long[]> skyline = zbTree.skyline(new long[]{0, 0});
            long l1 = System.currentTimeMillis();
            System.out.println("size :" + skyline.size());
            System.out.println("time :" + (l1 - l));

            System.out.println();
            for (long[] p : skyline)
                System.out.print(Arrays.toString(p));

            System.out.println();
            List<long[]> skyline1 = Utils.findSkyline(points);
            for (long[] p : skyline1)
                System.out.print(Arrays.toString(p));

            System.out.println();
            if (skyline1.size() != skyline.size()) {

                System.out.print("{");
                for (long[] p : points) {
                    System.out.print("{");
                    for (int ii = 0; ii < p.length; ii++) {
                        if (ii != p.length - 1)
                            System.out.print(p[ii] + ",");
                        else
                            System.out.print(p[ii]);
                    }
                    System.out.print("},");
                }
                System.out.println("}");
                break;
            }
        }
    }

}
