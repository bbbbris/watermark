package watermark.embed.pdfStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableInt;

public class Page {
	String pageStream;
	String[] lines;
	List<Text> texts;

	public Page(String pageStream) {
		this.pageStream = pageStream;
		lines = pageStream.split("(?<=Tj|TJ|Td|TD|Tf|Tm)");
		// for(int i= 0; i<lines.length; i++){
		// System.out.println(lines[i]);
		// }
	}

	public String embed(double[] watermarkEncoded) {
//		System.out.println("Page");
		
		texts = new ArrayList<>();
		MutableInt lineIndex = new MutableInt(0);
		MutableInt watermarkIndex = new MutableInt(0);
		
		for (; lineIndex.getValue() < lines.length; lineIndex.increment()) {
			String currentLine = lines[lineIndex.getValue()];
			Pattern pattern = Pattern.compile("BT");
			Matcher matcher = pattern.matcher(currentLine);

			while (matcher.find()) {
				texts.add(new Text(lines, lineIndex, watermarkEncoded, watermarkIndex));
			}
		}
		String watermarked = flatten(lines);
		return watermarked;
	}

	private String flatten(String[] lines) {
		String flat = "";
		for(String line : lines){
			flat += line;
		}
		return flat;
	}
}
