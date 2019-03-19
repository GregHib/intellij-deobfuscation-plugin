class InverseElseComparator {

    private int second = 2;
    private boolean bool = false;

    public void notBoolean() {
        <warning descr="'!bool' can be inverted to 'bool'">if (!bool) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void booleanNestedInverse() {
        <warning descr="'!(bool || bool)' can be inverted to 'bool || bool'">if (!(bool || bool)) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void booleanIgnored() {
        if (bool) {
            second = 1;
        } else {
            second = 2;
        }
    }

    public void doubleBooleanIgnored() {
        if (bool && bool) {
            second = 1;
        } else {
            second = 2;
        }
    }

    public void booleanLiteral() {
        <warning descr="'bool != true' can be inverted to 'bool == true'">if (bool != true) {
            second = 2;
        } else {
            second = 1;
        }</warning>
    }

}