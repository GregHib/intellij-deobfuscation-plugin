class Comparator {

    private int first = 1;

    public void method() {
        boolean result = <caret>~(first & 0x400) == -1;
    }

}