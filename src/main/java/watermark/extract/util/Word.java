package watermark.extract.util;

public class Word {

	int topRight_X;
	int topRight_Y;
	int bottomLeft_X;
	int bottomLeft_Y;

	public Word(int topRight_X, int topRight_Y, int bottomLeft_X, int bottomLeft_Y) {
		this.topRight_X = topRight_X;
		this.topRight_Y = topRight_Y;
		this.bottomLeft_X = bottomLeft_X;
		this.bottomLeft_Y = bottomLeft_Y;
	}

	public int getTopRight_X() {
		return topRight_X;
	}

	public void setTopRight_X(int topRight_X) {
		this.topRight_X = topRight_X;
	}

	public int getTopRight_Y() {
		return topRight_Y;
	}

	public void setTopRight_Y(int topRight_Y) {
		this.topRight_Y = topRight_Y;
	}

	public int getBottomLeft_X() {
		return bottomLeft_X;
	}

	public void setBottomLeft_X(int bottomLeft_X) {
		this.bottomLeft_X = bottomLeft_X;
	}

	public int getBottomLeft_Y() {
		return bottomLeft_Y;
	}

	public void setBottomLeft_Y(int bottomLeft_Y) {
		this.bottomLeft_Y = bottomLeft_Y;
	}

	@Override
	public String toString() {
		return "(" + topRight_X + "," + topRight_Y + "),(" + bottomLeft_X
				+ "," + bottomLeft_Y + ")";
	}

}
