class InverseElseComparator {

    private int first = 1;
    private int second = 2;
    private boolean bool = false;

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

    public void doubleExpressionInversion() {
        <warning descr="'first != 77 && first != 92' can be inverted to 'first == 77 || first == 92'">if (first != 77 && first != 92) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void tripleExpression() {
        <warning descr="'first != 77 && first != 92 && first != 15' can be inverted to 'first == 77 || first == 92 || first == 15'">if (first != 77 && first != 92 && first != 15) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void mixedExpression() {
        <warning descr="'bool != false && 15 != first && !bool' can be inverted to 'bool == false || 15 == first || bool'">if (bool != false && 15 != first && !bool) {
            second = 1;
        } else {
            second = 2;
        }</warning>
    }

    public void mixedDouble() {
        <warning descr="'first != 65535 && !bool' can be inverted to 'first == 65535 || bool'">if (first != 65535 && !bool) {
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