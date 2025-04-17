package org.example.homework6;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class Main extends ApplicationFrame {
    public Main(String title) {
        super(title);

        int N = 10;
        double L = Math.PI;
        double dx = L / N;
        int points = N + 1;
        double dt = 0.01;
        double T = 10;

        double[] y = new double[points];
        double[] v = new double[points];
        double[] a = new double[points];
        double[] yHalf = new double[points];
        double[] vHalf = new double[points];

        int steps = (int) (T / dt);
        XYSeries epSeries = new XYSeries("Ep");
        XYSeries ekSeries = new XYSeries("Ek");
        XYSeries etSeries = new XYSeries("Et");

        double[] recordTimes = {0.05, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5};
        XYSeriesCollection shapeDataset = new XYSeriesCollection();
        XYSeries initial = new XYSeries("Initial (t=0)");

        for (int i = 0; i < points; i++) {
            double x = i * dx;
            y[i] = Math.sin(x) / 1000;
            v[i] = 0;
            initial.add(x, y[i]);
        }

        shapeDataset.addSeries(initial);

        for (int step = 0; step <= steps; step++) {
            double t = step * dt;
            for (int i = 1; i < points - 1; i++) {
                a[i] = (y[i - 1] - 2 * y[i] + y[i + 1]) / (dx * dx);
            }

            a[0] = 0;
            a[points - 1] = 0;

            for (int i = 0; i < points; i++) {
                yHalf[i] = y[i] + v[i] * dt / 2;
                vHalf[i] = v[i] + a[i] * dt / 2;
            }

            for (int i = 1; i < points - 1; i++) {
                a[i] = (yHalf[i - 1] - 2 * yHalf[i] + yHalf[i + 1]) / (dx * dx);
            }

            for (int i = 0; i < points; i++) {
                y[i] = y[i] + vHalf[i] * dt;
                v[i] = v[i] + a[i] * dt;
            }

            double ek = 0;
            double ep = 0;
            for (int i = 0; i < points; i++) {
                ek += v[i] * v[i] * dx / 2;
            }

            for (int i = 0; i < points - 1; i++) {
                ep += (y[i + 1] - y[i]) * (y[i + 1] - y[i]) / (2 * dx);
            }

            epSeries.add(t, ep);
            ekSeries.add(t, ek);
            etSeries.add(t, ek + ep);

            for (double rt : recordTimes) {
                if (Math.abs(t - rt) < dt / 2) {
                    XYSeries series = new XYSeries("t = " + rt + "s");
                    for (int i = 0; i < points; i++) {
                        series.add(i * dx, y[i]);
                    }
                    shapeDataset.addSeries(series);
                }
            }
        }

        XYSeriesCollection energyDataset = new XYSeriesCollection();
        energyDataset.addSeries(epSeries);
        energyDataset.addSeries(ekSeries);
        energyDataset.addSeries(etSeries);

        JFreeChart shapeChart = ChartFactory.createXYLineChart(
                "String Shape throughout time", "x", "y", shapeDataset, PlotOrientation.VERTICAL, true, false, false);
        JFreeChart energyChart = ChartFactory.createXYLineChart(
                "Energy of String", "Time", "Energy", energyDataset, PlotOrientation.VERTICAL, true, false, false);

        ChartPanel shapePanel = new ChartPanel(shapeChart);
        ChartPanel energyPanel = new ChartPanel(energyChart);

        JPanel container = new JPanel(new GridLayout(1, 2));

        container.add(shapePanel);
        container.add(energyPanel);
        setContentPane(container);
    }

    public static void main(String[] args) {
        var demo = new Main("String Wave Simulation");
        demo.pack();
        demo.setVisible(true);
    }
}