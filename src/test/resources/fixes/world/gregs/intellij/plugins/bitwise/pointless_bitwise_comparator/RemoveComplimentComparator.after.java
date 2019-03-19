class Comparator {

    private int first = 1;
    private int second = 2;
    private int[] is = new int[1];

    public void method() {
        boolean result = (first ^ 0xffffffff) > -36 && <caret>is[second - -1] != 0;
    }

}