class Comparator {

    private int first = 1;

    public void method() {
        boolean result = <caret>(0x400 & first ^ 0xffffffff) == -1;
    }

}