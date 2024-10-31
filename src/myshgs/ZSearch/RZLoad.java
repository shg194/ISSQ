package myshgs.ZSearch;

import myshgs.Utils;

import java.util.*;

public class RZLoad {
    private final int d;
    private final int C;
    private final int F;

    public RZLoad(int d, int C, int F) {
        this.d = d;
        this.C = C;
        this.F = F;
    }

    private ZBNode createNode(List<BitSet> window, Deque<BitSet> deque) {
        // 创建叶节点
        int M = (int)(0.4*C);
        ZBDataNode leafNode = new ZBDataNode(null, d, C);
        BitSet bitSet = window.get(0);
        BitSet cur = utils.getArea(bitSet, window.get(window.size() - 1),d);
        int position = window.size()-1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(bitSet, window.get(position),d);
            if (Utils.compare(area,cur) > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            position = window.size() - 1;
        }
        for (int i = 0; i <= position; i++) {
            leafNode.addData(Utils.fromZtoP(window.get(i), d));
        }
        for (int i = window.size() - 1; i >= position + 1; i--) {
            deque.addFirst(window.get(i));
        }
        return leafNode;
    }

    private ZBNode merge(List<ZBNode> window, Deque<ZBNode> deque) {
        int M = (int)(F*0.4);
        // 创建节点
        ZBDirNode Node = new ZBDirNode(null, d, F);
        BitSet minzt = window.get(0).getMinzt();
        BitSet cur = utils.getArea(minzt, window.get(window.size() - 1).getMaxzt(),d);

        int position = window.size() - 1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(minzt, window.get(position).getMaxzt(),d);
            if (Utils.compare(cur,area) > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            position = window.size() - 1;
        }
        for (int i = 0; i <= position; i++) {
            Node.addChildren(window.get(i), window.get(i).getCurRzRegion());
        }
        for (int i = window.size() - 1; i >= position + 1; i--) {
            deque.addFirst(window.get(i));
        }
        return Node;
    }

    public ZBNode Loading(long[][] points) {
        List<ZBNode> target = new ArrayList<>();
        List<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }
        data.sort(Utils::compare);


        Deque<BitSet> deque = new ArrayDeque<>(data);

        while (!deque.isEmpty()) {
            List<BitSet> window = new ArrayList<>();
            int len = Math.min(deque.size(), C);
            for (int i = 0; i < len; i++) {
                window.add(deque.pop());
            }

            ZBNode node = createNode(window, deque);
            target.add(node);
        }

        do {
            Deque<ZBNode> deq = new ArrayDeque<>(target);
            target.clear();
            while (!deq.isEmpty()) {
                List<ZBNode> window = new ArrayList<>();
                int len = Math.min(deq.size(), F);
                for (int i = 0; i < len; i++) {
                    window.add(deq.pop());
                }
                ZBNode merge = merge(window, deq);
                target.add(merge);
            }
        } while (target.size() > 1);
        return target.get(0);
    }
}
