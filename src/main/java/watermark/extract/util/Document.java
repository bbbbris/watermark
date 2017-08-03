package watermark.extract.util;

import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.adaptiveThreshold;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.bytedeco.javacpp.opencv_imgcodecs.*;
import org.bytedeco.javacpp.*;
import org.xml.sax.*;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

import static org.bytedeco.javacpp.lept.*;
import static org.bytedeco.javacpp.tesseract.*;

import org.bytedeco.javacpp.lept.PIX;
import org.jfree.ui.RefineryUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opencv.core.Mat;

import flanagan.analysis.CurveSmooth;
import watermark.test.common.Comparison;

public class Document {
	private BufferedImage document;
	private String fullPath;
	private List<Line> lines;
	private String WORKING_LOCATION;

	private List<String> locationsOriginal;
	private List<String> locationsWatermarked;
	private int numberOfPages;

	// TODO automate this
	private static final int FILTER_WIDTH = 25;

	public Document() {
	}

	public Document(String fullPath, int numberOfPages) {
		this.numberOfPages = numberOfPages;
		locationsOriginal = new ArrayList<>();
		locationsWatermarked = new ArrayList<>();

		for (int i = 1; i <= numberOfPages; i++) {
			locationsOriginal.add(fullPath.substring(0, fullPath.length() - 4) + "-" + i + ".png");
			// System.out.println(locationsOriginal.get(locationsOriginal.size()
			// - 1));
			locationsWatermarked.add(fullPath.substring(0, fullPath.length() - 4) + "_W-" + i + ".png");
			// System.out.println(locationsWatermarked.get(locationsWatermarked.size()
			// - 1));
		}

		this.WORKING_LOCATION = fullPath.substring(0, fullPath.lastIndexOf("/")) + "/";
	}

