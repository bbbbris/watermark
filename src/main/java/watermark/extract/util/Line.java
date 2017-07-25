package watermark.extract.util;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jfree.ui.RefineryUtilities;

import flanagan.analysis.CurveSmooth;

public class Line {
	private int startYCoordinate;
	private int endYCoordinate;
	private BufferedImage line;

	// TODO find filter width automatically
	private static final int FILTER_WIDTH = 10;
	private static final int NUMBER_OF_BINS_FOR_THRESHOLDING_NOISE = 10;
	private static final double NUMBER_OF_BINS_FOR_THRESHOLDING_BURSTS = 7;
	private static final double ERROR_THRESHOLD = 0.1;

	private static final int RED = 16711680;
	private static final int GREEN = 65280;
	private static final int BLUE = 25855;
	private static final int BLACK = -16777216;
	private static final int WHITE = 16777215;

	public Line() {
	}

	public Line(int startYCoordinate, int endYCoordinate, BufferedImage line) {
		this.startYCoordinate = startYCoordinate;
		this.endYCoordinate = endYCoordinate;
		this.line = line;
	}

	public List<Space> getSpaces() {
		List<Space> spaces = new ArrayList<>();

		int[] horizontalProjection = sumRows(line.getHeight(), line.getWidth());

		List<Space> bursts = getBursts(horizontalProjection);
		bursts = cutMargins(bursts);
		bursts = thresHoldBursts(bursts);

		compareBursts(bursts);

		// FOR the TEST
		// double[] horizontalProjectionZERO = new
		// double[horizontalProjection.length];
		// for (int i = 0; i < horizontalProjection.length; i++) {
		// horizontalProjectionZERO[i] = 0;
		// }

		// there are no outliers because the lines are thin THERE COULD BE
		// horizontalProjection = eliminateOutliers(horizontalProjection);

		// CurveSmooth cs = new CurveSmooth(horizontalProjection);
		// double[] smoothProjection = cs.savitzkyGolay(FILTER_WIDTH);

		// XYLineChart_AWT chart1 = new XYLineChart_AWT("", "Smooth first",
		// horizontalProjection,
		// horizontalProjectionZERO);
		// chart1.pack();
		// RefineryUtilities.centerFrameOnScreen(chart1);
		// chart1.setVisible(true);

		return bursts;
	}

	private void compareBursts(List<Space> bursts) {
		double mean = getMeanLength(bursts);
		List<Integer> watermarkInLine = new ArrayList<>();

		int numberOfBursts = bursts.size();
		for (int i = 0; i < numberOfBursts; i++) {
			if (bursts.get(i).getLength() > mean * (1 + ERROR_THRESHOLD)) {
				watermarkInLine.add(1);
				bursts.get(i).setProbability(1);
			} else if (bursts.get(i).getLength() < mean * (1 - ERROR_THRESHOLD)) {
				watermarkInLine.add(-1);
				bursts.get(i).setProbability(-1);
			} else {
				watermarkInLine.add(0);
				bursts.get(i).setProbability(0);
			}
		}
	}

	private double getMeanLength(List<Space> bursts) {
		int numberOfBursts = bursts.size();
		int sum = 0;

		for (int i = 0; i < numberOfBursts; i++) {
			sum += bursts.get(i).getLength();
		}

		return sum / numberOfBursts;
	}

	public BufferedImage getSpacesToPrintOnProjection() {
		int lineHeight = line.getHeight();
		int lineWidth = line.getWidth();

		BufferedImage lineWithSpaces = new BufferedImage(lineWidth, lineHeight, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < lineHeight; j++) {
				lineWithSpaces.setRGB(i, j, -1);
			}
		}

