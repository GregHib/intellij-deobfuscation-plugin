package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	public int magic;

	public TestClass() {
		magic = 1741576309;
	}

	public void read() {
		magic = stream.readUnsignedShort() * <caret>-1741576309;
	}

	public void magic() {
		int lol = magic * <caret>1764050979;
	}

}