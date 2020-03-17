package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	private static int value;

	public static void method() {
		value = 1;//1
	}

	public static void method2() {
		value = 3;//3
	}

	public static int method3() {
		return value;
	}

}