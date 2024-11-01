package myshgs.MBRSKY;


public class utils {
    public static boolean DTDominated(MBR a, MBR b, int d,long[] count) {
        count[0]++;
        boolean flag = false, isDominate = false, equ = true, equ1 = true;
        for (int i = 0; i < d; i++) {
            if (a.getMax()[i] > b.getMin()[i]) {
                equ = false;
                if (flag)
                    return false;
                else {
                    flag = true;
                    if (a.getMin()[i] < b.getMin()[i]) {
                        isDominate = true;
                    } else if (a.getMin()[i] > b.getMin()[i])
                        return false;
                }
            } else if (a.getMax()[i] < b.getMin()[i]) {
                equ = false;
                isDominate = true;
            }
            if (a.getMin()[i] != a.getMax()[i])
                equ1 = false;
        }
        if (equ && !equ1)
            return true;
        return isDominate;
    }

}
