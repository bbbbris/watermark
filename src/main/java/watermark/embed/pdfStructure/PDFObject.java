package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

public class PDFObject {
	int startLineIndex;
	int endLineIndex;
	int objectNumber;
	int generationNumber;
	int xrefTableEntryIndex;
	List<Stream> streams;

	public static final String END_OF_OBJECT_MARKER = "endobj";
	public static final String START_OF_STREAM_MARKER = "stream";

	public PDFObject(BufferedReader br, String currentLine, List<String> lines, MutableInt lineIndex)
			throws IOException {
		startLineIndex = lineIndex.getValue();
		String line = currentLine;
		streams = new ArrayList<>();

		objectNumber = Integer.parseInt(line.split(" ")[0]);
		generationNumber = Integer.parseInt(line.split(" ")[1]);

		while (!line.contains(END_OF_OBJECT_MARKER)) {
			line = br.readLine();
			lineIndex.increment();
			lines.add(line + "\r\n");
			if (line.contains(START_OF_STREAM_MARKER)) {
				streams.add(new Stream(br, line, lines, lineIndex));
				line = lines.get(lineIndex.getValue());
			}
		}
		endLineIndex = lineIndex.getValue();
	}
	
	public void embed(List<String> lines, int[] watermark, MutableInt watermarkIndex, MutableInt offsetChange) {
		
		updateXrefTableEntry(lines, offsetChange);
		
		for(Stream stream : streams){
			stream.embed(lines, watermark, watermarkIndex, offsetChange);
		}
	}

	private void updateXrefTableEntry(List<String> lines, MutableInt offsetChange) {
		String xrefTableEntry = lines.get(xrefTableEntryIndex);
		int oldOffset = Integer.parseInt(xrefTableEntry.substring(0, 10));
		int currentOffset = oldOffset + offsetChange.getValue();
		String updatedEntry = currentOffset + lines.get(xrefTableEntryIndex).substring(10);
		
//		System.out.println("offsetChange: " + offsetChange.getValue());
		lines.set(xrefTableEntryIndex, getPadded(updatedEntry));
	}

	private String getPadded(String shortString) {
		while (shortString.length() < 20) {
			shortString = "0" + shortString;
		}
		return shortString;
	}

	public void setXrefTableEntryIndex(int xrefTableEntryIndex) {
		this.xrefTableEntryIndex = xrefTableEntryIndex;
	}

	@Override
	public String toString() {
		return "PDFObject [objectReferenceNumber=" + objectNumber + ", startLineIndex=" + startLineIndex
				+ ", endLineIndex=" + endLineIndex + ", generationNumber=" + generationNumber + ", xrefTableEntryIndex="
				+ xrefTableEntryIndex + /*", \n\tstreams=" + streams.toString() + */"]";
	}

}
