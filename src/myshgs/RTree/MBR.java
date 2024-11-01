package myshgs.RTree;

import java.util.Arrays;

public class MBR implements Cloneable, Comparable<MBR> {
	private final long[] low;
	private final long[] high;
	public MBR(long[] p1, long[] p2) { // Create rectangle that covers an area
		low = p1.clone();
		high = p2.clone();
	}

	public MBR(long[] p) { // Create rectangle for a single point
		low = high = p;
	}

    public long[] getMin() { return low.clone(); }

	public long[] getMax() { return high.clone(); }


	public MBR getUnion(MBR rec) { // Get the minimum rectangle that contains both of the 2 targeted rectangles
		if (rec == null) { throw new IllegalArgumentException("Rectangle cannot be null."); }
		if (rec.getDimension() != getDimension()) { throw new IllegalArgumentException("Rectangle must be of same dimension."); }
		long[] min = new long[getDimension()];
		long[] max = new long[getDimension()];
		for (int i = 0; i < getDimension(); i ++) {
			min[i] = Math.min(low[i], rec.low[i]);
			max[i] = Math.max(high[i], rec.high[i]);
		}
		return new MBR(min, max);
	}

	public double getArea() {
		double area = 1;
		for (int i = 0; i < getDimension(); i ++) {
			area *= high[i] - low[i];
		}
		return area;
	}

	public static MBR getUnion(MBR[] rec) { // Get the minimum rectangle that contains all the targeted rectangles
		if (rec == null || rec.length == 0) { throw new IllegalArgumentException("Rectangle array is empty."); }
		MBR res = (MBR) rec[0].clone();
		for (int i = 1; i < rec.length; i ++) {
			res = res.getUnion(rec[i]);
		}
		return res;
	}


	@Override
	public String toString() {
		if (!Arrays.equals(low, high)) { return "MBR min: " + Arrays.toString(low) + ", max: " + Arrays.toString(high); } // For rectangle
		return Arrays.toString(low); // For single point
	}

	public double intersectArea(MBR rec) { // Calculate the area of intersection with another rectangle
		if (isIntersect(rec)) { return 0; }
		double area = 1;
		for (int i = 0; i < rec.getDimension(); i ++) { // Multiply the intersected edges of each dimension
			double l1 = this.low[i];
			double h1 = this.high[i];
			double l2 = rec.low[i];
			double h2 = rec.high[i];

			if (l1 <= l2 && h1 <= h2) { area *= (h1 - l1) - (l2 - l1); } // Left
			else if (l1 >= l2 && h1 >= h2) { area *= (h2 - l2) - (l1 - l2); } // Right
			else if (l1 >= l2) { area *= h1 - l1; } // within
			else { area *= h2 - l2; } // enclosure
		}
		return area;
	}

	public boolean isIntersect(MBR rec) { // Judge if it's intersect with the targeted rectangle
		if (rec == null) { throw new IllegalArgumentException("Rectangle cannot be null."); }
		if (rec.getDimension() != getDimension()) { throw new IllegalArgumentException("Rectangle must be of same dimension."); }
		for (int i = 0; i < getDimension(); i ++) {
			if (low[i] > rec.high[i] || high[i] < rec.low[i]) { return false; }
		}
		return true;
	}

	public long getDistance(long[] data) { // Calculate the square of mindist of the point (distance to point o)
		long res = 0;
		for (long datum : data) {
			res += datum;
		}
		return res;
	}

	private int getDimension() { return low.length; }

	public boolean enclosure(MBR rec) { // Judge if the targeted rectangle is inside it
		if (rec == null) { throw new IllegalArgumentException("Rectangle cannot be null."); }
		if (rec.getDimension() != getDimension()) { throw new IllegalArgumentException("Rectangle must be of same dimension."); }
		for (int i = 0; i < getDimension(); i ++) {
			if (rec.low[i] < low[i] || rec.high[i] > high[i]) { return false; }
		}
		return true;
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (obj instanceof MBR rec) {
//            return Arrays.equals(low, rec.getMin()) && Arrays.equals(high, rec.getMax());
//		}
//		return false;
//	}

	@Override
	public int compareTo(MBR arg0) { // Compare 2 rectangles by their mindists
		if (arg0 != null) {
//            if (getDistance(getMin()) > getDistance(arg0.getMin())) { return 1; }
//			else if (getDistance(getMin()) < getDistance(arg0.getMin())) { return -1; }
//			else if (getDistance(getMax()) > getDistance(arg0.getMax())) { return 1; }
//			else if (getDistance(getMax()) < getDistance(arg0.getMax())) { return -1; }
			return Long.compare(getDistance(getMin()),getDistance(arg0.getMin()));
		}
		return 0;
	}

    @Override
    public MBR clone(){
        return new MBR(this.low, this.high);
    }

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(low);
		result = 31 * result + Arrays.hashCode(high);
		return result;
	}
}