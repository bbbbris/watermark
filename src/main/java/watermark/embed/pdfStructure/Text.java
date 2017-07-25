package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableInt;

public class Text {
	int startLineIndex;
	int endLineIndex;
	List<ShowText> showTexts;

	public static final String END_OF_TEXT_MARKER = "ET";
	public static final String END_OF_STREAM_MARKER = ".*endstream.*";
	public static final String END_OF_OBJECT_MARKER = ".*endobj.*";
	public static final String SHOW_TEXT_OPERATOR = "T[jJ]";

	public Text(BufferedReader br, String currentLine, List<String> lines, MutableInt lineIndex) throws IOException {
		startLineIndex = lineIndex.getValue();
		String line = currentLine;
		showTexts = new ArrayList<>();
		int indexOfPreviousTj = -1;

		// System.out.println("\t\tText");

		while (!line.contains(END_OF_TEXT_MARKER) && !line.matches(END_OF_STREAM_MARKER)
				&& !line.matches(END_OF_OBJECT_MARKER)) {
			if (line.contains("Tj")) {
				if (showTexts.size() != 0) {
					indexOfPreviousTj = showTexts.get(showTexts.size() - 1).getLineIndex();
				}
				showTexts.add(new ShowText(br, line, lines, lineIndex, indexOfPreviousTj));
			}
			line = br.readLine();
			lineIndex.increment();
			lines.add(line + "\r\n");
		}
		endLineIndex = lineIndex.getValue();
	}

	public Text(String[] lines, MutableInt lineIndex, double[] watermark, MutableInt watermarkIndex) {
//		System.out.println("\tText");

		showTexts = new ArrayList<>();
		startLineIndex = lineIndex.getValue();

		for (; lineIndex.getValue() < lines.length; lineIndex.increment()) {
			Pattern pattern = Pattern.compile("Tj|TJ");
			Matcher matcher = pattern.matcher(lines[lineIndex.getValue()]);

			if (matcher.find()) {
				showTexts.add(new ShowText(lines, lineIndex, watermark, watermarkIndex));
			}
		}
	}

	@Override
	public String toString() {
		return "\n\t\tText [startLineIndex=" + startLineIndex + ", endLineIndex=" + endLineIndex + ", \n\t\t\tshowTexts"
				+ showTexts.toString() + "]";
	}

	public void embed(List<String> lines, int[] watermark, MutableInt watermarkIndex, MutableInt lengthChange) {
		for (ShowText showText : showTexts) {
			showText.embed(lines, watermark, watermarkIndex, lengthChange);
		}
	}
}
