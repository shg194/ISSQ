package myshgs.MBRSKY;


import myshgs.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MBRSky {
    private final int d;
    private final int C;
    public long[][] points;
    private MBR[] MBRs;
    private HashMap<MBR, ArrayList<MBR>> dgMap;

    public MBRSky(int C, int d) {
        this.d = d;
        this.C = C;
    }

    public void init(long[][] points) {
        this.points = points;
        this.MBRs = getMBR(points, C,this.d);
    }

    private boolean isDominated(ArrayList<MBR> list, long[] p, long[] count) {
        for (MBR mbr : list) {
            if (!mbr.isDominate) {
                if (mbr.minValue >= Arrays.stream(p).sum())
                    break;
                count[1]++;
                for (int i = 0; i < mbr.usedSpace; i++) {
                    int dtDev = Utils.DtDev(mbr.datas[i], p, count);
                    if (dtDev == -1) {
                        return true;
                    } else if (dtDev == 1) {
                        mbr.delete(i--);
                    }
                }
            }
        }

        return false;
    }

    private void sortChunks(long[][] entries, int dims, int M, CenterComp comp) {
        comp.setDim(0);
        Arrays.sort(entries, comp);
        int nToSplit = entries.length;
        for (int d = 1; d < dims; d++) {
            int nodesPerAxis = (int) Math.pow((double) nToSplit / M, 1.0 / (double) (dims - d + 1));
            comp.setDim(d);
            int chunkSize = (int) Math.ceil(Math.pow(nodesPerAxis, dims - d) * M);
            if (chunkSize < M) {
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

    private MBR[] getMBR(long[][] points, int C, int d) {
        int N = points.length;
        CenterComp cmp = new CenterComp();

        sortChunks(points, d, C, cmp);

        MBR[] mbrs = new MBR[(int) Math.ceil(N / (double) C)];

        int posNode = 0;
        MBR node = null;
        for (int i = 0; i < points.length; i++) {
            if (i % C == 0) {
                node = new MBR(d, C);
                mbrs[posNode++] = node;
            }
            node.addData(points[i]);
        }
        Arrays.sort(mbrs);
        return mbrs;
    }

    private HashMap<MBR, ArrayList<MBR>> MBRQuery(MBR[] M, int d, long[] count) {
        HashMap<MBR, ArrayList<MBR>> dependentQuery = new HashMap<>();
        for (MBR mbr : M) {
            count[1]++;
            if (mbr.isDominate)
                continue;
            ArrayList<MBR> dependent = new ArrayList<>();
            for (MBR value : M) {
                count[1]++;
                if (value.isDominate || value == mbr)
                    continue;
                if (mbr.isDominate && mbr.maxValue < value.minValue) {
                    break;
                }
                if (utils.DTDominated(value, mbr, d, count)) {
                    mbr.setDominate(true);
                    break;
                }
                if (utils.DTDominated(mbr, value, d, count)) {
                    value.setDominate(true);
                    continue;
                }
                if (Utils.isDominatedBy(value.getMin(), mbr.getMax(), count)) {
                    dependent.add(value);
                }
            }
            dependentQuery.put(mbr, dependent);
        }
        return dependentQuery;
    }

    public List<long[]> skyline(long[] count) {
        List<long[]> skyline = new ArrayList<>();
        this.dgMap = MBRQuery(MBRs, d, count);

        for (MBR mbr : this.MBRs) {
            count[1]++;
            for (int i = 0; i < mbr.usedSpace; i++) {
                long[] data = mbr.datas[i];
                for (int j = i + 1; j < mbr.usedSpace; j++) {
                    int dtDev = Utils.DtDev(data, mbr.datas[j], count);
                    if (dtDev == 1) {
                        mbr.delete(i);
                        i--;
                        break;
                    } else if (dtDev == -1) {
                        mbr.delete(j--);
                    }
                }
            }

            if (!mbr.isDominate) {
                for (int i = 0; i < mbr.usedSpace; i++) {
                    long[] p = mbr.datas[i];
                    if (isDominated(dgMap.get(mbr), p, count)) {
                        mbr.delete(i--);
                    } else {
                        skyline.add(p);
                    }
                }
            }
        }
        return skyline;
    }

    public void cache() {
        this.MBRs = null;
        this.dgMap = null;
        this.points = null;
    }

    public static void main(String[] args) {
        long[][] data = {{6, 19}, {14, 10}, {7, 20}, {6, 18}, {10, 10}, {20, 3}, {7, 2}, {20, 1}, {11, 5}, {1, 20}, {0, 11}, {11, 16}, {9, 15}, {14, 14}, {18, 11}, {19, 2}, {4, 18}, {6, 20}, {8, 6}, {17, 3}};

        int fanOut = 4;

        MBRSky loader = new MBRSky(fanOut, 2);
        loader.init(data);

        System.out.println(Arrays.toString(loader.MBRs));
        List<long[]> skyline = loader.skyline(new long[]{0, 0});
    }
}