		int[] horizontalProjection = sumRows(lineHeight, lineWidth);

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < horizontalProjection[i]; j++) {
				lineWithSpaces.setRGB(i, (lineHeight - horizontalProjection[i]) + j, 1);
			}
		}

		List<Space> bursts = getBursts(horizontalProjection);
		bursts = cutMargins(bursts);
		// bursts = eliminateOutliers(bursts);
		bursts = thresHoldBursts(bursts);

		int numberOfBursts = bursts.size();
		Space burstTemp = new Space();
		int burstLength = 0;

		for (int i = 0; i < numberOfBursts; i++) {
			burstTemp = bursts.get(i);
			burstLength = burstTemp.getEndPosition() - burstTemp.getStartPosition();

			for (int j = 0; burstTemp.getStartPosition() < lineWidth && j < burstLength; j++)
				lineWithSpaces.setRGB(burstTemp.getStartPosition() + j, lineHeight - 1, 511);
		}

		return lineWithSpaces;
	}

	public BufferedImage getSpacesToPrintOnOriginal() {
		int lineHeight = line.getHeight();
		int lineWidth = line.getWidth();

		BufferedImage lineWithSpaces = new BufferedImage(lineWidth, lineHeight, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < lineHeight; j++) {
				// System.out.print(line.getRGB(i, j));
				if (line.getRGB(i, j) == BLACK) {
					lineWithSpaces.setRGB(i, j, BLACK);
				} else {
					lineWithSpaces.setRGB(i, j, WHITE);
				}
			}
			// System.out.println("lineEnd");
		}

		int[] horizontalProjection = sumRows(lineHeight, lineWidth);

		List<Space> bursts = getBursts(horizontalProjection);
		bursts = cutMargins(bursts);
		// bursts = eliminateOutliers(bursts);
		bursts = thresHoldBursts(bursts);

		int numberOfBursts = bursts.size();
		Space burstTemp = new Space();
		int burstLength = 0;

		compareBursts(bursts);

		for (int i = 0; i < numberOfBursts; i++) {
			burstTemp = bursts.get(i);
			burstLength = burstTemp.getEndPosition() - burstTemp.getStartPosition();

			for (int j = 0; burstTemp.getStartPosition() < lineWidth && j < burstLength; j++) {
				if (burstTemp.getProbability() < 0) {
					lineWithSpaces.setRGB(burstTemp.getStartPosition() + j, lineHeight - 1, BLUE);
				} else if (burstTemp.getProbability() > 0) {
					lineWithSpaces.setRGB(burstTemp.getStartPosition() + j, lineHeight - 1, RED);
				} else {
					lineWithSpaces.setRGB(burstTemp.getStartPosition() + j, lineHeight - 1, GREEN);
				}
			}
		}

		return lineWithSpaces;
	}

	private List<Space> cutMargins(List<Space> bursts) {
		List<Space> withoutMargins = new ArrayList<>();
		int length = bursts.size();
		Space tempSpace = new Space();

		for (int i = 1; i < length - 1; i++) {
			tempSpace = new Space(1, bursts.get(i).getStartPosition(), bursts.get(i).getEndPosition());
			withoutMargins.add(tempSpace);
		}
		return withoutMargins;
	}

	private List<Space> getBursts(int[] horizontalProjection) {
		List<Space> bursts = new ArrayList<>();
		int arrayLength = horizontalProjection.length;

		int startPosotion = 0;
		int length = 0;

		for (int i = 0; i < arrayLength; i++) {
			startPosotion = i;
			while (i < arrayLength && horizontalProjection[i] == 0) {
				length++;
				i++;
			}
			if (length > 0) {
				bursts.add(new Space(1, startPosotion, startPosotion + length));
			}
			length = 0;
		}

		return bursts;
	}

	private List<Space> thresHoldBursts(List<Space> bursts) {
		List<Space> thresholdedBursts = new ArrayList<Space>();
		// TODO continue here, needs thresholding for the levels too
		// bursts = eliminateOutliers(bursts);
		double binSize = calculateBinSize(bursts, NUMBER_OF_BINS_FOR_THRESHOLDING_BURSTS);

		for (int i = 0; i < bursts.size(); i++) {
			if (bursts.get(i).getLength() > binSize * 4) {
				thresholdedBursts.add(bursts.get(i));
			}
		}

		return thresholdedBursts;
	}

	private double calculateBinSize(List<Space> bursts, double numberOfBins) {
		if (bursts.size() > 0) {
			int min = bursts.get(0).getLength();
			int max = bursts.get(0).getLength();
			int numberOfBursts = bursts.size();
			for (int i = 0; i < numberOfBursts; i++) {
				if (bursts.get(i).getLength() < min) {
					min = bursts.get(i).getLength();
				} else if (bursts.get(i).getLength() > max) {
					max = bursts.get(i).getLength();
				}
			}
			return ((max - min) / numberOfBins);
		}
		return 0;
	}

	private List<Space> eliminateOutliers(List<Space> list) {
		int listLength = list.size();

		Collections.sort(list, new SpaceComparator());

		double median = median(list);
		int q1 = (int) median(list.subList(0, listLength / 2));
		int q3 = (int) median(list.subList(listLength / 2, listLength)) + 1;
		double allowedDistance = (q3 - q1);
		List<Space> trimmed = trimList(list, median - allowedDistance, (median + allowedDistance));
		return trimmed;
	}

	private List<Space> trimList(List<Space> list, double lower, double upper) {
		int listLength = list.size();
		List<Space> trimmed = new ArrayList<>();

		for (int i = 0; i < listLength; i++) {
			if (/* list.get(i).getLength() > lower && */ list.get(i).getLength() < upper) {
				trimmed.add(list.get(i));
			}
		}

		return trimmed;
	}

	private double median(List<Space> list) {
		int middle = list.size() / 2;
		if (list.size() % 2 == 1) {
			return list.get(middle).getLength();
		} else {
			return (list.get(middle - 1).getLength() + list.get(middle).getLength()) / 2.0;
		}
	}

	private int[] eliminateOutliers(int[] array) {
		int arrayLength = array.length;
		int[] sortedArray = new int[arrayLength];
		System.arraycopy(array, 0, sortedArray, 0, arrayLength);
		Arrays.sort(sortedArray);

		double median = median(sortedArray);
		int q1 = (int) median(Arrays.copyOfRange(sortedArray, 0, sortedArray.length / 2));
		int q3 = (int) median(Arrays.copyOfRange(sortedArray, sortedArray.length / 2, sortedArray.length)) + 1;
		double allowedDistance = (q3 - q1);
		int[] trimmed = trimArray(array, median - allowedDistance, median + allowedDistance);
		return trimmed;
	}

	private double median(int[] m) {
		int middle = m.length / 2;
		if (m.length % 2 == 1) {
			return m[middle];
		} else {
			return (m[middle - 1] + m[middle]) / 2.0;
		}
	}

	private int[] trimArray(int[] array, double lower, double upper) {
		int arrayLength = array.length;
		int[] trimmed = new int[arrayLength];
		for (int i = 0; i < arrayLength; i++) {
			if (array[i] < lower) {
				trimmed[i] = (int) lower;
			} else if (array[i] > upper) {
				trimmed[i] = ((int) upper) + 1;
			} else {
				trimmed[i] = array[i];
			}
		}
		return trimmed;
	}

	public BufferedImage getSmoothProjection() {
		int lineHeight = line.getHeight();
		int lineWidth = line.getWidth();

		int[] horizontalProjection = sumRows(lineHeight, lineWidth);

		CurveSmooth cs = new CurveSmooth(horizontalProjection);
		double[] smoothProjection = cs.savitzkyGolay(FILTER_WIDTH);

		BufferedImage projection = new BufferedImage(lineWidth, lineHeight, BufferedImage.TYPE_BYTE_BINARY);

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < lineHeight; j++) {
				projection.setRGB(i, j, -1);
			}
		}

		int[] inBoundsSmooth = new int[smoothProjection.length];
		for (int i = 0; i < smoothProjection.length; i++) {
			if (smoothProjection[i] < 0) {
				inBoundsSmooth[i] = 0;
			} else if (smoothProjection[i] > lineHeight - 1) {
				inBoundsSmooth[i] = lineHeight - 1;
			} else {
				inBoundsSmooth[i] = (int) smoothProjection[i];
			}
		}

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < inBoundsSmooth[i]; j++) {
				projection.setRGB(i, lineHeight - inBoundsSmooth[i] + j, 1);
			}
		}

		return projection;
	}

	public BufferedImage getHorizontalProjection() {
		int lineHeight = line.getHeight();
		int lineWidth = line.getWidth();

		int[] horizontalProjection = sumRows(lineHeight, lineWidth);

		BufferedImage projection = new BufferedImage(lineWidth, lineHeight, BufferedImage.TYPE_BYTE_BINARY);

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < lineHeight; j++) {
				projection.setRGB(i, j, -1);
			}
		}

		for (int i = 0; i < lineWidth; i++) {
			for (int j = 0; j < horizontalProjection[i]; j++) {
				projection.setRGB(i, (lineHeight - horizontalProjection[i]) + j, 1);
			}
		}

		return projection;
	}

	private int[] sumRows(int lineHeight, int lineWidth) {
		int[] horizontalProjection = new int[lineWidth];
		for (int j = 0; j < lineHeight; j++) {
			for (int i = 0; i < lineWidth; i++) {
				if (line.getRGB(i, j) == -1) {
					horizontalProjection[i] += 0;
				} else {
					horizontalProjection[i] += 1;
				}
			}
		}
		return horizontalProjection;
	}

	public int getStartYCoordinate() {
		return startYCoordinate;
	}

	public void setStartYCoordinate(int startYCoordinate) {
		this.startYCoordinate = startYCoordinate;
	}

	public int getEndYCoordinate() {
		return endYCoordinate;
	}

	public void setEndYCoordinate(int endYCoordinate) {
		this.endYCoordinate = endYCoordinate;
	}

	public BufferedImage getLine() {
		return line;
	}

	public void setLine(BufferedImage line) {
		this.line = line;
	}
}
