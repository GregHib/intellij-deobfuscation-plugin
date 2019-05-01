class Comparator {

    private int first = 1;
    private int second = 2;
    private int third = 2;
    private int fourth = 2;
    private int fifth = 2;

    private static boolean method1() {
        return true
    }

    private static boolean method2() {
        return true
    }

    private static boolean method3() {
        return true
    }

    private static boolean method4() {
        return true
    }

    public void method() {
        <caret>while_100_:
        do {
            while_99_:
            do {
                while_98_:
                do {
                    while_97_:
                    do {
                        if (first == -4) {
                            if (second == i_26_ && third == i_30_) {
                                fourth = second;
                                fifth = third;
                                return true;
                            }
                            break while_100_;
                        } else if (first != -3) {
                            if (first == -2) {
                                if (method1()) {
                                    fifth = third;
                                    fourth = second;
                                    return true;
                                }
                            } else if (first != -1) {
                                if (first == 0 || first == 1 || first == 2 || first == 3 || first == 9) {
                                    break while_98_;
                                }
                                break while_99_;
                            }
                            break while_97_;
                        }
                    } while (false);
                    if (method2()) {
                        fifth = third;
                        fourth = second;
                        return true;
                    }
                } while (false);
                if (method3()) {
                    fourth = second;
                    fifth = third;
                    return true;
                }
                break while_100_;
            } while (false);
            if (method4()) {
                fifth = third;
                fourth = second;
                return true;
            }
        } while (false);
    }

}