	public Document(String fullPath) {

		try {
			this.document = ImageIO.read(new File(fullPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.fullPath = fullPath;
		this.WORKING_LOCATION = fullPath.substring(0, fullPath.lastIndexOf("/")) + "/";
	}

	public List<Integer> extractWatermark() {
		List<Integer> watermark = new ArrayList<>();

		System.load("/usr/local/Cellar/opencv3/3.2.0/share/OpenCV/java/libopencv_java320.dylib");

		// Read image and
		Mat inputDocument = imread(fullPath, IMREAD_GRAYSCALE);
		Mat thresholdedDocument = imread(fullPath, IMREAD_GRAYSCALE);

		// Sharpen image
		sharpenImage(inputDocument);
		Mat sharpenedMap = imread(WORKING_LOCATION + "output/0_sharpImage.png", IMREAD_GRAYSCALE);

		// Transform image to binary with an adaptive threshold
		adaptiveThreshold(sharpenedMap, thresholdedDocument, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 15, 20);

		imwrite(WORKING_LOCATION + "output/0_adaptiveThresholdMean.png", thresholdedDocument);

		this.document = matToBufferedImage(thresholdedDocument);

		int[] verticalProjection = getVerticalProjection();
		CurveSmooth csRows = new CurveSmooth(verticalProjection);
		double[] smoothRows = csRows.savitzkyGolay(FILTER_WIDTH);

		// XYLineChart_AWT chart1 = new XYLineChart_AWT("", "Smooth first",
		// verticalProjection, smoothRows);
		// chart1.pack();
		// RefineryUtilities.centerFrameOnScreen(chart1);
		// chart1.setVisible(true);

		double[] thresholdedRows = thresholdArray(smoothRows);
		ArrayList<Integer> splittingPoints = getSplittingPoints(thresholdedRows);
		List<Line> lines = splitToLines(splittingPoints);
		List<Space> spaces = getSpaces(lines);
		return watermark;
	}

	public double[] extractWatermarkTess(Comparison comparison) {

		// GET LENGTHS FROM PAGES
		List<int[][]> lengthsOriginal = new ArrayList<>();
		List<int[][]> lengthsWatermarked = new ArrayList<>();

		for (int i = 0; i < numberOfPages; i++) {
			lengthsOriginal.add(getLengths(locationsOriginal.get(i)));
			lengthsWatermarked.add(getLengths(locationsWatermarked.get(i)));
			System.out.println("Finished extraction on " + (i + 1) + " pages.");
		}

		// GET DIFFERENCES: WATERMARKED - ORIGINAL
		int[][] lengthsO = new int[numberOfPages][];
		int[][] lengthsW = new int[numberOfPages][];
		List<List<Integer>> differences = getDifferences(lengthsO, lengthsW, lengthsOriginal, lengthsWatermarked);

		// ALIGN
		// List<int[][]> diff = getDiff(lengthsO, lengthsW, lengthsOriginal,
		// lengthsWatermarked);
		// List<List<Integer>> alignedDifferences = align(diff, differences);

		// GET WEIGHTS
		List<Double> weights = getWeights(lengthsOriginal, lengthsWatermarked, lengthsO, lengthsW);

		// GET PROBABILITIES
		int maxLength = 0;
		for (int i = 0; i < differences.size(); i++) {
			maxLength = Math.max(maxLength, differences.get(i).size());
		}

		double[] probabilities = getProbabilities(differences, weights, maxLength);

		double[] watermark = decode(probabilities, comparison);

		return watermark;
	}

	private List<int[][]> getDiff(int[][] lengthsO, int[][] lengthsW, List<int[][]> lengthsOriginal,
			List<int[][]> lengthsWatermarked) {
		List<int[][]> differences = new ArrayList<>();
		for (int i = 0; i < numberOfPages; i++) {

			lengthsO[i] = new int[lengthsOriginal.get(i).length];
			lengthsW[i] = new int[lengthsWatermarked.get(i).length];
			int numberOfLines = Math.min(lengthsOriginal.get(i).length, lengthsWatermarked.get(i).length);
			int[][] diffInPage = new int[numberOfLines][];

			for (int j = 0; j < numberOfLines; j++) {
				// int numberOfSpaces =
				// Math.min(lengthsOriginal.get(i)[j].length,
				// lengthsWatermarked.get(i)[j].length);
				int numberOfSpaces = lengthsOriginal.get(i)[j].length;
				int[] diffInLine = new int[numberOfSpaces];

				for (int k = 0; k < numberOfSpaces && k < lengthsWatermarked.get(i)[j].length; k++) {
					diffInLine[k] = Math.max(0, lengthsWatermarked.get(i)[j][k] - lengthsOriginal.get(i)[j][k]);
				}

				diffInPage[j] = diffInLine;
			}
			differences.add(diffInPage);
		}

		return differences;
	}

	private List<List<Integer>> align(List<int[][]> differences, List<List<Integer>> flatDifferences) {
		List<List<Integer>> consensusDifferences = new ArrayList<>();
		try {
			List<List<Integer>> sequences = new ArrayList<>();
			PrintWriter writer = new PrintWriter("/Users/abrisnagy/Documents/development/"
					+ "watermark/src/main/resources/test documents/sequences.txt", "UTF-8");
			String sequenceInPage;

			// multialign
			for (int i = 0; i < differences.size(); i++) {
				sequenceInPage = "";
				writer.println(">PAGE " + i);
				for (int j = 0; j < differences.get(i).length; j++) {
					int maxSpaceLengthInLine = 0;
					for (int k = 0; k < differences.get(i)[j].length; k++) {
						if (maxSpaceLengthInLine < differences.get(i)[j][k]) {
							maxSpaceLengthInLine = differences.get(i)[j][k];
						}
					}

					for (int k = 0; k < differences.get(i)[j].length; k++) {
						if (maxSpaceLengthInLine * 0.5 < differences.get(i)[j][k]) {
							// this is a long space (1)
							sequenceInPage += "A";
							// System.out.print(differences.get(i)[j][k] + "A\t");
						} else {
							// this is a short space (0)
							sequenceInPage += "T";
							// System.out.print(differences.get(i)[j][k] + "T\t");
						}
					}
					// sequenceInPage += "\t";
				}
				writer.print(sequenceInPage + "\n");
				// System.out.println();
			}
			writer.close();

			MatlabEngine eng = MatlabEngine.startMatlab();
			eng.feval("addpath", "/Users/abrisnagy/Documents/development/watermark/src/main/resources/test documents/"
					.toCharArray());

			double[] dummy = eng.feval("align");

			// match aligned to original
			File file = new File(
					"/Users/abrisnagy/Documents/development/watermark/src/main/resources/test documents/aligned.txt");
			BufferedReader reader = new BufferedReader(new java.io.FileReader(file));

			String line;
			List<String> pages = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				pages.add(line);
//				System.out.println(line);
			}
			
			//align differences according to multialign
			System.out.println();
			List<List<Integer>> alignedDifferences = new ArrayList<>();

			for (int i = 0; i < pages.size() - 1; i++) {
				List<Integer> alignedLine = new ArrayList<>();
				int k = 0;
				for (int j = 0; j < pages.get(i).length() && j < flatDifferences.get(i).size(); j++) {
					if (pages.get(i).charAt(j) == '-') {
						alignedLine.add(-1);
					} else {
						alignedLine.add(flatDifferences.get(i).get(k));
						k++;
					}
				}
				alignedDifferences.add(alignedLine);
			}

			// prepare for merge according to consensus sequence
//			System.out.println();
			String consensusSequence = pages.get(pages.size() - 1);

			for (int i = 0; i < alignedDifferences.size(); i++) {
				List<Integer> alignedLineConsensus = new ArrayList<>();
				for (int j = 0; j < consensusSequence.length(); j++) {
					if (consensusSequence.charAt(j) != '-') {
						if(j < alignedDifferences.get(i).size()){
							alignedLineConsensus.add(alignedDifferences.get(i).get(j));
						} else {
							alignedLineConsensus.add(-1);
						}
					}
				}
				consensusDifferences.add(alignedLineConsensus);
			}
			
			// print
			// for(int i = 0; i < alignedDifferences.size(); i++){
			// for (int j = 0; j < alignedDifferences.get(i).size(); j++) {
			// System.out.print(alignedDifferences.get(i).get(j) + "\t");
			// }
			// System.out.println();
			// }
			// System.out.println();
			// for(int i = 0; i < consensusDifferences.size(); i++){
			// for (int j = 0; j < consensusDifferences.get(i).size(); j++) {
			// System.out.print(consensusDifferences.get(i).get(j) + "\t");
			// }
			// System.out.println();
			// }
			for (int i = 0; i < consensusSequence.length(); i++) {
				System.out.print(consensusSequence.charAt(i));
			}
			System.out.println();

		} catch (IOException | IllegalArgumentException | IllegalStateException | RejectedExecutionException
				| InterruptedException | ExecutionException e) {
		}
		return consensusDifferences;
	}

	private double[] decode(double[] probabilities, Comparison comparison) {

		try {
			MatlabEngine eng = MatlabEngine.startMatlab();
			eng.feval("addpath", "/Users/abrisnagy/Documents/MATLAB/LDPC_sim_v1/".toCharArray());
			eng.feval("addpath", "/Users/abrisnagy/Documents/MATLAB/LDPC_sim_v1/codes".toCharArray());

			probabilities = java.util.Arrays.copyOfRange(probabilities, 0, 288);

			comparison.setDetectedLDPC(probabilities.clone());

			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = probabilities[i] * (-2) + 1;
			}

			// for (double e : probabilities) {
			// System.out.print(e + " ");
			// }
			// System.out.println();

			double[] LDPCdecoded = eng.feval("decode", probabilities);

			comparison.setDetecetdHadamard(LDPCdecoded);

			// for (double e : LDPCdecoded) {
			// System.out.print(e + " ");
			// }
			// System.out.println();

			String LDPCDecodedString = "";
			for (int i = 0; i < 128; i++) {
				LDPCDecodedString += (int) LDPCdecoded[i];
			}
			Hadamard hadamard = new Hadamard(7);
			String hadamardDecoded = hadamard.decode(LDPCDecodedString);
			System.out.println(hadamardDecoded);

			comparison.setDetectedID(hadamardDecoded);

			eng.close();

			return LDPCdecoded;
		} catch (IllegalArgumentException | IllegalStateException | InterruptedException | RejectedExecutionException
				| ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

	private double[] getProbabilities(List<List<Integer>> flatDifferences, List<Double> weights, int maxLength) {
		double sum;
		int count;
		double[] averagesAcrossPages = new double[maxLength];
		for (int j = 0; j < maxLength; j++) {
			sum = 0;
			count = 0;
			for (int i = 0; i < flatDifferences.size(); i++) {
				if (j < flatDifferences.get(i).size()) {
					// System.out.print(flatDifferences.get(i).get(j) + " " +
					// weights.get(i) + "\t");
					if(flatDifferences.get(i).get(j) != -1){
						sum += flatDifferences.get(i).get(j) * weights.get(i);
						count++;
					}
				}
			}
			averagesAcrossPages[j] = sum / (double) count;
			// System.out.println("\t" + averagesAcrossPages[j]);

		}
		// System.out.println();

		// NORMALISE
		averagesAcrossPages = normaliseAverages(averagesAcrossPages);

		// System.out.println(averagesAcrossPages.length);
		// for (int i = 0; i < averagesAcrossPages.length; i++) {
		// System.out.print(/* i + " " + */ averagesAcrossPages[i] + "\t");
		// }
		// System.out.println();

		return averagesAcrossPages;

	}

	private double[] normaliseAverages(double[] averagesAcrossPages) {
		double max = 0;
		for (int i = 0; i < averagesAcrossPages.length; i++) {
			max = Math.max(max, averagesAcrossPages[i]);
		}
		for (int i = 0; i < averagesAcrossPages.length; i++) {
			averagesAcrossPages[i] = averagesAcrossPages[i] / max;
		}
		return averagesAcrossPages;
	}

	private List<List<Integer>> getDifferences(int[][] lengthsO, int[][] lengthsW, List<int[][]> lengthsOriginal,
			List<int[][]> lengthsWatermarked) {
		List<int[][]> differences = new ArrayList<>();
		for (int i = 0; i < numberOfPages; i++) {

			lengthsO[i] = new int[lengthsOriginal.get(i).length];
			lengthsW[i] = new int[lengthsWatermarked.get(i).length];
			int numberOfLines = Math.min(lengthsOriginal.get(i).length, lengthsWatermarked.get(i).length);
			int[][] diffInPage = new int[numberOfLines][];

			for (int j = 0; j < numberOfLines; j++) {
				// int numberOfSpaces =
				// Math.min(lengthsOriginal.get(i)[j].length,
				// lengthsWatermarked.get(i)[j].length);
				int numberOfSpaces = lengthsOriginal.get(i)[j].length;
				int[] diffInLine = new int[numberOfSpaces];

				for (int k = 0; k < numberOfSpaces && k < lengthsWatermarked.get(i)[j].length; k++) {
					diffInLine[k] = Math.max(0, lengthsWatermarked.get(i)[j][k] - lengthsOriginal.get(i)[j][k]);
				}

				diffInPage[j] = diffInLine;
			}
			differences.add(diffInPage);
		}

		// for (int i = 0; i < differences.size(); i++) {
		// int count = 0;
		// for (int j = 0; j < differences.get(i).length; j++) {
		// for (int k = 0; k < differences.get(i)[j].length; k++) {
		// System.out.print(differences.get(i)[j][k] + "\t");
		// count++;
		// }
		// System.out.println("c" + count);
		// }
		// System.out.println();
		// }
		// System.out.println();

		// FLATTEN DIFFERENCES
		List<List<Integer>> flatDifferences = flattenDifferences(differences);

		return flatDifferences;
	}

	private List<List<Integer>> flattenDifferences(List<int[][]> differences) {
		List<List<Integer>> flatDifferences = new ArrayList<>();
		for (int i = 0; i < differences.size(); i++) {
			List<Integer> diff = new ArrayList<>();
			// System.out.println("length1: " + differences.get(i).length);
			for (int j = 0; j < differences.get(i).length; j++) {
				// System.out.println("length2: " +
				// differences.get(i)[j].length);
				for (int k = 0; k < differences.get(i)[j].length; k++) {
					diff.add(differences.get(i)[j][k]);
				}
			}
			// System.out.println("diff: " + diff.size());
			flatDifferences.add(diff);
		}
		
		// for(int i= 0; i<flatDifferences.size(); i++){
		// for(int j = 0; j< flatDifferences.get(i).size(); j++){
		// System.out.print(flatDifferences.get(i).get(j) + "\t");
		// }
		// System.out.println();
		// }
		// System.out.println();
		
		return flatDifferences;
	}

	private List<Double> getWeights(List<int[][]> lengthsOriginal, List<int[][]> lengthsWatermarked, int[][] lengthsO,
			int[][] lengthsW) {
		List<Double> weights = new ArrayList<>();
		for (int i = 0; i < numberOfPages; i++) {

			int numberOfLines = Math.min(lengthsOriginal.get(i).length, lengthsWatermarked.get(i).length);
			// Double[] weightsOnPage = new Double[numberOfLines];

			Double minWeight = (double) 1;

			for (int j = 0; j < numberOfLines; j++) {

				int lengthO = lengthsOriginal.get(i)[j].length;
				int lengthW = lengthsWatermarked.get(i)[j].length;

				if (lengthO != 0) {

					lengthsO[i][j] = lengthO;
					lengthsW[i][j] = lengthW;

					// System.out.println(lengthsO[i][j] + " O\t" +
					// lengthsW[i][j] + " W");

					minWeight = Math.min(minWeight,
							Math.max(0, ((double) lengthO - Math.abs(lengthW - lengthO) * 10) / (double) lengthO));
					// weightsOnPage[j] = minWeight;
				} else {
					// weightsOnPage[j] = (double) 0;
				}
			}

			int sumO = 0;
			for (int a = 0; a < lengthsO[i].length; a++) {
				sumO += lengthsO[i][a];
			}
			int sumW = 0;
			for (int a = 0; a < lengthsW[i].length; a++) {
				sumW += lengthsW[i][a];
			}
			double pageWeight = Math.max(0, ((double) sumO - Math.abs(sumW - sumO) * 10) / (double) sumO);

			weights.add(Math.min(pageWeight, minWeight));
		}

		// for (int i = 0; i < weights.size(); i++) {
		// System.out.print(weights.get(i) + " ");
		// }
		// System.out.println();

		return weights;
	}

	private int[][] getLengths(String location) {

		String hocr = getHOCRFromImage(location);

		// GET WORDS FROM HOCR
		List<List<Word>> wordsPerLine = new ArrayList<>();

		org.jsoup.nodes.Document doc = Jsoup.parse(hocr);
		Elements lines = doc.getElementsByClass("ocr_line");
		for (Element line : lines) {
			List<Word> wordsInLine = new ArrayList<>();
			String lineString = line.attr("title");

			Elements words = line.getElementsByClass("ocrx_word");

			for (Element word : words) {
				String wordString = word.attr("title");

				String[] attr = wordString.split(" ");

				int topRight_X = Integer.parseInt(attr[1]);
				int topRight_Y = Integer.parseInt(attr[2]);
				int bottomLeft_X = Integer.parseInt(attr[3]);
				int bottomLeft_Y = Integer.parseInt(attr[4].substring(0, attr[4].length() - 1));

				wordsInLine.add(new Word(topRight_X, topRight_Y, bottomLeft_X, bottomLeft_Y));

				// System.out.println(
				// "(" + topRight_X + "," + topRight_Y + "), (" + bottomLeft_X +
				// "," + bottomLeft_Y + ")");
			}
			wordsPerLine.add(wordsInLine);
		}

		// for(int i = 0; i<wordsPerLine.size(); i++){
		// for(int j= 0; j<wordsPerLine.get(i).size(); j++){
		// System.out.print(wordsPerLine.get(i).get(j).toString() + "\t");
		// }
		// System.out.println();
		// }

		// GET SPACES FROM WORDS
		List<List<Space>> spacesPerLine = new ArrayList<>();

		for (int i = 0; i < wordsPerLine.size(); i++) {
			List<Space> spacesInLine = new ArrayList<>();
			for (int j = 0; j < wordsPerLine.get(i).size() - 1; j++) {
				int start = wordsPerLine.get(i).get(j).getBottomLeft_X();
				int end = wordsPerLine.get(i).get(j + 1).getTopRight_X();
				spacesInLine.add(new Space(i, start, end));
			}
			spacesPerLine.add(spacesInLine);
		}

		// GET LENGTHS OF SPACES
		int[][] lengths = new int[spacesPerLine.size()][];
		for (int i = 0; i < lengths.length; i++) {
			lengths[i] = new int[spacesPerLine.get(i).size()];
			for (int j = 0; j < spacesPerLine.get(i).size(); j++) {
				lengths[i][j] = spacesPerLine.get(i).get(j).getLength();
			}
		}

		// for (int i = 0; i < lengths.length; i++) {
		// for (int j = 0; j < lengths[i].length; j++) {
		// System.out.print(lengths[i][j] + " ");
		// }
		// System.out.println();
		// }
		// System.out.println();

		return lengths;
	}

	private IplImage cleanImageSmoothingForOCR(IplImage srcImage) {
		IplImage destImage = opencv_core.cvCreateImage(opencv_core.cvGetSize(srcImage), opencv_core.IPL_DEPTH_8U, 1);
		opencv_imgproc.cvCvtColor(srcImage, destImage, opencv_imgproc.CV_BGR2GRAY);
		// opencv_imgproc.cvSmooth(destImage, destImage,
		// opencv_imgproc.CV_MEDIAN, 3, 0, 0, 0);
		// opencv_imgproc.cvThreshold(destImage, destImage, 0, 255,
		// opencv_imgproc.THRESH_OTSU);
		opencv_imgproc.cvAdaptiveThreshold(destImage, destImage, 255, opencv_imgproc.CV_ADAPTIVE_THRESH_GAUSSIAN_C,
				THRESH_BINARY, 15, 40);
		return destImage;
	}

	private String getHOCRFromImage(final String pathToReceiptImageFile) {
		try {
			final URL tessDataResource = getClass().getResource("/");
			File tessFolder;
			tessFolder = new File(tessDataResource.toURI());

			final String tessFolderPath = tessFolder.getAbsolutePath();

			BytePointer outText;
			TessBaseAPI api = new TessBaseAPI();
			api.SetDebugVariable("tessedit_char_whitelist",
					"abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789");

			if (api.Init(tessFolderPath, "eng") != 0) {
				System.err.println("Could not initialise tesseract.");
			}

			PIX image = pixRead(pathToReceiptImageFile);
			api.SetImage(image);

			// System.out.println(api.GetUTF8Text().getString());

			outText = api.GetHOCRText(1);

			String string = outText.getString();

			// System.out.println(string);

			api.End();
			outText.deallocate();
			pixDestroy(image);
			return string;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getTextFromReceiptImage(final String receiptFileImagePath) {
		final File receiptImageFile = new File(receiptFileImagePath);
		final String receiptImagePathFile = receiptImageFile.getAbsolutePath();

		IplImage scannedImage = opencv_imgcodecs.cvLoadImage(receiptImagePathFile);
		scannedImage = cleanImageSmoothingForOCR(scannedImage);

		final File cleanedImageFile = new File(receiptFileImagePath);
		final String cleanedScanPathFile = cleanedImageFile.getAbsolutePath();
		opencv_imgcodecs.cvSaveImage(cleanedScanPathFile, scannedImage);

		opencv_core.cvReleaseImage(scannedImage);
		scannedImage = null;

		return getHOCRFromImage(cleanedScanPathFile);
	}

	private List<Space> getSpaces(List<Line> lines) {
		List<Space> spaces = new ArrayList<>();

		int numberOfLines = lines.size();
		for (int i = 0; i < numberOfLines; i++) {
			spaces.addAll(lines.get(i).getSpaces());
		}

		return null;
	}

	private List<Line> splitToLines(List<Integer> splittingPoints) {
		lines = new ArrayList<>();
		for (int i = 0; i < splittingPoints.size() - 1; i++) {
			try {
				ImageIO.write(
						this.document.getSubimage(0, splittingPoints.get(i), this.document.getWidth(),
								splittingPoints.get(i + 1) - splittingPoints.get(i)),
						"png", new File(WORKING_LOCATION + "output\\line_" + i + ".png"));

				lines.add(new Line(splittingPoints.get(i), splittingPoints.get(i + 1) - splittingPoints.get(i),
						document.getSubimage(0, splittingPoints.get(i), this.document.getWidth(),
								splittingPoints.get(i + 1) - splittingPoints.get(i))));

			} catch (IOException ex) {
				Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return lines;
	}

	private ArrayList<Integer> getSplittingPoints(double[] thresholdedArray) {
		ArrayList<Integer> halfPoints = new ArrayList<Integer>();
		ArrayList<Integer> splittingPoints = new ArrayList<Integer>();
		int lastMarker = 0;

		for (int i = 0; i < thresholdedArray.length - 1; i++) {
			if (thresholdedArray[i] != thresholdedArray[i + 1]) {
				halfPoints.add((lastMarker + i) / 2);
				lastMarker = i;
			}
		}

		splittingPoints.add(0);
		for (int i = 2; i < halfPoints.size(); i += 2) {
			splittingPoints.add(halfPoints.get(i));
		}
		splittingPoints.add(thresholdedArray.length);

		return splittingPoints;
	}

	private double[] thresholdArray(double[] data) {
		double min = data[0];
		double max = data[0];
		int length = data.length;
		double[] thresholdedArray = new double[length];

		for (int i = 0; i < length; i++) {
			if (data[i] < min) {
				min = data[i];
			}
			if (data[i] > max) {
				max = data[i];
			}
		}
		double threshold = getThreshold(data, min, max);

		for (int i = 0; i < length; i++) {
			if (data[i] > threshold) {
				thresholdedArray[i] = max;
			} else {
				thresholdedArray[i] = min;
			}
		}

		return thresholdedArray;
	}

	private double getThreshold(double[] data, double min, double max) {
		int numberOfBins = 10;
		int filterWidth = 10;
		double binSize = (max - min) / numberOfBins;
		int[] histogram = getHistogram(data, numberOfBins + 1);

		CurveSmooth csHistogram = new CurveSmooth(histogram);
		double[] smoothHistogram = csHistogram.savitzkyGolay(filterWidth);

		return ((findFirstLocalMin(smoothHistogram) * binSize) - min) / 2;
	}

	private double findFirstLocalMin(double[] array) {
		double firstElement = array[0];
		double localMin = array[0];
		int length = array.length;

		for (int i = 0; i < length; i++) {
			if (firstElement < array[i]) {
				return (i - 1);
			}
			firstElement = array[i];
		}
		return localMin;
	}

	private int[] getHistogram(double[] array, int numberOfBins) {
		double min = array[0];
		double max = array[0];
		int length = array.length;
		for (int i = 0; i < length; i++) {
			if (array[i] < min) {
				min = array[i];
			}
			if (array[i] > max) {
				max = array[i];
			}
		}

		double binSize = (max - min) / numberOfBins;
		int[] histogram = new int[numberOfBins + 1];

		int count = 0;
		for (int i = 0; i < length; i++) {
			int value = (int) ((array[i] - min) / binSize);
			if (value < histogram.length && histogram[value] != 0) {
				count = histogram[value];
				count++;
			} else {
				count = 1;
			}
			if (value < histogram.length && count != 0) {
				histogram[value] = count;
			}
		}

		return histogram;
	}

	private int[] getVerticalProjection() {
		int documentHeight = document.getHeight();
		int documentWidth = document.getWidth();
		int[] summedRows = new int[documentHeight];

		for (int j = 0; j < documentHeight; j++) {
			for (int i = 0; i < documentWidth; i++) {
				if (document.getRGB(i, j) == -1) {
					summedRows[j] += 0;
				} else {
					summedRows[j] += 1;
				}
			}
		}

		return summedRows;
	}

	public BufferedImage sharpenImage(Mat blurredMat) {

		// GAUSSIAN UNSHARP MASKING (this is better)
		float[] kernel = { 1, 4, 6, 4, 1, 4, 16, 24, 16, 4, 6, 24, -476, 24, 6, 4, 16, 24, 16, 4, 1, 4, 6, 4, 1 };

		kernel = normalizeKernel(kernel);

		Kernel k = new Kernel(5, 5, kernel);
		ConvolveOp convolver = new java.awt.image.ConvolveOp(k, java.awt.image.ConvolveOp.EDGE_NO_OP, null);

		BufferedImage blurredImage = matToBufferedImage(blurredMat);

		BufferedImage sharpImage = new java.awt.image.BufferedImage(blurredImage.getWidth(), blurredImage.getHeight(),
				blurredImage.getType());
		Graphics g = sharpImage.createGraphics();
		g.drawImage(blurredImage, 0, 0, null);
		g.dispose();
		blurredImage = convolver.filter(sharpImage, blurredImage);

		try {
			ImageIO.write(sharpImage, "png", new File(WORKING_LOCATION + "\\output\\0_blurredImage.png"));
			ImageIO.write(blurredImage, "png", new File(WORKING_LOCATION + "\\output\\0_sharpImage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sharpImage;
	}

	public float[] normalizeKernel(float[] kernel) {

		int n = 0;
		for (int i = 0; i < kernel.length; i++) {
			n += kernel[i];
		}
		for (int i = 0; i < kernel.length; i++) {
			kernel[i] /= n;
		}

		return kernel;
	}

	public BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage bimg = new BufferedImage(cols, rows, type);
		bimg.getRaster().setDataElements(0, 0, cols, rows, data);
		return bimg;
	}

	public BufferedImage getDocument() {
		return document;
	}

	public void setDocument(BufferedImage document) {
		this.document = document;
	}

	public String getLocation() {
		return fullPath;
	}

	public void setLocation(String fullPath) {
		this.fullPath = fullPath;
	}

	public List<Line> getLines() {
		return lines;
	}

	public void setLines(List<Line> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		return "Document [document=" + document + ", fullPath=" + fullPath + ", WORKING_LOCATION=" + WORKING_LOCATION
				+ "]";
	}
}
