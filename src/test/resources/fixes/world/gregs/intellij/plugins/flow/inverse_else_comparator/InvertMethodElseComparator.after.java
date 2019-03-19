class Comparator {

    private int first = 1;
    private int second = 1;

    public void method() {
        if (success()) {
            second = 1;
        } else {
            second = 2;
        }
    }

    private boolean success() {
        return true;
    }

}