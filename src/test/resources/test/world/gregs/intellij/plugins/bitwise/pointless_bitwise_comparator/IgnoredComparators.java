class IgnoredComparators {

    private int first = 1;
    private int second = 2;

    public void bitwiseNegative() {
        boolean result = -(first ^ 0xffffffff) == -2;
    }

    public void bitwiseOr() {
        boolean result = (first | 1 ^ 0xffffffff) == -1;
    }

    public void bitwiseOrReversed() {
        boolean result = (1 | first ^ 0xffffffff) == -1;
    }

}