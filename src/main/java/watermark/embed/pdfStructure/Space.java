package watermark.embed.pdfStructure;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

public class Space {
	private int lineIndex;
	private int startIndexInline;
	private int endIndexInline;
	private int length;

	private static final String space = ")-200(";

	public Space(MutableInt lineIndex, int startIndexInline, int endIndexInline) {

		System.out.println("\t\t\t\tSpace");

		this.lineIndex = lineIndex.getValue();
		this.startIndexInline = startIndexInline;
		this.endIndexInline = endIndexInline - 1;
	}

	public Space(String[] lines, MutableInt lineIndex, int start, int end, int length) {
		// System.out.println("\t\t\tSpace");

		this.lineIndex = lineIndex.getValue();
		this.startIndexInline = start + 1;
		this.endIndexInline = end - 1;
		this.length = length;
	}

	public void embed(List<String> lines, int[] watermark, MutableInt cumulativeShift) {
		String line = lines.get(lineIndex);
		String beforeSpace = line.substring(0, startIndexInline);
		String afterSpace = line.substring(endIndexInline);

		lines.set(lineIndex, beforeSpace + space + afterSpace);
		cumulativeShift.add(space.length());
	}

	public void lengthen() {
		this.length = (int) (this.length * 1.4);
	}

	public void shift() {
		startIndexInline += space.length();
		endIndexInline += space.length();
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public int getStart() {
		return startIndexInline;
	}

	public void setStart(int startIndexInline) {
		this.startIndexInline = startIndexInline;
	}

	public int getEnd() {
		return endIndexInline;
	}

	public void setEnd(int endIndexInline) {
		this.endIndexInline = endIndexInline;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString() {
		return "\n\t\t\t\tSpace [lineIndex=" + lineIndex + ", startIndexInline=" + startIndexInline
				+ ", endIndexInline=" + endIndexInline + "]";
	}

}
