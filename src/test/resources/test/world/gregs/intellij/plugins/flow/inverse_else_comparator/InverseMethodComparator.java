class InverseElseComparator {

    private int second = 2;

    public void notEqualsString() {
        <warning descr="'!method()' can be inverted to 'method()'">if (!method()) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    private boolean method() {
        return true;
    }

}