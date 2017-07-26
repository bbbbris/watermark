package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableInt;

public class ShowText {
	int TdIndex;
	int TmIndex;
	int lineIndex;
	List<Space> spaces;

	public static final String TEXT_MATRIX_OPERATOR = "Tm";
	public static final String MOVE_TEXT_POSITION = "T[dD]";

	public ShowText(BufferedReader br, String currentLine, List<String> lines, MutableInt lineIndex,
			int indexOfPreviousTj) throws IOException {

		// System.out.println("\t\t\tShowText");

		this.lineIndex = lineIndex.getValue();
		TdIndex = -1;
		TmIndex = -1;
		spaces = new ArrayList<>();

		Pattern pattern = Pattern.compile("[ \\t\\x0B\\f\\r]");
		Matcher matcher = pattern.matcher(currentLine);

		while (matcher.find()) {
			spaces.add(new Space(lineIndex, matcher.start(), matcher.end()));
		}

		if (indexOfPreviousTj != -1) {
			for (int i = lineIndex.getValue(); i >= indexOfPreviousTj; i--) {
				if (lines.get(i).matches(".*" + MOVE_TEXT_POSITION + "\\r\\n")) {
					TdIndex = i;
				} else if (lines.get(i).matches(".*" + TEXT_MATRIX_OPERATOR + "\\r\\n")) {
					TmIndex = i;
				}
			}
		}
	}

	public ShowText(String[] lines, MutableInt lineIndex, double[] watermark, MutableInt watermarkIndex,
			double difference) {
//		System.out.println("\t\tShowText - " + lines[lineIndex.getValue()]);

		spaces = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\)(-*\\d*)\\(");
		Matcher matcher = pattern.matcher(lines[lineIndex.getValue()]);

		while (matcher.find()) {
			// System.out.println(lines[lineIndex.getValue()].substring(matcher.start(),
			// matcher.end()) + " " + Integer.parseInt(matcher.group(1)));
			spaces.add(new Space(lines, lineIndex, matcher.start(), matcher.end(), Integer.parseInt(matcher.group(1))));
		}

		spaces = filterSpaces(spaces);
//		for (Space space : spaces) {
//			System.out.println("\t\t\t" + space.getLength());
//		}

		for (int i = 0; i < spaces.size() && watermarkIndex.getValue() < watermark.length; i++) {
			if (watermark[watermarkIndex.getValue()] == 1) {
				spaces.get(i).lengthen(difference);
			}
			watermarkIndex.increment();
		}
		
		String line = lines[lineIndex.getValue()];
		for(Space space : spaces){
			line = line.substring(0, space.getStart()) + space.getLength() + line.substring(space.getEnd());
//			System.out.println("M" + marked);
		}
		lines[lineIndex.getValue()] = line;
//		System.out.println("W - " + lines[lineIndex.getValue()]);
	}

	private List<Space> filterSpaces(List<Space> spaces) {
		List<Space> filtered = new ArrayList<>();

		int spacelength = 0;
		for (Space space : spaces) {
			spacelength = Math.max(spacelength, Math.abs(space.getLength()));
		}

		for (Space space : spaces) {
			if (Math.abs(space.getLength()) > spacelength * 0.9 && Math.abs(space.getLength()) > 100) {
				filtered.add(space);
			}
		}

		// for(Space space : filtered){
		// System.out.print(space.getLineIndex() + " ");
		// }
		// System.out.println();

		return filtered;
	}

	public void embed(List<String> lines, int[] watermark, MutableInt watermarkIndex, MutableInt cumulativeShift) {
		System.out.println("before: " + lines.get(lineIndex));

		for (int i = 1; i < spaces.size() - 2 && watermarkIndex.getValue() < watermark.length; i++) {
			if (watermark[watermarkIndex.getValue()] == 1) {
				spaces.get(i).embed(lines, watermark, cumulativeShift);
				shiftSpaces(i);
			}
			watermarkIndex.increment();
		}

		System.out.println("after: " + lines.get(lineIndex));

		int lineLength = lines.get(lineIndex).length();
		String newLine = "[" + lines.get(lineIndex).substring(0, lineLength - 4) + "] TJ\r\n";
		cumulativeShift.add(3);
		lines.set(lineIndex, newLine);

		System.out.println("after: " + lines.get(lineIndex));
	}

	public void shiftSpaces(int startIndex) {
		for (int i = startIndex; i < this.spaces.size() - 1; i++) {
			spaces.get(i).shift();
		}
	}

	@Override
	public String toString() {
		return "\n\t\t\tShowText [TdIndex=" + TdIndex + ", TmIndex=" + TmIndex + ", lineIndex=" + lineIndex
				+ ", \n\t\t\t\tspaces" + spaces.toString() + "]";
	}

	public int getLineIndex() {
		return lineIndex;
	}

}
