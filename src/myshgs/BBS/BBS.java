package myshgs.BBS;

import myshgs.RTree.*;
import myshgs.Utils;

import java.util.*;

public class BBS {
    private final int C;
    private final int F;
    private final int d;
    private RTNode root;

    public BBS(int C, int F, int dim) {
        this.C = C;
        this.F = F;
        this.d = dim;
    }

    public void init(long[][] points) {
        RTree tree = new RTree(C, F, d);
        tree.STRLoad(points);
        root = tree.root;
    }

    public List<long[]> skyline(long[] count) {
        List<long[]> res = new ArrayList<>();
        PriorityQueue<MBR> deque = new PriorityQueue<>();
        HashMap<MBR, RTNode> record = new HashMap<>();

        if (root instanceof RTDataNode) {
            deque.addAll(Arrays.asList(root.datas).subList(0, root.getUsedSpace()));
            count[1]++;
            while (!deque.isEmpty()) {
                MBR poll = deque.poll();
                if (isDominate(res, poll, count)) {
                    res.add(poll.getMax());
                }
            }
            return res;
        }
        RTDirNode r = (RTDirNode) root;
        count[1]++;
        for (int i = 0; i < r.getUsedSpace(); i++) {
            count[1]++;
            deque.add(r.datas[i]);
            record.put(r.datas[i], r.getChild(i));
        }

        while (!deque.isEmpty()) {
            MBR rec = deque.poll();
            count[1]++;
            if (isDominate(res, rec, count)) {
                if (record.get(rec) != null) {
                    RTNode r1 = record.get(rec);
                    for (int i = 0; i < r1.getUsedSpace(); i++) {
                        count[1]++;
                        if (isDominate(res, r1.datas[i], count)) {
                            deque.add(r1.datas[i]);
                            if (r1 instanceof RTDirNode r2) {
                                record.put(r2.datas[i], r2.getChild(i));
                            } else {
                                record.put(r1.datas[i], null);
                            }
                        }
                    }
                } else {
                    res.add(rec.getMax());
                }
            }
        }
        return res;
    }

    public void cache() {
        this.root = null;
    }


    public boolean isDominate(List<long[]> list, MBR rec, long[] count) {
        for (long[] rectangle : list) {
            if (Utils.isDominatedBy(rectangle, rec.getMin(), count))
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        for (int ii = 0; ii < 100; ii++) {
            int[] count = new int[1];
            int d = 3;
            long[][] points = Utils.generateIndependentData(d, 1000000, 1000000);
            //long[][] points = {{1, 9, 4}, {7, 3, 9}, {7, 1, 8}, {0, 9, 7}, {7, 2, 4}, {1, 8, 6}, {6, 7, 0}, {3, 9, 1}, {7, 3, 8}, {7, 1, 9}};
            BBS bbs = new BBS(100, 1000, d);
            bbs.init(points);
            System.out.println("init complete!");

            long l = System.currentTimeMillis();
            List<long[]> skyline = bbs.skyline(new long[]{0, 0});
            long l1 = System.currentTimeMillis();
            System.out.println("size: " + skyline.size());
            System.out.println("time: " + (l1 - l));

        }
    }

}