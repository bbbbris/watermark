package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

public class CrossReferenceTable {
	int startLineIndex;
	int endLineIndex;

	public static final String END_XREF_TABLE_MARKER = "startxref";

	public CrossReferenceTable(BufferedReader br, String currentLine, List<String> lines, MutableInt lineIndex,
			List<PDFObject> objects) throws IOException {

		startLineIndex = lineIndex.getValue();
		String line = currentLine;

		int objectIndex = 0;

		while (!line.contains(END_XREF_TABLE_MARKER)) {
			if (line.matches("\\d{10}\\s\\d{5}\\s[nf].*")) {
				if (!line.matches("0{10}\\s\\d{5}\\s[nf].*")) {
					objects.get(objectIndex).setXrefTableEntryIndex(lineIndex.getValue());
					objectIndex++;
				}
			}
			line = br.readLine();
			lineIndex.increment();
			lines.add(line + "\r\n");
		}
		endLineIndex = lineIndex.getValue();
	}
}
