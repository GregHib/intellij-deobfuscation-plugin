class InverseElseComparator {

    private int first = 1;
    private int second = 2;

    public void rangeInversion() {
        <warning descr="'first < 30 || first >= 35' can be inverted to 'first >= 30 && first < 35'">if (first < 30 || first >= 35) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void rangeIgnored() {
        if (first >= 35 && first < 40) {
            second = 1;
        } else {
            second = 2;
        }
    }

}