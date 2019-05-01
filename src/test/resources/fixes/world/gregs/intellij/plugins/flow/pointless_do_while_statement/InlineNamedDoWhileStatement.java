class Comparator {

    private int first = 1;

    public void method() {
        <caret>while_89_:
        do {
            if (first != 1) {
                break;
            }
        } while (false);
    }

}