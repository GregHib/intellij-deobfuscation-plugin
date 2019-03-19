class Comparator {

    private int first = 1;
    private int second = 2;

    public void positive() {
        boolean result = <warning descr="'(first ^ 0xffffffff) != 32768' can be replaced with 'first != -32769'">(first ^ 0xffffffff) != 32768</warning>;
    }

    public void regular() {
        boolean result = <warning descr="'(first ^ 0xffffffff) != -32768' can be replaced with 'first != 32767'">(first ^ 0xffffffff) != -32768</warning>;
    }

    public void flipped() {
        boolean result = <warning descr="'-32768 != (first ^ 0xffffffff)' can be replaced with 'first != 32767'">-32768 != (first ^ 0xffffffff)</warning>;
    }

    public void inverted() {
        boolean result = <warning descr="'-32768 != (0xffffffff ^ first)' can be replaced with 'first != 32767'">-32768 != (0xffffffff ^ first)</warning>;
    }

    public void complimented() {
        boolean result = <warning descr="'~first != -32768' can be replaced with 'first != 32767'">~first != -32768</warning>;
    }

    public void prefix() {
        boolean result = <warning descr="'(--first ^ 0xffffffff) != -32768' can be replaced with '--first != 32767'">(--first ^ 0xffffffff) != -32768</warning>;
    }

    public void suffix() {
        boolean result = <warning descr="'(first++ ^ 0xffffffff) != -32768' can be replaced with 'first++ != 32767'">(first++ ^ 0xffffffff) != -32768</warning>;
    }

    public void parenthesised() {
        boolean result = <warning descr="'((first * 4) ^ 0xffffffff) != -32768' can be replaced with '(first * 4) != 32767'">((first * 4) ^ 0xffffffff) != -32768</warning>;
    }

    public void variable() {
        boolean result = <warning descr="'(first ^ 0xffffffff) != ~second' can be replaced with 'second != first'">(first ^ 0xffffffff) != ~second</warning>;
    }

    public void obstructions() {
        boolean result = <warning descr="'((first << 6) / 2 ^ 0xffffffff) != (0xffffffff ^ second - 4)' can be replaced with 'second - 4 != (first << 6) / 2'">((first << 6) / 2 ^ 0xffffffff) != (0xffffffff ^ second - 4)</warning>;
    }
}