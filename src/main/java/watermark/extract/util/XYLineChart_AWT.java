package watermark.extract.util;

import java.awt.Color;
import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class XYLineChart_AWT extends ApplicationFrame {


	public XYLineChart_AWT(String applicationTitle, String chartTitle, double[] data1, int[] data2, List<Integer> data3) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data1, data2, data3),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data1, double[] data2) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data1, data2),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));
        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data1, int[] data2) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data1, data2),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data3, ArrayList<Integer> data4) {
        super(applicationTitle);
        
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data3, data4),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data3, ArrayList<Integer> data4, boolean flag) {
        super(applicationTitle);
        
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDatasetWithSpaces(data3, data4),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));
        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, double[] smoothRows, double[] thresholdedRows, ArrayList<Integer> splittingPoints) {
        super(applicationTitle);
        
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(smoothRows, thresholdedRows, splittingPoints),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));
        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }
    
    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data1, int[] data2, int[] data3) {
        super(applicationTitle);
        
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data1, data2, data3),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    public XYLineChart_AWT(String applicationTitle, String chartTitle, int[] data1) {
        super(applicationTitle);
        
        
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Category",
                "Score",
                createDataset(data1),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

	private XYDataset createDataset(int[] data1) {
		final XYSeries original = new XYSeries("Original");

        for (int i = 0; i < data1.length; i++) {
            original.add(i, data1[i]);
//            System.out.println(i + " -> " + data1[i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(original);
        return dataset;
	}

	private XYDataset createDataset(double[] smoothRows, double[] thresholdedRows, ArrayList<Integer> splittingPoints) {
		final XYSeries smoothRowsXY = new XYSeries("Lines");
	    final XYSeries thresholdedRowsXY = new XYSeries("Thresholded");
	    final XYSeries splittingPointsXY = new XYSeries("SplittingPoints");
	
	    for (int i = 0; i < smoothRows.length; i++) {
	        smoothRowsXY.add(i, smoothRows[i]);
	        thresholdedRowsXY.add(i, thresholdedRows[i]);
	    }
	    
	    for(int i = 0; i < splittingPoints.size(); i++) {
	        splittingPointsXY.add((int) splittingPoints.get(i), 125);
	    }
	
	    final XYSeriesCollection dataset = new XYSeriesCollection();
	    dataset.addSeries(thresholdedRowsXY);
	    dataset.addSeries(smoothRowsXY);
	    dataset.addSeries(splittingPointsXY);
	    return dataset;
	}

	private XYDataset createDataset(double[] data1, int[] data2, List<Integer> data3) {
        final XYSeries lines = new XYSeries("Lines");
        final XYSeries thresholdedData = new XYSeries("Thresholded");
        final XYSeries splittingPoints = new XYSeries("SplittingPoints");

        for (int i = 0; i < data1.length; i++) {
            lines.add(i, data1[i]);
            thresholdedData.add(i, data2[i]);
        }
        
        for(int i = 0; i < data3.size(); i++) {
            splittingPoints.add((int) data3.get(i), 125);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(thresholdedData);
        dataset.addSeries(lines);
        dataset.addSeries(splittingPoints);
        return dataset;
    }
    
    private XYDataset createDataset(int[] data3, ArrayList<Integer> data4) {
//        final XYSeries lines = new XYSeries("Lines");
//        final XYSeries thresholdedData = new XYSeries("Thresholded");
        final XYSeries splittingPoints = new XYSeries("SplittingPoints");
        final XYSeries splittingPoints123 = new XYSeries("SplittingPoints123");

//        for (int i = 0; i < data1.length; i++) {
//            lines.add(i, data1[i]);
//            thresholdedData.add(i, data2[i]);
//        }
//        
        for(int i = 0; i < data3.length; i++) {
            splittingPoints.add(i, data3[i]);
        }

        for(int i = 0; i < data4.size(); i++) {
            splittingPoints123.add(i, data4.get(i));
        }
        
        final XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(thresholdedData);
//        dataset.addSeries(lines);
//        dataset.addSeries(splittingPoints);
        dataset.addSeries(splittingPoints123);
        return dataset;
    }
    
    private XYDataset createDatasetWithSpaces(int[] data1, ArrayList<Integer> data2) {
      final XYSeries words = new XYSeries("Words");
      final XYSeries spaces = new XYSeries("Spaces");
      
      for(int i = 0; i < data1.length; i++) {
          words.add(i, data1[i]);
      }

      for(int i = 0; i < Math.max(data1.length, data2.size()); i++) {
          spaces.add(i, 5);
      }
      for(int i = 0; i < data2.size(); i++) {
          spaces.add((int) data2.get(i), 0);
      }
      
      final XYSeriesCollection dataset = new XYSeriesCollection();
      dataset.addSeries(spaces);
      dataset.addSeries(words);
      return dataset;
  }
    
    private XYDataset createDataset(double[] data3, ArrayList<Integer> data4) {
      final XYSeries splittingPoints = new XYSeries("SplittingPoints");
      final XYSeries splittingPoints123 = new XYSeries("SplittingPoints123");
      
      for(int i = 0; i < data3.length; i++) {
          splittingPoints.add(i, data3[i]);
      }

      for(int i = 0; i < data4.size(); i++) {
          splittingPoints123.add(i, data4.get(i));
      }
      
      final XYSeriesCollection dataset = new XYSeriesCollection();
      dataset.addSeries(splittingPoints123);
      return dataset;
  }
    
    private XYDataset createDataset(int[] data1, double[] data2) {
        final XYSeries original = new XYSeries("Original");
        final XYSeries smooth = new XYSeries("Smooth");

        for (int i = 0; i < data1.length; i++) {
            original.add(i, data1[i]);
            smooth.add(i, data2[i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(smooth);
        dataset.addSeries(original);
        return dataset;
    }
    
    private XYDataset createDataset(int[] data1, int[] data2) {
        final XYSeries original = new XYSeries("Original");
        final XYSeries smooth = new XYSeries("Smooth");

        for (int i = 0; i < data1.length; i++) {
            original.add(i, data1[i]);
            smooth.add(i, data2[i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(smooth);
        dataset.addSeries(original);
        return dataset;
    }
    
    private XYDataset createDataset(int[] data1, int[] data2, int[] data3) {
        final XYSeries original = new XYSeries("Words");
        final XYSeries smooth = new XYSeries("Thresholded words");
        final XYSeries spaces = new XYSeries("spaces");

        for (int i = 0; i < data1.length; i++) {
            original.add(i, data1[i]);
            smooth.add(i, data2[i]);
            spaces.add(i, data3[i]);
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(original);
        dataset.addSeries(smooth);
        dataset.addSeries(spaces);
        return dataset;
    }
}
