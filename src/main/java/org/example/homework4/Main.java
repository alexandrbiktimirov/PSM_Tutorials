package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Main extends JFrame {
    private JTextField mField, hField, rField, angField, dtField;
    private final XYSeriesCollection trajectoryData = new XYSeriesCollection();
    private final XYSeriesCollection energyData = new XYSeriesCollection();
    private ChartPanel chartPanelTrajectory, chartPanelEnergy;

    public Main() {
        super("Rolling Object Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(buildInputArea(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        add(buildChartsArea(), gbc);

        setVisible(true);
    }

    private JPanel buildInputArea() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.insets = new Insets(10, 10, 10, 10);
        cons.fill = GridBagConstraints.HORIZONTAL;

        mField = new JTextField(10);
        hField = new JTextField(10);
        rField = new JTextField(10);
        angField = new JTextField(10);
        dtField = new JTextField(10);

        cons.gridx = 0;
        cons.gridy = 0;
        panel.add(makeInputComponent("Mass:", mField), cons);
        cons.gridx = 1;
        panel.add(makeInputComponent("Height:", hField), cons);
        cons.gridx = 2;
        panel.add(makeInputComponent("Radius:", rField), cons);
        cons.gridx = 3;
        panel.add(makeInputComponent("Angle:", angField), cons);
        cons.gridx = 4;
        panel.add(makeInputComponent("Time Step:", dtField), cons);
        cons.gridx = 5;
        JButton btnRun = new JButton("Simulate");
        btnRun.addActionListener(_ -> executeSimulation());
        panel.add(btnRun, cons);

        return panel;
    }

    private JPanel buildChartsArea() {
        JPanel charts = new JPanel(new GridBagLayout());
        GridBagConstraints cons = new GridBagConstraints();
        cons.fill = GridBagConstraints.BOTH;
        cons.weightx = 1.0;
        cons.weighty = 1.0;

        JFreeChart trajectoriesChart = ChartFactory.createXYLineChart(
                "Rolling Object Motion Trajectories",
                "X",
                "Y",
                trajectoryData,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        trajectoriesChart.getXYPlot().setRenderer(configureTrajectoryRenderer());
        chartPanelTrajectory = new ChartPanel(trajectoriesChart);
        cons.gridx = 0;
        cons.gridy = 0;
        charts.add(chartPanelTrajectory, cons);

        JFreeChart energiesChart = ChartFactory.createXYLineChart(
                "Rolling Object Energies",
                "Time",
                "Energy",
                energyData,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        energiesChart.getXYPlot().setRenderer(configureEnergyRenderer());
        chartPanelEnergy = new ChartPanel(energiesChart);
        cons.gridx = 1;
        charts.add(chartPanelEnergy, cons);

        return charts;
    }

    private JPanel makeInputComponent(String labelText, JTextField field) {
        JPanel comp = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        comp.add(label, BorderLayout.NORTH);
        comp.add(field, BorderLayout.CENTER);
        return comp;
    }

    private XYLineAndShapeRenderer configureTrajectoryRenderer() {
        XYLineAndShapeRenderer rend = new XYLineAndShapeRenderer();
        rend.setSeriesLinesVisible(0, true);
        rend.setSeriesShapesVisible(0, false);
        rend.setSeriesStroke(0, new BasicStroke(3.0f));

        rend.setSeriesLinesVisible(1, false);
        rend.setSeriesShapesVisible(1, true);
        rend.setSeriesShape(1, new Ellipse2D.Double(-1, -1, 3, 3));

        rend.setSeriesLinesVisible(2, false);
        rend.setSeriesShapesVisible(2, true);
        rend.setSeriesShape(2, new Ellipse2D.Double(-2, -2, 4, 4));

        rend.setSeriesLinesVisible(3, false);
        rend.setSeriesShapesVisible(3, true);
        rend.setSeriesShape(3, new Ellipse2D.Double(-2, -2, 4, 4));
        return rend;
    }

    private XYLineAndShapeRenderer configureEnergyRenderer() {
        XYLineAndShapeRenderer rend = new XYLineAndShapeRenderer();
        rend.setSeriesStroke(0, new BasicStroke(3.0f));
        rend.setSeriesShapesVisible(0, false);
        rend.setSeriesStroke(1, new BasicStroke(3.0f));
        rend.setSeriesShapesVisible(1, false);
        rend.setSeriesStroke(2, new BasicStroke(3.0f));
        rend.setSeriesShapesVisible(2, false);
        return rend;
    }

    private void executeSimulation() {
        try {
            double mass = Double.parseDouble(mField.getText());
            double height = Double.parseDouble(hField.getText());
            double radius = Double.parseDouble(rField.getText());
            double angleDeg = Double.parseDouble(angField.getText());
            double dt = Double.parseDouble(dtField.getText());
            runRollingSim(mass, height, radius, angleDeg, dt);
            chartPanelTrajectory.repaint();
            chartPanelEnergy.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runRollingSim(double mass, double height, double radius, double angleDeg, double dt) {
        trajectoryData.removeAllSeries();
        energyData.removeAllSeries();

        double g = 9.81;
        double angleRad = Math.toRadians(angleDeg);
        double inertiaFactor = 3.6;
        double inertiaVal = inertiaFactor * mass * radius * radius;
        double length = height / Math.sin(angleRad);
        double accelDenominator = 1.0 + (inertiaVal / (mass * radius * radius));
        double accel = g * Math.sin(angleRad) / accelDenominator;
        double epsilon = accel / radius;
        double distance = 0.0;
        double speed = 0.0;
        double rotAngle = 0.0;
        double omega = 0.0;
        XYSeries surface = new XYSeries("Incline Surface");
        double xEnd = height * Math.cos(angleRad) / Math.sin(angleRad);
        surface.add(0.0, height);
        surface.add(xEnd, 0.0);
        XYSeries ballOutline = new XYSeries("Ball Projection");
        XYSeries centerPath = new XYSeries("Ball Center");
        XYSeries ringPath = new XYSeries("Ball Ring");
        XYSeries potEnergy = new XYSeries("Ep");
        XYSeries kinEnergy = new XYSeries("Ek");
        XYSeries totEnergy = new XYSeries("Et");

        double t = 0.0;
        boolean drawnProjection = false;
        do {
            double xCenter = distance * Math.cos(-angleRad) - radius * Math.sin(-angleRad);
            double yCenter = distance * Math.sin(-angleRad) + radius * Math.cos(-angleRad) + height;
            if (xCenter >= 0 && yCenter >= 0) {
                centerPath.add(xCenter, yCenter);
            }
            double xRing = radius * Math.sin(rotAngle) + xCenter;
            double yRing = radius * Math.cos(rotAngle) + yCenter;
            if (xRing >= 0 && yRing >= 0) {
                ringPath.add(xRing, yRing);
            }
            double potential = mass * g * yCenter;
            double kineticTrans = 0.5 * mass * speed * speed;
            double kineticRot = 0.5 * inertiaVal * omega * omega;
            double totalE = potential + kineticTrans + kineticRot;
            potEnergy.add(t, potential);
            kinEnergy.add(t, kineticTrans + kineticRot);
            totEnergy.add(t, totalE);
            if (!drawnProjection) {
                for (double theta = 0; theta <= 2 * Math.PI; theta += 0.05) {
                    double xPoint = xCenter + radius * Math.cos(theta);
                    double yPoint = yCenter + radius * Math.sin(theta);
                    ballOutline.add(xPoint, yPoint);
                }
                drawnProjection = true;
            }
            double midSpeed = speed + (accel * dt / 2);
            double midOmega = omega + (epsilon * dt / 2);
            distance += midSpeed * dt;
            speed += accel * dt;
            rotAngle += midOmega * dt;
            omega += epsilon * dt;
            t += dt;
            if (yCenter <= 0 || distance >= length) {
                break;
            }
        } while (true);

        XYSeriesCollection trajectoriesCollection = new XYSeriesCollection();
        trajectoriesCollection.addSeries(surface);
        trajectoriesCollection.addSeries(ballOutline);
        trajectoriesCollection.addSeries(centerPath);
        trajectoriesCollection.addSeries(ringPath);
        XYSeriesCollection energyCollection = new XYSeriesCollection();
        energyCollection.addSeries(potEnergy);
        energyCollection.addSeries(kinEnergy);
        energyCollection.addSeries(totEnergy);

        refreshDataset(trajectoryData, trajectoriesCollection);
        refreshDataset(energyData, energyCollection);
    }

    private void refreshDataset(XYSeriesCollection target, XYSeriesCollection source) {
        target.removeAllSeries();

        for (int i = 0; i < source.getSeriesCount(); i++) {
            target.addSeries(source.getSeries(i));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}