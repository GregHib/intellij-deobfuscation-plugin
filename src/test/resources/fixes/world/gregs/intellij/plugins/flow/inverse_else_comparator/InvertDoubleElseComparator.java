class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        if <caret>(first != 77 && 92 != first) {
            second = 2;
        } else {
            second = 1;
        }
    }

}