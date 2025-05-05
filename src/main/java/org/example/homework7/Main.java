package org.example.homework7;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.DefaultXYZDataset;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        int N = 40;
        int size = N * N;
        RealMatrix A = new Array2DRowRealMatrix(size, size);
        RealVector b = new ArrayRealVector(size);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int k = i * N + j;
                A.setEntry(k, k, -4);
                if (j > 0) A.setEntry(k, i * N + (j - 1), 1);
                if (j < N - 1) A.setEntry(k, i * N + (j + 1), 1);
                if (i > 0) A.setEntry(k, (i - 1) * N + j, 1);
                if (i < N - 1) A.setEntry(k, (i + 1) * N + j, 1);
                if (j == 0) b.addToEntry(k, -200);
                if (j == N - 1) b.addToEntry(k, -150);
                if (i == 0) b.addToEntry(k, -100);
                if (i == N - 1) b.addToEntry(k, -50);
            }
        }

        DecompositionSolver solver = new LUDecomposition(A).getSolver();
        RealVector x = solver.solve(b);

        double[] xCoord = new double[size];
        double[] yCoord = new double[size];
        double[] zCoord = new double[size];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int k = i * N + j;
                xCoord[k] = j + 1;
                yCoord[k] = N - i;
                zCoord[k] = x.getEntry(k);
            }
        }

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        dataset.addSeries("Temperature", new double[][]{xCoord, yCoord, zCoord});

        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);

        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setLowerMargin(0);
        yAxis.setUpperMargin(0);

        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setBlockWidth(1);
        renderer.setBlockHeight(1);
        LookupPaintScale scale = new LookupPaintScale(50, 200, Color.BLACK);

        for (int i = 0; i <= 100; i++) {
            float hue = 0.7f - 0.7f * i / 100f;
            double v = 50 + i * (150.0 / 100.0);
            scale.add(v, Color.getHSBColor(hue, 1f, 1f));
        }

        renderer.setPaintScale(scale);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart("Temperature Distribution", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setPadding(new RectangleInsets(0, 0, 0, 0));
        NumberAxis scaleAxis = new NumberAxis("Temperature");

        scaleAxis.setRange(50, 200);
        scaleAxis.setTickUnit(new NumberTickUnit(50));

        PaintScaleLegend legend = new PaintScaleLegend(scale, scaleAxis);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);

        chart.addSubtitle(legend);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(600, 600));

        JFrame frame = new JFrame("Task 7 – Plate Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
