package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	private static int value;

	static {
		value = 894014710;
	}

	public static int method() {
		return <caret>value * -275612851;
	}

}