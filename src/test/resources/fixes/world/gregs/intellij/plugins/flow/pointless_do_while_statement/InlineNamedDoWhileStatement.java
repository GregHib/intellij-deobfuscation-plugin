class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        <caret>while_89_:
        do {
            if (first == 0) {
                second = 1;
                break;
            } else if (second != 1) {
                break;
            }
            first = 0;
        } while (false);
        if (first != 0) {
            second = 2;
        }
    }

}