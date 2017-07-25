package watermark.extract.util;

import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ShowSpaces extends JFrame {
	private static final long serialVersionUID = 1L;

	public ShowSpaces(String s) {
		super("Spaces visualized");
		setSize(1920, 1080);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		ImageIcon image = new ImageIcon(s);
		JLabel imageLabel = new JLabel();
//		imageLabel.setBounds(10, 10, 400, 400);
		imageLabel.setVisible(true);
		add(imageLabel);

		Document input = new Document(s);
		List<Integer> watermark = input.extractWatermark();
		List<Line> lines = input.getLines();
		
//		for (int i = 0; i < lines.size(); i++) {
//			image = new ImageIcon(lines.get(i).getLine());
//			imageLabel = new JLabel(image);
//			imageLabel.setBounds(100, 10 + i * 80, 900, 200);
//			imageLabel.setVisible(true);
//			add(imageLabel);
//		}
		
//		for (int i = 0; i < lines.size(); i++) {
//			image = new ImageIcon(lines.get(i).getHorizontalProjection());
//			imageLabel = new JLabel(image);
//			imageLabel.setBounds(100, 10 + i * 80, 1600, 200);
//			imageLabel.setVisible(true);
//			add(imageLabel);
//		}
		
		for (int i = 0; i < lines.size(); i++) {
			image = new ImageIcon(lines.get(i).getSpacesToPrintOnOriginal());
			imageLabel = new JLabel(image);
			imageLabel.setBounds(100, 10 + i * 80, 1600, 200);
			imageLabel.setVisible(true);
			add(imageLabel);
		}
		
//		for (int i = 0; i < lines.size(); i++) {
//			image = new ImageIcon(lines.get(i).getSmoothProjection());
//			imageLabel = new JLabel(image);
//			imageLabel.setBounds(100, 10 + i * 80, 1600, 200);
//			imageLabel.setVisible(true);
//			add(imageLabel);
//		}
		
		image = new ImageIcon(lines.get(0).getLine());
		imageLabel = new JLabel(image);
		imageLabel.setBounds(0, 0, 0, 0);
		imageLabel.setVisible(false);
		add(imageLabel);
	}
}
