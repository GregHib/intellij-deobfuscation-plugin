class NestedElse {

    private int first = 1;
    private int second = 2;

    public void simpleNest() {
        if (first == 1) {
            second = 1;
        } <warning descr="'else' can be inlined">else</warning> {
            if(first == 2) {
                second = 2;
            }
        }
    }

    public void doubleNest() {
        if (first == 1) {
            second = 1;
        } <warning descr="'else' can be inlined">else</warning> {
            if(first == 2) {
                second = 2;
            } <warning descr="'else' can be inlined">else</warning> {
                if(first == 3) {
                    second = 3;
                } else {
                    second = 4;
                }
            }
        }
    }

    public void nestMultiStatementIgnored() {
        if (first == 1) {
            second = 1;
        } else {
            if(first == 2) {
                second = 2;
            }
            second = 4;
        }
    }

    public void singleIfNestIgnored() {
        if (first == 1) {
            if(first == 2) {
                second = 2;
            }
        }
    }

}