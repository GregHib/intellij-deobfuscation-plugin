class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        if <caret>(1 != first) {
            second = 2;
        } else {
            second = 1;
        }
    }

}