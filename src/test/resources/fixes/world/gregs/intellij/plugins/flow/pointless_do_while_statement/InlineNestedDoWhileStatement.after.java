class Comparator {

    private int first = 1;

    public void method() {
        if (first != 1) {
            first = 1;
            first = 3;
        } else {
            first = 2;
        }
    }

}