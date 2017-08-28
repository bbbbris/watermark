package watermark.test;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.PrintWriter;

import watermark.embed.Embed;
import watermark.extract.Extract;
import watermark.test.common.Comparison;
import watermark.test.scan.Scanner;

public class Test {

	public static void main(String[] args) {
		testDetectionRate();
		Toolkit.getDefaultToolkit().beep();
	}

	public static void generalTestRun(PrintWriter writer, int rate) throws IOException {

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
		scanner.scan(WORKING_LOCATION, documentName, stdDev, scanquality, sizeOfSmear, rate);

		int NUMBER_OF_PAGES = embed.getNumberOfPages();

		Extract extract = new Extract(WORKING_LOCATION, documentName, NUMBER_OF_PAGES, comparison);
		extract.extract();

		comparison.compare(writer);
	}

	public static void testDetectionRate() {
		try {
			PrintWriter writer = new PrintWriter("/Users/abrisnagy/Desktop/thesis pictures/rateResults10.txt", "UTF-8");

			int numberOfRuns = 50;
			int[] rates = { 200, 150, 140, 130, 120, 110, 100, 90, 80, 70, 60, 50, 25 };

			for (int i = 0; i < rates.length; i++) {
				System.out.println("%%%%" + rates[i]);
				writer.print(rates[i] + "\n");
				for (int j = 0; j < numberOfRuns; j++) {
					System.out.println("####" + j);
					generalTestRun(writer, rates[i]);
				}
				writer.println("\n");
			}

//			for (int i = 0; i < rates.length; i++) {
//				writer.print(rates[i] + "\n");
//				for (int j = 0; j < errorcounts.length; j++) {
//					System.out.print(errorcounts[j] + " - ");
//					writer.print(errorcounts[j] + " - ");
//				}
//				writer.println("\n");
//			}

			writer.close();
			System.out.println("\n\nFINISHED!!!");
		} catch (IOException e) {
			System.out.println("\n\nNO FILE MAN!!!");
		}
	}

}
