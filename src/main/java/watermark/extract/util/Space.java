package watermark.extract.util;

public class Space {
	private int rowNumber;
	private int startPosition;
	private int endPosition;
	private double probability;

	public Space() {

	}

	public Space(int rowNumber, int startPosition, int endPosition) {
		this.rowNumber = rowNumber;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.probability = 0;
	}

	public Space(int rowNumber, int startPosition, int endPosition, double probability) {
		this.rowNumber = rowNumber;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.probability = probability;
	}

	public int getLength() {
		return endPosition - startPosition;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	@Override
	public String toString() {
		return "Space [rowNumber=" + rowNumber + ", startPosition=" + startPosition + ", endPosition=" + endPosition
				+ ", probability=" + probability + "]";
	}

}
