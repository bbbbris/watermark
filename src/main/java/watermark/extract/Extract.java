package watermark.extract;

import watermark.extract.util.Document;

public class Extract {

	private String WORKING_LOCATION = "/Users/abrisnagy/Documents/development/watermark-extract/test_images/";
	private String DOCUMENT_NAME = "test.png";
	private int NUMBER_OF_PAGES = 8;

	public Extract(String wORKING_LOCATION, String dOCUMENT_NAME, int nUMBER_OF_PAGES) {
		WORKING_LOCATION = wORKING_LOCATION;
		DOCUMENT_NAME = dOCUMENT_NAME;
		NUMBER_OF_PAGES = nUMBER_OF_PAGES;
	}

	public double[] extract() {
		Document input = new Document(WORKING_LOCATION + DOCUMENT_NAME, NUMBER_OF_PAGES);
		double[] watermark = input.extractWatermarkTess();
		return watermark;
	}
}
