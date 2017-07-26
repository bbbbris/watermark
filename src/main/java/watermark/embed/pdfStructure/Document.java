package watermark.embed.pdfStructure;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.lang3.mutable.MutableInt;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.mathworks.engine.MatlabEngine;

import watermark.embed.util.Hadamard;
import watermark.test.common.Comparison;

public class Document {

	private List<String> lines;
	private int[] watermark;
	private List<PDFObject> objects;
	private CrossReferenceTable xrefTable;

	private String workingDirectory;
	private String documentName;
	private int numberOfPages;

	public static final String END_OF_FILE_MARKER = "%%EOF";
	public static final String START_OF_OBJECT_MARKER = "obj";
	public static final String START_OF_XREF_TABLE_MARKER = "xref";

	public Document() {
	}

	public Document(String path, int[] watermark) {
		this.watermark = watermark;

		workingDirectory = path.substring(0, path.lastIndexOf("/")) + "/";
		documentName = path.substring(path.lastIndexOf("/"));

		File pdfFile;
		try {
			pdfFile = new File(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pdfFile), "Cp1252"));

			lines = new ArrayList<>();
			objects = new ArrayList<>();

			String line = br.readLine();
			lines.add(line + "\r\n");
			MutableInt lineIndex = new MutableInt(0);

			while (!line.equals(END_OF_FILE_MARKER)) {

				if (line.matches("\\d+ \\d+ obj")) {
					objects.add(new PDFObject(br, line, lines, lineIndex));
					line = lines.get(lineIndex.getValue());
				}

				if (line.contains(START_OF_XREF_TABLE_MARKER)) {
					xrefTable = new CrossReferenceTable(br, line, lines, lineIndex, objects);
				}

				line = br.readLine();
				lineIndex.increment();
				lines.add(line + "\r\n");
				if (line == null) {
					throw new IOException();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error reading file!");
			e.printStackTrace();
		}
	}

	public Document(String path, int[] watermark, Comparison comparison) {
		workingDirectory = path.substring(0, path.lastIndexOf("/")) + "/";
		documentName = path.substring(path.lastIndexOf("/"));

		double[] watermarkEncoded = encode(watermark, comparison);

		String src = path;
		String dest = src.substring(0, src.length() - 4) + "_W.pdf";

		try {
			PdfReader reader;
			reader = new PdfReader(src);
			numberOfPages = reader.getNumberOfPages();
			for (int i = 1; i <= numberOfPages; i++) {
				PdfDictionary dict = reader.getPageN(i);
				PdfObject object = dict.getDirectObject(PdfName.CONTENTS);
				if (object instanceof PRStream) {
					PRStream stream = (PRStream) object;
					byte[] data = PdfReader.getStreamBytes(stream);
					String pageStream = new String(data);
					// System.out.println(pageStream);
					String watermarked = embed(pageStream, watermarkEncoded);
					stream.setData(watermarked.getBytes());
				}
			}
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
			stamper.close();
			reader.close();
		} catch (IOException | DocumentException e) {
			e.printStackTrace();
		}
	}

	private double[] encode(int[] watermark, Comparison comparison) {

		comparison.setEmbeddedID(watermark);

		try {
			// Hadamard
			String watermarkString = "";
			for (int i = 0; i < watermark.length; i++) {
				watermarkString += watermark[i];
			}

			System.out.println(watermarkString);
			System.out.println(watermarkString.length());

			Hadamard hadamard = new Hadamard(7);
			String hadamardEncodedString = hadamard.encode(watermarkString);
			for (int i = hadamardEncodedString.length(); i < 144; i++) {
				hadamardEncodedString += "0";
			}

			System.out.println(hadamardEncodedString);
			System.out.println(hadamardEncodedString.length());

			int[] hadamardEncoded = new int[144];
			for (int i = 0; i < hadamardEncoded.length; i++) {
				hadamardEncoded[i] = Integer.parseInt(hadamardEncodedString.substring(i, i + 1));
			}

			comparison.setEmbeddedHadamard(hadamardEncoded);

			// LDPC
			MatlabEngine eng = MatlabEngine.startMatlab();
			eng.feval("addpath",
					"/Users/abrisnagy/Documents/development/watermark/src/main/resources/LDPC/".toCharArray());
			eng.feval("addpath",
					"/Users/abrisnagy/Documents/development/watermark/src/main/resources/LDPC/codes".toCharArray());

			double[] LDPCencoded = eng.feval("encode", hadamardEncoded);

			comparison.setEmbeddedLDPC(LDPCencoded);

			System.out.println(LDPCencoded.length);
			for (int i = 0; i < LDPCencoded.length; i++) {
				System.out.print(LDPCencoded[i] + " ");
			}

			eng.close();

			return LDPCencoded;
		} catch (IllegalArgumentException | IllegalStateException | InterruptedException | RejectedExecutionException
				| ExecutionException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String embed(String pageStream, double[] watermarkEncoded) {
		Page page = new Page(pageStream);
		String watermarkedPage = page.embed(watermarkEncoded);
		return watermarkedPage;
	}

	public void embed() {
		MutableInt offsetChange = new MutableInt(0);
		MutableInt watermarkIndex = new MutableInt(0);

		for (PDFObject object : objects) {
			object.embed(lines, watermark, watermarkIndex, offsetChange);
		}
		int offsetOfXref = Integer.parseInt(lines.get(xrefTable.endLineIndex + 1).substring(0,
				lines.get(xrefTable.endLineIndex + 1).indexOf("\r\n")));
		lines.set(xrefTable.endLineIndex + 1, (offsetOfXref + offsetChange.getValue()) + "\r\n");

		String watermarkedDocumentName = documentName.substring(0, documentName.lastIndexOf('.')) + "_watermarked.pdf";

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(workingDirectory + watermarkedDocumentName), "Cp1252"))) {
			for (int i = 0; i < lines.size(); i++) {
				writer.write(lines.get(i));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print() {
		int numberOfLines = lines.size();
		for (int i = 0; i < numberOfLines; i++) {
			System.out.print(i + " -> " + lines.get(i));
		}
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public int[] getWatermark() {
		return watermark;
	}

	public void setWatermark(int[] watermark) {
		this.watermark = watermark;
	}

	public List<PDFObject> getObjects() {
		return objects;
	}

	public void setObjects(List<PDFObject> objects) {
		this.objects = objects;
	}

	public CrossReferenceTable getXrefTable() {
		return xrefTable;
	}

	public void setXrefTable(CrossReferenceTable xrefTable) {
		this.xrefTable = xrefTable;
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	@Override
	public String toString() {
		return "Document [objects=" + objects + ", xrefTable=" + xrefTable + "]";
	}

}
