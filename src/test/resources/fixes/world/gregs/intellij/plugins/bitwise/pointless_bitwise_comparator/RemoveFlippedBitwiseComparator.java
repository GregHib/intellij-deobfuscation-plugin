class Comparator {

    private int first = 1;

    public void method() {
        boolean result = <caret>-1 <= (0xffffffff ^ first + 4);
    }

}