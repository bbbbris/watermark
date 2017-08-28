package watermark.test.scan;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

public class Scanner {

	public void scan(String WORKING_LOCATION, String documentName, double StdDev, int likelihood, int sizeOfSmear, int rate) {
		String watermarkedDocument = WORKING_LOCATION + documentName.substring(0, documentName.length() - 4) + "_W.pdf";

		System.out.println(watermarkedDocument);

		try {
			PDDocument document = PDDocument.load(new File(watermarkedDocument));

			PDFRenderer pdfRenderer = new PDFRenderer(document);
			for (int page = 0; page < document.getNumberOfPages(); ++page) {

				BufferedImage bim = 
						addSmearsParse(rate, 10, pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY));

//				BufferedImage bim = addSmearsPoint(40, pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY));
//				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.GRAY);


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

	public BufferedImage addGaussian(double StdDev, BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Random r = new Random();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				// System.out.println(
				// image.getRGB(i, j) + "\t" + (int) ((image.getRGB(i, j) -
				// Math.abs((10 * r.nextGaussian())))));
				image.setRGB(i, j,
						Math.min(-1, Math.max(-16777216, (int) ((image.getRGB(i, j) - (StdDev * r.nextGaussian()))))));
			}
		}

		return image;
	}

	public BufferedImage addSmearsParse(int likelihood, int sizeOfSmear, BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Random r = new Random();

		for (int i = 0; i < width; i += sizeOfSmear) {
			for (int j = 0; j < height; j += sizeOfSmear) {

				int maxDarkness = -1;
				for (int a = 0; a < sizeOfSmear && i + a < width; a++) {
					for (int b = 0; b < sizeOfSmear && j + b < height; b++) {
						if (image.getRGB(i + a, j + b) < maxDarkness) {
							maxDarkness = image.getRGB(i + a, j + b);
						}
					}
				}

				if (r.nextInt(likelihood) == 1 && maxDarkness < -500) {

					for (int a = 0; a < sizeOfSmear && i + a < width; a++) {
						for (int b = 0; b < sizeOfSmear && j + b < height; b++) {
							image.setRGB(i + a, j + b, maxDarkness);
						}
					}
				}

			}
		}

		return image;
	}

	public BufferedImage addSmearsPoint(int sizeOfSmear, BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Random r = new Random();
		int numberOfSmears = 200;

		for (int index = 0; index < numberOfSmears; index++) {
			int i = r.nextInt(width - sizeOfSmear);
			int j = r.nextInt(height - sizeOfSmear);
			int x = (int) (r.nextGaussian() * sizeOfSmear);
			int y = (int) (r.nextGaussian() * sizeOfSmear);
			
			int maxDarkness = -1;
			for (int a = 0; a < sizeOfSmear && i + a < width; a++) {
				for (int b = 0; b < sizeOfSmear && j + b < height; b++) {
					if (image.getRGB(i + a, j + b) < maxDarkness) {
						maxDarkness = image.getRGB(i + a, j + b);
					}
				}
			}

			for (int a = 0; a < x && i + a < width; a++) {
				for (int b = 0; b < y && j + b < height; b++) {
					image.setRGB(i + a, j + b, maxDarkness);
				}
			}
		}

		return image;
	}

}
