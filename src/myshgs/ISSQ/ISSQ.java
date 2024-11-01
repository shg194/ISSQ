package myshgs.ISSQ;


import myshgs.Utils;

import java.util.*;

public class ISSQ {
    public ZBlock root;
    private int C;
    private int d;
    private int G;
    private int Q;
    private final int len;

    public ISSQ(int d, int C, int G, int Q) {
        this.d = d;
        this.C = C;
        this.Q = Q;
        this.G = G;
        this.len = (1 << d) - 1;
    }

    protected ZBlock loadingToB(long[][] points) {
        List<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }
        data.sort(new Comparator<BitSet>() {
            @Override
            public int compare(BitSet o1, BitSet o2) {
                return -Utils.compare(o1, o2);
            }
        });
        int len = data.get(0).length();
        int t = (len / d) * d;
        len = (len % d == 0) ? t - d : t;

        this.root = new ZNode(d, -1);
        this.root.setFrom(len);
        for (BitSet a : data) {
            ((ZNode) this.root).addBitset(a);
        }
        Stack<ZNode> stack = new Stack<>();
        stack.add(((ZNode) this.root));

        while (!stack.isEmpty()) {
            ZNode poll = stack.pop();

            ArrayList<BitSet> set = poll.getBitSet();
            int from = poll.getFrom();

            if (poll.getBitSet().size() > C && from >= 0) {
                int size = set.size() - 1;
                if (set.get(0).get(from, from + d).equals(set.get(size).get(from, from + d))) {
                    poll.setFrom(from - d);
                    stack.add(poll);
                } else {
                    List<ZNode> tmp = new ArrayList<>();
                    BitSet cur = set.get(0).get(from, from + d);
                    int pos = cur.isEmpty() ? 0 : (int) cur.toLongArray()[0];
                    ZNode zcur = new ZNode(d, pos);
                    zcur.setFrom(from - d);
                    tmp.add(zcur);
//                    stack.add(zcur);

                    ZDirBlock k = new ZDirBlock(d, poll.pos);
                    k.setFrom(from);

                    for (int j = 0; j <= size; j++) {
                        BitSet bitSet = set.get(j);
                        if (Utils.compare(cur, bitSet.get(from, from + d)) == 0) {
                            zcur.addBitset(bitSet);
                        } else {
//                            k.setChild(pos, zcur);
                            cur = bitSet.get(from, from + d);
                            pos = cur.isEmpty() ? 0 : (int) cur.toLongArray()[0];
                            zcur = new ZNode(d, pos);
                            zcur.setFrom(from - d);
                            tmp.add(zcur);
//                            stack.add(zcur);
                            zcur.addBitset(bitSet);
                        }
                    }
//                    k.setChild(pos, zcur);

                    for (int i = 0; i < tmp.size(); i++) {
                        ZNode zNode = tmp.get(i);
                        int sum = zNode.tmp.size();
                        for (i = i + 1; sum < G && i < tmp.size(); i++) {
                            ZNode node = tmp.get(i);
                            sum += node.tmp.size();
                            if (sum > G) {
                                break;
                            }
                            zNode.pos = node.pos;
                            zNode.tmp.addAll(node.tmp);
                            zNode.merge.add(node.pos);
                        }
                        k.setChild(zNode.pos, zNode);
                        stack.add(zNode);
                        i--;
                    }
                    tmp.clear();
                    if (poll.getParent() == null) {
                        this.root = k;
                    } else
                        poll.getParent().alterChild(poll.pos, k);
                }
            } else {
                poll.clear();
                ZDirBlock cur = poll.getParent();
                while (cur != null) {
                    cur.alterMin(poll.zmbr.min);
                    cur = cur.getParent();
                }
            }
        }
        return this.root;
    }

    public void trace() {
        Queue<ZBlock> queue = new ArrayDeque<>();
        queue.add(this.root);
        while (!queue.isEmpty()) {
            ZBlock poll = queue.poll();

            System.out.println(poll);

            if (poll instanceof ZDirBlock zds) {
                List<Integer> bit = poll.zmbr.bit;
                for (int i = bit.size() - 1; i >= 0; i--) {
                    ZBlock zbk = zds.child[bit.get(i)];
                    queue.add(zbk);
                }
            }

        }
    }

    public void init(long[][] points) {
        this.root = loadingToB(points);
    }

    public boolean SDominate(int pre, int last, List<long[]> skyline, long[] p, long[] count) {
        for (int i = pre; i < last; i++) {
            if (Utils.isDominatedBy(skyline.get(i), p, count))
                return true;
        }
        return false;
    }

    public boolean isDominate(ZBlock zBlock, long[] p, boolean minpt, List<long[]> skyline, long[] count) {
        ZBlock cur = zBlock;
        Stack<ZIMBRA> stack = new Stack<>();
        HashMap<ZIMBRA, ZBlock> record = new HashMap<>();
        if (!minpt) {
            stack.add(zBlock.zmbr);
            record.put(zBlock.zmbr, zBlock);
        }
        while (cur != null) {
            ZDirBlock parent = cur.getParent();
            count[1]++;
            if (parent != null && parent.skyline[1] - parent.skyline[0] > 0) {
                for (int pos : cur.dG) {
                    ZBlock block = parent.child[pos];
                    count[1]++;
                    if (block != null && !block.isDominate()) {
                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        if (last - pre > 0 && Utils.isDominatedBy(block.zmbr.min, p, count)) {
                            if (last - pre <= Q) {
                                if (SDominate(pre, last, skyline, p, count)) {
                                    if (minpt) zBlock.setDominate(true);
                                    return false;
                                }
                            } else {
                                stack.add(block.zmbr);
                                record.put(block.zmbr, block);
                            }
                        }
                    }
                }
            }
            cur = cur.getNext();
            count[1]++;
        }
        while (!stack.isEmpty()) {
            ZIMBRA zm = stack.pop();
            ZBlock poll = record.get(zm);
            count[1]++;

            record.remove(zm);
            if (poll instanceof ZDirBlock zds) {
                for (int i : zm.bit) {
                    ZBlock zbk = zds.child[i];
                    count[1]++;
                    if (!zbk.isDominate() && Utils.isDominatedBy(zbk.zmbr.min, p, count)) {
                        int pre = zbk.skyline[0];
                        int last = zbk.skyline[1];
                        if (last - pre <= Q) {
                            if (SDominate(pre, last, skyline, p, count)) {
                                if (minpt) zBlock.setDominate(true);
                                return false;
                            }
                        } else {
                            stack.add(zbk.zmbr);
                            record.put(zbk.zmbr, zbk);
                        }
                    }

                }
            } else {
                ZNode node = (ZNode) poll;
                int pre = node.skyline[0];
                int last = node.skyline[1];
                if (SDominate(pre, last, skyline, p, count)) {
                    if (minpt) zBlock.setDominate(true);
                    return false;
                }
            }
        }
        return true;
    }

    public List<long[]> skyline(long[] count) {
        List<long[]> skyline = new ArrayList<>();
        Stack<ZIMBRA> stack = new Stack<>();
        HashMap<ZIMBRA, ZBlock> record = new HashMap<>();

        count[1]++;
        stack.add(this.root.zmbr);
        record.put(this.root.zmbr, this.root);

        while (!stack.isEmpty()) {
            ZIMBRA zm = stack.pop();
            count[1]++;
            ZBlock pop = record.get(zm);
            record.remove(zm);

            int[] its = pop.skyline;
            its[0] = its[1] = skyline.size();

            if (isDominate(pop, zm.min, true, skyline, count)) {
                if (pop instanceof ZDirBlock zdb) {
                    int size = zm.bit.size();
                    int pi = zm.bit.get(0);
                    if (pi == this.len && zm.bit.get(size - 1) == 0) {
                        zdb.child[pi].setDominate(true);
                        count[1]++;
                        pi = 1;
                    } else {
                        pi = 0;
                    }
                    for (int i = pi; i < size; i++) {
                        count[1]++;
                        ZBlock zbk = zdb.child[zm.bit.get(i)];
                        stack.add(zbk.zmbr);
                        record.put(zbk.zmbr, zbk);
                    }
                } else {
                    ZNode node = (ZNode) pop;
                    ArrayList<long[]> data = node.getData();
                    boolean flag = false;

                    for (long[] p : data) {
                        if (isDominate(pop, p, false, skyline, count)) {
                            skyline.add(p);
                            its[1]++;
                            flag = true;
                        }
                    }

                    if (!flag) {
                        node.setDominate(true);
                    } else {
                        ZBlock cur = node.getParent();
                        count[1]++;
                        while (cur != null) {
                            cur.skyline[1] = its[1];
                            cur = cur.getParent();
                            count[1]++;
                        }
                    }
                }
            }
        }
        return skyline;
    }

    public void cache() {
        Queue<ZBlock> queue1 = new ArrayDeque<>();
        queue1.add(this.root);
        while (!queue1.isEmpty()) {
            ZBlock poll = queue1.poll();
            Arrays.fill(poll.skyline,0);
            poll.setDominate(false);

            if (poll instanceof ZDirBlock zds) {
                for (Integer index : poll.zmbr.bit) {
                    ZBlock zbk = zds.child[index];
                    queue1.add(zbk);
                }
            }
        }
        this.root = null;

    }

    private static BitSet createBitSet(int value) {
        BitSet bitSet = new BitSet();
        int index = 0;
        while (value != 0) {
            if (value % 2 == 1) {
                bitSet.set(index);
            }
            index++;
            value /= 2;
        }
        return bitSet;
    }

    public static void main(String[] args) {
        for (int ii = 0; ii < 10000; ii++) {
            int d = 5;
            int T = 3;
            ISSQ zas = new ISSQ(d, T, 2, 2);
            long[][] points = Utils.generateIndependentData(d, 300, 300);
//            long[][] points = {{4, 5}, {2, 6}, {3, 4}, {9, 6}, {4, 9}, {0, 5}, {6, 1}, {2, 9}, {1, 8}, {4, 9}};


            zas.init(points);
//            zas.trace();
//
//            System.out.println("----");

            List<long[]> list = Utils.findSkyline(points);

            for (long[] p : list) {
                System.out.print(Arrays.toString(p));
            }
            System.out.println();

            long l = System.nanoTime();
            List<long[]> skyline = zas.skyline(new long[]{0, 0});
            long l1 = System.nanoTime();

            for (long[] p : skyline) {
                System.out.print(Arrays.toString(p));
            }
            System.out.println();

            System.out.println("time: " + (l1 - l) / 1000000.0);
            System.out.println("size: " + skyline.size());
            System.out.println("-----");
            System.out.println();

            if (list.size() != skyline.size()) {
                System.out.print("{");
                for (long[] p : points) {
                    System.out.print("{");
                    for (int i = 0; i < p.length; i++) {
                        if (i != p.length - 1)
                            System.out.print(p[i] + ",");
                        else
                            System.out.print(p[i]);
                    }
                    System.out.print("},");
                }
                System.out.println("}");
                break;
            }

        }
    }
}
