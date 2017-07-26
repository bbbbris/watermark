package watermark.embed;

import watermark.embed.pdfStructure.Document;
import watermark.test.common.Comparison;

public class Embed {

	private String WORKING_LOCATION = "/Users/abrisnagy/Documents/development/watermark-embed/test_documents/";
	private String DOCUMENT_NAME = "test_O.pdf";
	private Document document;
	private Comparison comparison;

	public Embed(String wORKING_LOCATION, String dOCUMENT_NAME) {
		WORKING_LOCATION = wORKING_LOCATION;
		DOCUMENT_NAME = dOCUMENT_NAME;
	}

	public void embed(int[] watermark, double difference) {
		comparison = new Comparison();
		document = new Document(WORKING_LOCATION + DOCUMENT_NAME, watermark, comparison, difference);
	}

	public int getNumberOfPages() {
		return document.getNumberOfPages();
	}
	
	public Comparison getComparison(){
		return comparison;
	}

}
