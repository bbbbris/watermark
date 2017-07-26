package watermark.test.scan;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class Scanner {

	public void scan(String WORKING_LOCATION, String documentName) {
		String watermarkedDocument = WORKING_LOCATION + documentName.substring(0, documentName.length() - 4) + "_W.pdf";

		System.out.println(watermarkedDocument);

		try {
			PDDocument document = PDDocument.load(new File(watermarkedDocument));

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {

				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

				ImageIOUtil.writeImage(bim,
						watermarkedDocument.substring(0, watermarkedDocument.length() - 4) + "-" + (page + 1) + ".png",
						300);

			}

			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String originalDocument = WORKING_LOCATION + documentName;
		try {
			PDDocument document = PDDocument.load(new File(originalDocument));

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {

				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

				ImageIOUtil.writeImage(bim,
						originalDocument.substring(0, originalDocument.length() - 4) + "-" + (page + 1) + ".png", 300);

			}

			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
