package watermark.test;

import watermark.embed.Embed;
import watermark.extract.Extract;

public class Test {

	public static void main(String[] args) {

		String WORKING_LOCATION = "/Users/abrisnagy/Documents/development/watermark/src/main/resources/test documents/";
		String documentName = "test_O.pdf";
		int[] watermark = { 1, 0, 1, 0, 1, 1, 0 };

		Embed embed = new Embed(WORKING_LOCATION, documentName);
		embed.embed(watermark);

		String DOCUMENT_NAME = "test.png";
		int NUMBER_OF_PAGES = 8;

		Extract extract = new Extract(WORKING_LOCATION, DOCUMENT_NAME, NUMBER_OF_PAGES);
		extract.extract();
	}
}
