package org.example.homework8;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main extends JFrame {
    private final JTextField aField;
    private final JTextField bField;
    private final JTextField cField;
    private final JTextField dtField;
    private final JTextField xField;
    private final JTextField yField;
    private final JTextField zField;
    private final JTextField stepsField;
    private final JComboBox<String> methodBox;
    private final XYSeriesCollection dataset;
    private final XYLineAndShapeRenderer renderer;

    public Main() {
        super("Plot of z on x");
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 9, 5, 5));
        inputPanel.add(new JLabel("A:"));

        aField = new JTextField("10");
        inputPanel.add(aField);
        inputPanel.add(new JLabel("B:"));

        bField = new JTextField("25");
        inputPanel.add(bField);
        inputPanel.add(new JLabel("C:"));

        cField = new JTextField("2.6666666667");
        inputPanel.add(cField);
        inputPanel.add(new JLabel("Δt:"));

        dtField = new JTextField("0.01");
        inputPanel.add(dtField);
        inputPanel.add(new JLabel("x₀:"));

        xField = new JTextField("1");
        inputPanel.add(xField);
        inputPanel.add(new JLabel("y₀:"));

        yField = new JTextField("1");
        inputPanel.add(yField);
        inputPanel.add(new JLabel("z₀:"));

        zField = new JTextField("1");
        inputPanel.add(zField);
        inputPanel.add(new JLabel("steps:"));

        stepsField = new JTextField("20000");
        inputPanel.add(stepsField);
        inputPanel.add(new JLabel("Method:"));

        methodBox = new JComboBox<>(new String[]{"Euler", "Improved Euler", "RK4", "All"});
        inputPanel.add(methodBox);

        JButton plotButton = new JButton("Plot");
        plotButton.addActionListener(_ -> updatePlot());
        inputPanel.add(plotButton);
        add(inputPanel, BorderLayout.NORTH);

        dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart("Plot of z on x", "x", "z", dataset);
        XYPlot plot = chart.getXYPlot();
        renderer = new XYLineAndShapeRenderer(false, true);

        Ellipse2D dot = new Ellipse2D.Double(-1, -1, 2, 2);
        renderer.setSeriesShape(0, dot);
        renderer.setSeriesShape(1, dot);
        renderer.setSeriesShape(2, dot);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void updatePlot() {
        double A = Double.parseDouble(aField.getText());
        double B = Double.parseDouble(bField.getText());
        double C = Double.parseDouble(cField.getText());
        double dt = Double.parseDouble(dtField.getText());
        double x0 = Double.parseDouble(xField.getText());
        double y0 = Double.parseDouble(yField.getText());
        double z0 = Double.parseDouble(zField.getText());
        int steps = Integer.parseInt(stepsField.getText());
        String method = (String) methodBox.getSelectedItem();

        dataset.removeAllSeries();

        if ("All".equals(method)) {
            XYSeries se = new XYSeries("Euler");
            XYSeries si = new XYSeries("Improved Euler");
            XYSeries sr = new XYSeries("RK4");
            double xE = x0, yE = y0, zE = z0;
            double xI = x0, yI = y0, zI = z0;
            double xR = x0, yR = y0, zR = z0;
            for (int i = 0; i < steps; i++) {

                double dxE = A * (yE - xE);
                double dyE = xE * (B - zE) - yE;
                double dzE = xE * yE - C * zE;
                xE += dt * dxE;
                yE += dt * dyE;
                zE += dt * dzE;
                se.add(xE, zE);

                double k1x = A * (yI - xI);
                double k1y = xI * (B - zI) - yI;
                double k1z = xI * yI - C * zI;
                double xm = xI + 0.5 * dt * k1x;
                double ym = yI + 0.5 * dt * k1y;
                double zm = zI + 0.5 * dt * k1z;
                double k2x = A * (ym - xm);
                double k2y = xm * (B - zm) - ym;
                double k2z = xm * ym - C * zm;
                xI += dt * k2x;
                yI += dt * k2y;
                zI += dt * k2z;
                si.add(xI, zI);

                double K1x = A * (yR - xR);
                double K1y = xR * (B - zR) - yR;
                double K1z = xR * yR - C * zR;
                double X2 = xR + 0.5 * dt * K1x;
                double Y2 = yR + 0.5 * dt * K1y;
                double Z2 = zR + 0.5 * dt * K1z;
                double K2x = A * (Y2 - X2);
                double K2y = X2 * (B - Z2) - Y2;
                double K2z = X2 * Y2 - C * Z2;
                double X3 = xR + 0.5 * dt * K2x;
                double Y3 = yR + 0.5 * dt * K2y;
                double Z3 = zR + 0.5 * dt * K2z;
                double K3x = A * (Y3 - X3);
                double K3y = X3 * (B - Z3) - Y3;
                double K3z = X3 * Y3 - C * Z3;
                double X4 = xR + dt * K3x;
                double Y4 = yR + dt * K3y;
                double Z4 = zR + dt * K3z;
                double K4x = A * (Y4 - X4);
                double K4y = X4 * (B - Z4) - Y4;
                double K4z = X4 * Y4 - C * Z4;
                xR += dt * (K1x + 2 * K2x + 2 * K3x + K4x) / 6;
                yR += dt * (K1y + 2 * K2y + 2 * K3y + K4y) / 6;
                zR += dt * (K1z + 2 * K2z + 2 * K3z + K4z) / 6;
                sr.add(xR, zR);
            }
            dataset.addSeries(se);
            dataset.addSeries(si);
            dataset.addSeries(sr);

            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, Color.GREEN);
            renderer.setSeriesPaint(2, Color.BLUE);
            return;
        }

        XYSeries series = new XYSeries(Objects.requireNonNull(method));
        double x = x0, y = y0, z = z0;
        for (int i = 0; i < steps; i++) {
            switch (method) {
                case "Euler":
                    double dx = A * (y - x);
                    double dy = x * (B - z) - y;
                    double dz = x * y - C * z;
                    x += dt * dx;
                    y += dt * dy;
                    z += dt * dz;
                    break;
                case "Improved Euler":
                    double d1x = A * (y - x);
                    double d1y = x * (B - z) - y;
                    double d1z = x * y - C * z;
                    double xi = x + 0.5 * dt * d1x;
                    double yi = y + 0.5 * dt * d1y;
                    double zi = z + 0.5 * dt * d1z;
                    double d2x = A * (yi - xi);
                    double d2y = xi * (B - zi) - yi;
                    double d2z = xi * yi - C * zi;
                    x += dt * d2x;
                    y += dt * d2y;
                    z += dt * d2z;
                    break;
                case "RK4":
                    double k1x2 = A * (y - x);
                    double k1y2 = x * (B - z) - y;
                    double k1z2 = x * y - C * z;
                    double xx2 = x + 0.5 * dt * k1x2;
                    double yy2 = y + 0.5 * dt * k1y2;
                    double zz2 = z + 0.5 * dt * k1z2;
                    double k2x2 = A * (yy2 - xx2);
                    double k2y2 = xx2 * (B - zz2) - yy2;
                    double k2z2 = xx2 * yy2 - C * zz2;
                    double xx3 = x + 0.5 * dt * k2x2;
                    double yy3 = y + 0.5 * dt * k2y2;
                    double zz3 = z + 0.5 * dt * k2z2;
                    double k3x2 = A * (yy3 - xx3);
                    double k3y2 = xx3 * (B - zz3) - yy3;
                    double k3z2 = xx3 * yy3 - C * zz3;
                    double xx4 = x + dt * k3x2;
                    double yy4 = y + dt * k3y2;
                    double zz4 = z + dt * k3z2;
                    double k4x2 = A * (yy4 - xx4);
                    double k4y2 = xx4 * (B - zz4) - yy4;
                    double k4z2 = xx4 * yy4 - C * zz4;
                    x += dt * (k1x2 + 2 * k2x2 + 2 * k3x2 + k4x2) / 6;
                    y += dt * (k1y2 + 2 * k2y2 + 2 * k3y2 + k4y2) / 6;
                    z += dt * (k1z2 + 2 * k2z2 + 2 * k3z2 + k4z2) / 6;
                    break;
            }

            series.add(x, z);
        }

        dataset.addSeries(series);

        switch (method) {
            case "Euler":
                renderer.setSeriesPaint(0, Color.RED);
                break;
            case "Improved Euler":
                renderer.setSeriesPaint(0, Color.GREEN);
                break;
            case "RK4":
                renderer.setSeriesPaint(0, Color.BLUE);
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
