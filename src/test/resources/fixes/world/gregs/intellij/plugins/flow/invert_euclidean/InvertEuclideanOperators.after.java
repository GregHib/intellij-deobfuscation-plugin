package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	public byte[] buffer;
	public int index;

	public void writeShortLE128(int i) {
		buffer[(index += 1) - 1] = (byte) (i + 128);
		buffer[(index += 1) - 1] = (byte) (i >> 8);
	}

	public int readShort(int i) {
		index += 2;
		int i_6_ = (((buffer[index - 2] & 0xff) << 8) + (buffer[index - 1] & 0xff));
		if (i_6_ > 32767) {
			i_6_ -= 65536;
		}
		return i_6_;
	}

	public int readUnsignedShort() {
		index += 2;
		return ((buffer[index - 1] & 0xff) + ((buffer[index - 2] & 0xff) << 8));
	}

}