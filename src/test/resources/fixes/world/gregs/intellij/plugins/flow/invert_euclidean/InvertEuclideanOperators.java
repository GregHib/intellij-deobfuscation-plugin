package fixes.world.gregs.intellij.plugins.flow.invert_euclidean;

class TestClass {

	public byte[] buffer;
	public int index;

	public void writeShortLE128(int i) {
		buffer[<caret>(index += <caret>116413311) * <caret>385051775 - 1] = (byte) (i + 128);
		buffer[<caret>(index += <caret>116413311) * <caret>385051775 - 1] = (byte) (i >> 8);
	}

	public int readShort(int i) {
		index += <caret>232826622;
		int i_6_ = (((buffer[index * <caret>385051775 - 2] & 0xff) << 8) + (buffer[<caret>385051775 * index - 1] & 0xff));
		if (i_6_ > 32767) {
			i_6_ -= 65536;
		}
		return i_6_;
	}

	public int readUnsignedShort() {
		index += <caret>232826622;
		return ((buffer[index * <caret>385051775 - 1] & 0xff) + ((buffer[index * <caret>385051775 - 2] & 0xff) << 8));
	}

}