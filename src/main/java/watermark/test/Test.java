package watermark.test;

import watermark.embed.Embed;
import watermark.extract.Extract;
import watermark.test.common.Comparison;
import watermark.test.scan.Scanner;

public class Test {

	public static void main(String[] args) {

		String WORKING_LOCATION = "/Users/abrisnagy/Documents/development/watermark/src/main/resources/test documents/";
		String documentName = "test.pdf";
		int[] watermark = { 1, 0, 1, 0, 1, 1, 0 };
		double difference = 1.4;
		double stdDev = 16777216 * 0.001;
		int scanquality = 100;
		int sizeOfSmear = 40;

		Embed embed = new Embed(WORKING_LOCATION, documentName);
		embed.embed(watermark, difference);
		Comparison comparison = embed.getComparison();

		Scanner scanner = new Scanner();
		scanner.scan(WORKING_LOCATION, documentName, stdDev, scanquality, sizeOfSmear);

		int NUMBER_OF_PAGES = embed.getNumberOfPages();

		Extract extract = new Extract(WORKING_LOCATION, documentName, NUMBER_OF_PAGES, comparison);
		extract.extract();

		comparison.compare();
	}
}
