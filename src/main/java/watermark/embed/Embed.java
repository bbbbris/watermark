package watermark.embed;

import watermark.embed.pdfStructure.Document;

public class Embed {

	private String WORKING_LOCATION = "/Users/abrisnagy/Documents/development/watermark-embed/test_documents/";
	private String DOCUMENT_NAME = "test_O.pdf";

	public Embed(String wORKING_LOCATION, String dOCUMENT_NAME) {
		WORKING_LOCATION = wORKING_LOCATION;
		DOCUMENT_NAME = dOCUMENT_NAME;
	}

	public void embed(int[] watermark) {
		@SuppressWarnings("unused")
		Document docu = new Document(WORKING_LOCATION + DOCUMENT_NAME, watermark, "Tesseract");
	}
}
