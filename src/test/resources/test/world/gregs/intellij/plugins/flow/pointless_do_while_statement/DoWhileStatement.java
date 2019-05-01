class NestedElse {

    private int first = 1;
    private int second = 2;
    private int third = 3;

    public void simpleNest() {
        <warning descr="'do' can be inlined">do</warning> {
            if (first != 1) {
                break;
            }
        } while (false);
    }

    public void followedByBlock() {
        <warning descr="'do' can be inlined">do</warning> {
            if (first != 1) {
                break;
            }
        } while (false);
        if (first != second) {
            third = 3;
        }
    }

    public void ignoreBreak() {
        <warning descr="'do' can be inlined">do</warning> {
            first = 2;
            break;
        } while (false);
    }

    public void named() {
        <warning descr="'while_89_' can be inlined">while_89_</warning>:
        do {
            if (first == 1) {
                break while_89_;
            }
            first = 2;
            break;
        } while (false);
    }

    public void nestedNames() {
        <warning descr="'while_90_' can be inlined">while_90_</warning>:
            do {
                while_89_:
                    do {
                        if (first == 1) {
                            break while_89_;
                        }
                        first = 2;
                        break while_90_;
                    } while (false);
            } while (false);
    }

    public void complete() {
        <warning descr="'while_90_' can be inlined">while_90_</warning>:
            do {
                while_89_:
                    do {
                        if (first == 1) {
                            first = 3;
                            break while_89_;
                        }
                        first = 2;
                        break while_90_;
                    } while (false);
            } while (false);
    }

}