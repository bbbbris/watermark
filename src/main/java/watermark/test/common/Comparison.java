package watermark.test.common;

import java.io.PrintWriter;

public class Comparison {

	private int[] embeddedID;
	private int[] embeddedHadamard;
	private double[] embeddedLDPC;

	private double[] detectedLDPC;
	private double[] detectedHadamard;
	private int[] detectedID;

	public int[] getEmbeddedID() {
		return embeddedID;
	}

	public void setEmbeddedID(int[] watermark) {
		this.embeddedID = watermark;
	}

	public int[] getEmbeddedHadamard() {
		return embeddedHadamard;
	}

	public void setEmbeddedHadamard(int[] embeddedHadamard) {
		this.embeddedHadamard = embeddedHadamard;
	}

	public double[] getEmbeddedLDPC() {
		return embeddedLDPC;
	}

	public void setEmbeddedLDPC(double[] embeddedLDPC) {
		this.embeddedLDPC = embeddedLDPC;
	}

	public double[] getDetectedLDPC() {
		return detectedLDPC;
	}

	public void setDetectedLDPC(double[] detectedLDPC) {
		this.detectedLDPC = detectedLDPC;
	}

	public double[] getDetecetdHadamard() {
		return detectedHadamard;
	}

	public void setDetecetdHadamard(double[] detecetdHadamard) {
		this.detectedHadamard = detecetdHadamard;
	}

	public int[] getDetectedID() {
		return detectedID;
	}

	public void setDetectedID(String hadamardDecoded) {

		detectedID = new int[hadamardDecoded.length()];
		for (int i = 0; i < hadamardDecoded.length(); i++) {
			detectedID[i] = Integer.parseInt(hadamardDecoded.substring(i, i + 1));
		}
	}

	public void compare(PrintWriter writer) {
		// System.out.println(embeddedID.length);
		// System.out.println(detectedID.length + "\n");
		//
		// System.out.println(embeddedHadamard.length);
		// System.out.println(detectedHadamard.length + "\n");
		//
		// System.out.println(embeddedLDPC.length);
		// System.out.println(detectedLDPC.length + "\n");

		compareID(writer);

		compareHadamard(writer);

		compareLDPC(writer);
		
		writer.print("\n");
	}

	private int compareID(PrintWriter writer) {
		int errorCountID = 0;
		for (int i = 0; i < detectedID.length && i < embeddedID.length; i++) {
			if (detectedID[i] != embeddedID[i]) {
				errorCountID++;
			}
		}
		System.out.println("ID errors: " + errorCountID);
		writer.print(errorCountID + "\t");
		
		return errorCountID;
	}

	private void compareHadamard(PrintWriter writer) {
		int errorCountHadamard = 0;
		for (int i = 0; i < detectedHadamard.length; i++) {
			if (detectedHadamard[i] != embeddedHadamard[i]) {
				errorCountHadamard++;
			}
		}
		System.out.println("Hadamard errors: " + errorCountHadamard);
		writer.print(errorCountHadamard + "\t");
	}

	private void compareLDPC(PrintWriter writer) {
		int errorCountLDPC = 0;
		for (int i = 0; i < detectedLDPC.length; i++) {
			if ((int) Math.round(detectedLDPC[i]) != (int) embeddedLDPC[i]) {
				System.out.println((int) Math.round(detectedLDPC[i]) + "\t" + (int) embeddedLDPC[i]);
				errorCountLDPC++;
			}
		}
		System.out.println("LDPC errors: " + errorCountLDPC);
		writer.print(errorCountLDPC + "\t");
	}

}
