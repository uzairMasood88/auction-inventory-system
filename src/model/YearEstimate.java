package model;

public class YearEstimate {
    private int low;
    private int high;
    private int mid;

    public YearEstimate(int low, int high) {
        this.low = low;
        this.high = high;
        this.mid = (low + high) / 2; // Integer division for whole number
    }

    // Getters
    public int getLow() { return low; }
    public int getHigh() { return high; }
    public int getMid() { return mid; }

    @Override
    public String toString() {
        return low + "-" + high + " (" + mid + ")";
    }
}