package org.example.homework7;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
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

public class Main {
    public static void main(String[] args) {
        int N = 40;
        double[][] T = new double[N+2][N+2];

        for (int i = 0; i < N+2; i++) {
            T[i][0] = 300;
            T[i][N+1] = -300;
            T[0][i] = -200;
            T[N+1][i] = 200;
        }

        double tol = 1e-4;

        while (true) {
            double maxDiff = 0;
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= N; j++) {
                    double old = T[i][j];
                    T[i][j] = 0.25 * (T[i-1][j] + T[i+1][j] + T[i][j-1] + T[i][j+1]);
                    double diff = Math.abs(T[i][j] - old);
                    if (diff > maxDiff) maxDiff = diff;
                }
            }
            if (maxDiff < tol) break;
        }

        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double[] x = new double[N * N];
        double[] y = new double[N * N];
        double[] z = new double[N * N];
        int idx = 0;

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                x[idx] = i;
                y[idx] = j;
                z[idx] = T[i][j];
                idx++;
            }
        }

        dataset.addSeries("Temperature", new double[][] { x, y, z });
        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setLowerMargin(0);
        yAxis.setUpperMargin(0);
        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setBlockWidth(1);
        renderer.setBlockHeight(1);
        LookupPaintScale paintScale = new LookupPaintScale(-300, 300, Color.BLACK);

        for (int i = 0; i <= 100; i++) {
            double v = -300 + i * 600.0 / 100.0;
            paintScale.add(v, Color.getHSBColor((float)(0.7 - 0.7 * i / 100.0), 1f, 1f));
        }

        renderer.setPaintScale(paintScale);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart("Temperature Distribution", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        chart.setPadding(new RectangleInsets(0, 0, 0, 0));

        NumberAxis scaleAxis = new NumberAxis("Temperature");
        scaleAxis.setRange(-300, 300);
        scaleAxis.setTickUnit(new NumberTickUnit(50));

        PaintScaleLegend legend = new PaintScaleLegend(paintScale, scaleAxis);
        legend.setPosition(RectangleEdge.RIGHT);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setStripWidth(15);
        chart.addSubtitle(legend);

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(600, 600));

        JFrame frame = new JFrame("Homework 7");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
