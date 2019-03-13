class PointlessBitwiseComparator {

    private int first = 1;
    private int second = 2;

    public void bitwiseLessThanNegative() {
        boolean result = <warning descr="'(first ^ 0xffffffff) < -32768' can be replaced with 'first > 32767'">(first ^ 0xffffffff) < -32768</warning>;
    }

    public void bitwiseLessThanPositive() {
        boolean result = <warning descr="'(first ^ 0xffffffff) < 32768' can be replaced with 'first > -32769'">(first ^ 0xffffffff) < 32768</warning>;
    }

    public void bitwiseLessThanReversed() {
        boolean result = <warning descr="'-32768 < (first ^ 0xffffffff)' can be replaced with 'first > 32767'">-32768 < (first ^ 0xffffffff)</warning>;
    }

    public void bitwiseLessThanOperandReversed() {
        boolean result = <warning descr="'-32768 < (0xffffffff ^ first)' can be replaced with 'first > 32767'">-32768 < (0xffffffff ^ first)</warning>;
    }

    public void bitwiseLessThanBitwise() {
        boolean result = <warning descr="'(first ^ 0xffffffff) < (0xffffffff ^ second)' can be replaced with 'second < first'">(first ^ 0xffffffff) < (0xffffffff ^ second)</warning>;
    }

    public void bitwiseEquals() {
        boolean result = <warning descr="'(first ^ 0xffffffff) == -1' can be replaced with 'first == 0'">(first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseNotEquals() {
        boolean result = <warning descr="'(0xffffffff ^ first) != -1' can be replaced with 'first != 0'">(0xffffffff ^ first) != -1</warning>;
    }

    public void bitwiseGreaterThan() {
        boolean result = <warning descr="'(first ^ 0xffffffff) > -1' can be replaced with 'first < 0'">(first ^ 0xffffffff) > -1</warning>;
    }

    public void bitwiseLessThanEqual() {
        boolean result = <warning descr="'-1 <= (first ^ 0xffffffff)' can be replaced with 'first >= 0'">-1 <= (first ^ 0xffffffff)</warning>;
    }

    public void bitwiseGreaterThanEqual() {
        boolean result = <warning descr="'(first ^ 0xffffffff) >= -221' can be replaced with 'first <= 220'">(first ^ 0xffffffff) >= -221</warning>;
    }

    public void bitwiseNegative() {
        boolean result = -(first ^ 0xffffffff) == -2;
    }

    public void bitwiseNot() {
        boolean result = !(<warning descr="'(first ^ 0xffffffff) == -2' can be replaced with 'first == 1'">(first ^ 0xffffffff) == -2</warning>);
    }

    public void bitwiseOnes() {
        boolean result = ~(first ^ 0xffffffff) == -2;
    }

    public void bitwiseAnd() {
        boolean result = <warning descr="'(first & 0xff ^ 0xffffffff) == -1' can be replaced with '(first & 0xff) == 0'">(first & 0xff ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseAndReversed() {
        boolean result = <warning descr="'(0xff & first ^ 0xffffffff) == -1' can be replaced with '(0xff & first) == 0'">(0xff & first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseOr() {
        boolean result = (first | 1 ^ 0xffffffff) == -1;
    }

    public void bitwiseOrReversed() {
        boolean result = (1 | first ^ 0xffffffff) == -1;
    }

    public void bitwiseShiftLeft() {
        boolean result = <warning descr="'(first << 16 ^ 0xffffffff) == -1' can be replaced with 'first << 16 == 0'">(first << 16 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseShiftLeftReversed() {
        boolean result = <warning descr="'(16 << first ^ 0xffffffff) == -1' can be replaced with '16 << first == 0'">(16 << first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseShiftRight() {
        boolean result = <warning descr="'(first >> 16 ^ 0xffffffff) == -1' can be replaced with 'first >> 16 == 0'">(first >> 16 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseShiftRightReversed() {
        boolean result = <warning descr="'(16 >> first ^ 0xffffffff) == -1' can be replaced with '16 >> first == 0'">(16 >> first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseAdd() {
        boolean result = <warning descr="'(first + 1 ^ 0xffffffff) == -1' can be replaced with 'first + 1 == 0'">(first + 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseAddReversed() {
        boolean result = <warning descr="'(1 + first ^ 0xffffffff) == -1' can be replaced with '1 + first == 0'">(1 + first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseSubtract() {
        boolean result = <warning descr="'(first - 1 ^ 0xffffffff) == -1' can be replaced with 'first - 1 == 0'">(first - 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseSubtractReversed() {
        boolean result = <warning descr="'(1 - first ^ 0xffffffff) == -1' can be replaced with '1 - first == 0'">(1 - first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseMultiply() {
        boolean result = <warning descr="'(first * 1 ^ 0xffffffff) == -1' can be replaced with 'first * 1 == 0'">(first * 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseMultiplyReversed() {
        boolean result = <warning descr="'(1 * first ^ 0xffffffff) == -1' can be replaced with '1 * first == 0'">(1 * first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseDivide() {
        boolean result = <warning descr="'(first / 1 ^ 0xffffffff) == -1' can be replaced with 'first / 1 == 0'">(first / 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseDivideReversed() {
        boolean result = <warning descr="'(1 / first ^ 0xffffffff) == -1' can be replaced with '1 / first == 0'">(1 / first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseModulus() {
        boolean result = <warning descr="'(first % 1 ^ 0xffffffff) == -1' can be replaced with 'first % 1 == 0'">(first % 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseModulusReversed() {
        boolean result = <warning descr="'(1 % first ^ 0xffffffff) == -1' can be replaced with '1 % first == 0'">(1 % first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseIncrement() {
        boolean result = <warning descr="'(first++ ^ 0xffffffff) == -1' can be replaced with 'first++ == 0'">(first++ ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseIncrementReversed() {
        boolean result = <warning descr="'(++first ^ 0xffffffff) == -1' can be replaced with '++first == 0'">(++first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseDecrement() {
        boolean result = <warning descr="'(first-- ^ 0xffffffff) == -1' can be replaced with 'first-- == 0'">(first-- ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseDecrementReversed() {
        boolean result = <warning descr="'(--first ^ 0xffffffff) == -1' can be replaced with '--first == 0'">(--first ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseMultiple() {
        boolean result = <warning descr="'(1 + first + second + 1 ^ 0xffffffff) == -1' can be replaced with '1 + first + second + 1 == 0'">(1 + first + second + 1 ^ 0xffffffff) == -1</warning>;
    }

    public void bitwiseMultipleBitwise() {
        boolean result = <warning descr="'((first << 6) / 2 ^ 0xffffffff) < (0xffffffff ^ second - 4)' can be replaced with 'second - 4 < (first << 6) / 2'">((first << 6) / 2 ^ 0xffffffff) < (0xffffffff ^ second - 4)</warning>;
    }

}