package org.example;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {

    private static final int NUM_STEPS = 365;
    private static final double DT = 86400;
    private static final double G = 6.6743e-11;
    private static final double SUN_MASS = 1.989e30;
    private static final double EARTH_MASS = 5.972e24;
    private static final double MOON_MASS = 7.347e22;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Moon Orbit Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Time step, sec:"));
        JTextField timeStepField = new JTextField("", 10);
        controlPanel.add(timeStepField);

        JButton runButton = new JButton("Run Simulation");
        controlPanel.add(runButton);

        frame.add(controlPanel, BorderLayout.NORTH);

        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Moon Orbit Simulation",
                "X (m)",
                "Y (m)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        Ellipse2D.Double circleShape = new Ellipse2D.Double(-2, -2, 4, 4);

        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, circleShape);

        renderer.setSeriesPaint(1, Color.GRAY);
        renderer.setSeriesLinesVisible(1, false);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShape(1, circleShape);

        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesShape(2, new Ellipse2D.Double(-3, -3, 6, 6));
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(2, true);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 600));
        frame.add(chartPanel, BorderLayout.CENTER);

        runButton.addActionListener(_ -> {
            double dt = Double.parseDouble(timeStepField.getText());
            double totalTime = Double.parseDouble(String.valueOf((NUM_STEPS * DT)));
            int steps = (int)(totalTime / dt);

            XYSeries earthSeries = new XYSeries("Earth Orbit");
            XYSeries moonSeries = new XYSeries("Moon Trajectory");
            XYSeries sunSeries = new XYSeries("Sun");
            sunSeries.add(0, 0);

            double[] earthPositions = calculateEarthPositions(steps, dt);
            for (int t = 0; t < steps; t++) {
                earthSeries.add(earthPositions[t * 2], earthPositions[t * 2 + 1]);
            }

            double earthMoonDistance = 384400000;
            double moonVelocity = 1018.289046;
            double xEarth = 0;
            double yEarth = 0;
            double xMoon = xEarth;
            double yMoon = yEarth + earthMoonDistance;
            double vxMoon = moonVelocity;
            double vyMoon = 0;

            int i = 0;
            for (int t = 0; t < steps; t++) {
                earthMoonDistance = Math.sqrt(Math.pow(xEarth - xMoon, 2) + Math.pow(yEarth - yMoon, 2));

                double gravityForce = G * EARTH_MASS * MOON_MASS / (earthMoonDistance * earthMoonDistance);
                double xGravityForce = (xEarth - xMoon) / earthMoonDistance * gravityForce;
                double yGravityForce = (yEarth - yMoon) / earthMoonDistance * gravityForce;
                double axMoon = xGravityForce / MOON_MASS;
                double ayMoon = yGravityForce / MOON_MASS;
                double xMoonMid = xMoon + vxMoon * dt / 2;
                double yMoonMid = yMoon + vyMoon * dt / 2;
                double xMoonMidForce = (xEarth - xMoonMid) / earthMoonDistance * gravityForce;
                double yMoonMidForce = (yEarth - yMoonMid) / earthMoonDistance * gravityForce;
                double vxMoonMid = vxMoon + axMoon * dt / 2;
                double vyMoonMid = vyMoon + ayMoon * dt / 2;
                double axMoonMid = xMoonMidForce / MOON_MASS;
                double ayMoonMid = yMoonMidForce / MOON_MASS;

                xMoon += vxMoonMid * dt;
                yMoon += vyMoonMid * dt;
                vxMoon += axMoonMid * dt;
                vyMoon += ayMoonMid * dt;
                moonSeries.add(xMoon + earthPositions[i], yMoon + earthPositions[i + 1]);
                i += 2;
            }

            dataset.removeAllSeries();
            dataset.addSeries(earthSeries);
            dataset.addSeries(moonSeries);
            dataset.addSeries(sunSeries);
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static double[] calculateEarthPositions(int steps, double dt) {
        double[] positions = new double[steps * 2];
        double earthSunDistance = 1.5e11;
        double earthVelocity = 29749.15427;
        double xEarth = 0;
        double yEarth = earthSunDistance;
        double vxEarth = earthVelocity;
        double vyEarth = 0;

        for (int t = 0; t < steps; t++) {
            earthSunDistance = Math.sqrt(xEarth * xEarth + yEarth * yEarth);

            double gravityForce = G * SUN_MASS * EARTH_MASS / (earthSunDistance * earthSunDistance);
            double xGravityForce = -xEarth / earthSunDistance * gravityForce;
            double yGravityForce = -yEarth / earthSunDistance * gravityForce;
            double axEarth = xGravityForce / EARTH_MASS;
            double ayEarth = yGravityForce / EARTH_MASS;
            double xEarthMid = xEarth + vxEarth * dt / 2;
            double yEarthMid = yEarth + vyEarth * dt / 2;
            double xEarthMidForce = -xEarthMid / earthSunDistance * gravityForce;
            double yEarthMidForce = -yEarthMid / earthSunDistance * gravityForce;
            double vxEarthMid = vxEarth + axEarth * dt / 2;
            double vyEarthMid = vyEarth + ayEarth * dt / 2;
            double axEarthMid = xEarthMidForce / EARTH_MASS;
            double ayEarthMid = yEarthMidForce / EARTH_MASS;

            xEarth += vxEarthMid * dt;
            yEarth += vyEarthMid * dt;
            vxEarth += axEarthMid * dt;
            vyEarth += ayEarthMid * dt;
            positions[t * 2] = xEarth / 10;
            positions[t * 2 + 1] = yEarth / 10;
        }

        return positions;
    }

}