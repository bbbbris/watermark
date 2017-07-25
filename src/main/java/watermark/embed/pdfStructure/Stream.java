package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

public class Stream {
	int startLineIndex;
	int endLineIndex;
	int length;
	int indexOfLineWithLength;
	int beginningOfLength;
	int endOfLength;
	List<Text> texts;

	public static final String END_OF_STREAM_MARKER = ".*endstream.*";
	public static final String END_OF_OBJECT_MARKER = ".*endobj.*";
	public static final String START_OF_TEXT_MARKER = "BT";

	public Stream(BufferedReader br, String currentLine, List<String> lines, MutableInt lineIndex) throws IOException {
		
		startLineIndex = lineIndex.getValue();
		String line = currentLine;
		texts = new ArrayList<>();
		length = getLength(currentLine, lines, lineIndex);

		while (!line.matches(END_OF_STREAM_MARKER) && !line.matches(END_OF_OBJECT_MARKER)) {
			line = br.readLine();
			lineIndex.increment();
			lines.add(line + "\r\n");
			if (line.contains(START_OF_TEXT_MARKER)) {
				texts.add(new Text(br, line, lines, lineIndex));
				line = lines.get(lineIndex.getValue());
			}
		}
		endLineIndex = lineIndex.getValue();
		System.out.println("\tEndStream " + lines.get(lineIndex.getValue()));
	}

	private int getLength(String currentLine, List<String> lines, MutableInt lineIndex) {
		length = -1;

		for (int i = 0; i < lineIndex.getValue(); i++) {
			if (lines.get(i).contains("/Length")) {
				int beginning = lines.get(i).lastIndexOf("/Length") + 8;
				int end = lines.get(i).substring(beginning).indexOf(" ");
				length = Integer.parseInt(lines.get(i).substring(beginning, beginning + end));

				indexOfLineWithLength = i;
				beginningOfLength = beginning;
				endOfLength = beginning + end;
			}
		}

		return length;
	}

	@Override
	public String toString() {
		return "\n\tStream [startLineIndex=" + startLineIndex + ", endLineIndex=" + endLineIndex + ", length=" + length
				+ ", \n\t\ttexts" + texts.toString() + "]";
	}

	public void embed(List<String> lines, int[] watermark, MutableInt watermarkIndex, MutableInt offsetChange) {
		MutableInt newLength = new MutableInt(length);

		for (Text text : texts) {
			text.embed(lines, watermark, watermarkIndex, newLength);
		}

		int numberOfDigitsInNewLength = String.valueOf(newLength.getValue()).length();
		int numberOfDigitsInOriginalLength = String.valueOf(length).length();		
		
		changeLengthOfStream(lines, newLength);
		offsetChange.add(newLength.intValue()-length);
		
		offsetChange.add(numberOfDigitsInNewLength-numberOfDigitsInOriginalLength);
	}

	private void changeLengthOfStream(List<String> lines, MutableInt newLength) {
		String partBefore = lines.get(indexOfLineWithLength).substring(0, beginningOfLength);
		String partAfter = lines.get(indexOfLineWithLength).substring(endOfLength);

		String newLine = partBefore + newLength + partAfter;
		lines.set(indexOfLineWithLength, newLine);
	}
}
