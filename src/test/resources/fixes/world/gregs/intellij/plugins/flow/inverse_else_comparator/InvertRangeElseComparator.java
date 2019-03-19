class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        if <caret>(first < 30 || first >= 35) {
            second = 2;
        } else {
            second = 1;
        }
    }

}