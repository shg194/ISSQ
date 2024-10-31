package myshgs.RTree;

import java.util.Arrays;

public class STRLoad {
    public RTNode Load(RTree rtree, long[][] points, int C,int F) {
        int depth = 0;
        int N = points.length;
        int d = points[0].length;
        CenterComp cmp = new CenterComp();

        MBR[] list = new MBR[N];
        for (int i = 0; i < N; i++) {
            list[i] = new MBR(points[i]);
        }

        sortChunks(list, d, C, cmp);

        RTNode[] nodes = new RTNode[(int) Math.ceil(N / (double) C)];
        int posNode = 0;
        RTNode node = null;
        for (int i = 0; i < list.length; i++) {
            if (i % C == 0) {
                node = new RTDataNode(rtree, Constants.NULL);
                nodes[posNode++] = node;
            }
            node.addData(list[i]);
        }

        if (nodes.length == 1) {
            return nodes[0];
        }

        RTDirNode[] parentNodes = null;
        do {
            depth++;
            parentNodes = new RTDirNode[(int) Math.ceil(nodes.length / (double) F)];

            sortChunks(nodes, d, F, cmp);
            RTDirNode p = null;
            int posParent = 0;
            for (int i = 0; i < nodes.length; i++) {
                if (i % F == 0) {
                    p = new RTDirNode(rtree, Constants.NULL, depth);
                    parentNodes[posParent++] = p;
                }
                p.add(nodes[i]);
            }
            nodes = parentNodes;
        } while (parentNodes.length > 1);
        return parentNodes[0];
    }
    private void sortChunks(Object[] entries, int dims, int F, CenterComp comp) {
        comp.setDim(0);
        Arrays.sort(entries, comp);
        int nToSplit = entries.length;
        for (int d = 1; d < dims; d++) {
            int nodesPerAxis = (int) Math.pow((double) nToSplit / F, 1.0 / (double) (dims - d + 1));
            comp.setDim(d);
            int chunkSize = (int) Math.ceil(Math.pow(nodesPerAxis, dims - d) * F);
            if (chunkSize < F) {
                break;
            }
            int pos = 0;
            while (pos < entries.length) {
                int end = Math.min(pos + chunkSize, entries.length);
                Arrays.sort(entries, pos, end, comp);
                pos += chunkSize;
            }
            nToSplit /= nodesPerAxis;
        }
    }
}
