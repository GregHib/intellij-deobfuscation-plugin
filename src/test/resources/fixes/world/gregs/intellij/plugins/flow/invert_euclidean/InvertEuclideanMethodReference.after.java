package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	public int magic;

	public TestClass() {
		magic = -1;
	}

	public void read() {
		magic = stream.readUnsignedShort() * 1;
	}

	public void magic() {
		int lol = magic;
	}

}