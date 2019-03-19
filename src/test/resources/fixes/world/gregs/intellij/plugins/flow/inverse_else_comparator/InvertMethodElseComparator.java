class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        if <caret>(!success()) {
            second = 2;
        } else {
            second = 1;
        }
    }

    private boolean success() {
        return true;
    }

}