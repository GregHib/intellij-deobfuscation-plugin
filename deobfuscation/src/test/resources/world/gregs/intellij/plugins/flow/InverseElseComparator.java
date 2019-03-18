class InverseElseComparator {

    private int first = 1;
    private int second = 2;

    public void simpleInversion() {
        <warning descr="'first != 1' can be inverted to 'first == 1'">if (first != 1) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void simpleInversionReversed() {
        <warning descr="'1 != first' can be inverted to '1 == first'">if (1 != first) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

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

    public void doubleExpressionInversion() {
        <warning descr="'first != 77 && first != 92' can be inverted to 'first == 77 || first == 92'">if (first != 77 && first != 92) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void doubleExpressionIgnored() {
        if (first == 106 || first == 118) {
            second = 1;
        } else {
            second = 2;
        }
    }

    public void elseIfIgnored() {
        if (first != 1) {
            second = 1;
        } else if(second != 1) {
            second = 2;
        }
    }

}