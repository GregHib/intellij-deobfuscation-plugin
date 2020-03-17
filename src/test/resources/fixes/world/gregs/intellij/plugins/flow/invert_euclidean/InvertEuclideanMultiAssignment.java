package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	private static int value;

	public static void method() {
		value = <caret>986798515;//1
	}

	public static void method2() {
		value = <caret>-1334571751;//3
	}

	public static int method3() {
		return <caret>value * 1596783995;
	}

}