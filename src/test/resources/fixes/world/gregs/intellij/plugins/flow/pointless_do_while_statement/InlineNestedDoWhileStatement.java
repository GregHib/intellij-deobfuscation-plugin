class Comparator {

    private int first = 1;

    public void method() {
        <caret>while_90_:
            do {
                while_89_:
                do {
                    if (first != 1) {
                        first = 1;
                        break while_89_;
                    }
                    first = 2;
                    break while_90_;
                } while (false);
                first = 3;
            } while (false);
    }

}