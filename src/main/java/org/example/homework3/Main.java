import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main extends JFrame {
    private JTextField massField;
    private JTextField lengthField;
    private JTextField angleField;
    private JTextField timeField;
    private JTextField timeStepField;
    private JButton simulateButton;
    private JPanel chartsPanel;
    private static final double G = 9.81;
    private XYSeriesCollection heunEnergiesDataset;
    private XYSeriesCollection heunPhaseDataset;
    private XYSeriesCollection rk4EnergiesDataset;
    private XYSeriesCollection rk4PhaseDataset;
    private JFreeChart heunEnergiesChart;
    private JFreeChart heunPhaseChart;
    private JFreeChart rk4EnergiesChart;
    private JFreeChart rk4PhaseChart;

    public Main() {
        super("Pendulum Simulation (Heun & RK4)");
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Mass:"));
        massField = new JTextField("", 4);
        inputPanel.add(massField);
        inputPanel.add(new JLabel("Length:"));
        lengthField = new JTextField("", 4);
        inputPanel.add(lengthField);
        inputPanel.add(new JLabel("Angle (°):"));
        angleField = new JTextField("", 4);
        inputPanel.add(angleField);
        inputPanel.add(new JLabel("Time:"));
        timeField = new JTextField("", 4);
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("Time Step:"));
        timeStepField = new JTextField("", 5);
        inputPanel.add(timeStepField);
        simulateButton = new JButton("Simulate");
        inputPanel.add(simulateButton);
        add(inputPanel, BorderLayout.NORTH);
        chartsPanel = new JPanel(new GridLayout(2, 2));
        add(chartsPanel, BorderLayout.CENTER);
        heunEnergiesDataset = new XYSeriesCollection();
        heunPhaseDataset = new XYSeriesCollection();
        rk4EnergiesDataset = new XYSeriesCollection();
        rk4PhaseDataset = new XYSeriesCollection();
        heunEnergiesChart = ChartFactory.createXYLineChart(
                "Pendulum Heun's Energies Motion",
                "Time (s)",
                "Energy (J)",
                heunEnergiesDataset
        );
        heunPhaseChart = ChartFactory.createXYLineChart(
                "Pendulum Heun Omega",
                "Alpha (rad)",
                "Omega",
                heunPhaseDataset
        );
        applyOmegaRenderer(heunPhaseChart);
        rk4EnergiesChart = ChartFactory.createXYLineChart(
                "Pendulum RK4 Energies Motion",
                "Time (s)",
                "Energy (J)",
                rk4EnergiesDataset
        );
        rk4PhaseChart = ChartFactory.createXYLineChart(
                "Pendulum RK4 Omega",
                "Alpha (rad)",
                "Omega",
                rk4PhaseDataset
        );

        applyOmegaRenderer(rk4PhaseChart);
        chartsPanel.add(new ChartPanel(heunEnergiesChart));
        chartsPanel.add(new ChartPanel(heunPhaseChart));
        chartsPanel.add(new ChartPanel(rk4EnergiesChart));
        chartsPanel.add(new ChartPanel(rk4PhaseChart));
        simulateButton.addActionListener(e -> simulateAndDisplay());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private void simulateAndDisplay() {
        double m = parseDouble(massField.getText(), 1.0);
        double r = parseDouble(lengthField.getText(), 1.0);
        double angleDeg = parseDouble(angleField.getText(), 45.0);
        double T = parseDouble(timeField.getText(), 5.0);
        double dt = parseDouble(timeStepField.getText(), 0.01);
        double alpha0 = Math.toRadians(angleDeg);
        int steps = (int) Math.floor(T / dt);
        double[] alphaHeun = new double[steps + 1];
        double[] omegaHeun = new double[steps + 1];
        double[] alphaRK4 = new double[steps + 1];
        double[] omegaRK4 = new double[steps + 1];
        alphaHeun[0] = alpha0;
        omegaHeun[0] = 0.0;
        alphaRK4[0] = alpha0;
        omegaRK4[0] = 0.0;
        heunMethod(alphaHeun, omegaHeun, r, dt);
        rk4Method(alphaRK4, omegaRK4, r, dt);
        heunEnergiesDataset = createEnergiesDataset(alphaHeun, omegaHeun, m, r, dt);
        heunPhaseDataset = createPhaseDataset(alphaHeun, omegaHeun);
        rk4EnergiesDataset = createEnergiesDataset(alphaRK4, omegaRK4, m, r, dt);
        rk4PhaseDataset = createPhaseDataset(alphaRK4, omegaRK4);
        heunEnergiesChart.getXYPlot().setDataset(heunEnergiesDataset);
        heunPhaseChart.getXYPlot().setDataset(heunPhaseDataset);
        rk4EnergiesChart.getXYPlot().setDataset(rk4EnergiesDataset);
        rk4PhaseChart.getXYPlot().setDataset(rk4PhaseDataset);
        applyOmegaRenderer(heunPhaseChart);
        applyOmegaRenderer(rk4PhaseChart);
    }

    private void heunMethod(double[] alpha, double[] omega, double r, double dt) {
        int steps = alpha.length - 1;

        for (int i = 0; i < steps; i++) {
            double alpha_i = alpha[i];
            double omega_i = omega[i];
            double k1_alpha = omega_i;
            double k1_omega = - (G / r) * Math.sin(alpha_i);
            double alpha_pred = alpha_i + dt * k1_alpha;
            double omega_pred = omega_i + dt * k1_omega;
            double k2_alpha = omega_pred;
            double k2_omega = - (G / r) * Math.sin(alpha_pred);
            alpha[i + 1] = alpha_i + (dt / 2.0) * (k1_alpha + k2_alpha);
            omega[i + 1] = omega_i + (dt / 2.0) * (k1_omega + k2_omega);
        }
    }

    private void rk4Method(double[] alpha, double[] omega, double r, double dt) {
        int steps = alpha.length - 1;

        for (int i = 0; i < steps; i++) {
            double alpha_i = alpha[i];
            double omega_i = omega[i];
            double k1_alpha = omega_i;
            double k1_omega = - (G / r) * Math.sin(alpha_i);
            double k2_alpha = omega_i + 0.5 * dt * k1_omega;
            double k2_omega = - (G / r) * Math.sin(alpha_i + 0.5 * dt * k1_alpha);
            double k3_alpha = omega_i + 0.5 * dt * k2_omega;
            double k3_omega = - (G / r) * Math.sin(alpha_i + 0.5 * dt * k2_alpha);
            double k4_alpha = omega_i + dt * k3_omega;
            double k4_omega = - (G / r) * Math.sin(alpha_i + dt * k3_alpha);
            alpha[i + 1] = alpha_i + (dt / 6.0) * (k1_alpha + 2.0 * k2_alpha + 2.0 * k3_alpha + k4_alpha);
            omega[i + 1] = omega_i + (dt / 6.0) * (k1_omega + 2.0 * k2_omega + 2.0 * k3_omega + k4_omega);
        }
    }

    private XYSeriesCollection createEnergiesDataset(double[] alpha, double[] omega, double m, double r, double dt) {
        XYSeries peSeries = new XYSeries("Ep");
        XYSeries keSeries = new XYSeries("Ek");
        XYSeries teSeries = new XYSeries("Et");

        for (int i = 0; i < alpha.length; i++) {
            double t = i * dt;
            double pe = m * G * r * (1.0 - Math.cos(alpha[i]));
            double ke = 0.5 * m * r * r * (omega[i] * omega[i]);
            double te = pe + ke;
            peSeries.add(t, pe);
            keSeries.add(t, ke);
            teSeries.add(t, te);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(peSeries);
        dataset.addSeries(keSeries);
        dataset.addSeries(teSeries);

        return dataset;
    }

    private XYSeriesCollection createPhaseDataset(double[] alpha, double[] omega) {
        XYSeries phaseSeries = new XYSeries("Omega");
        for (int i = 0; i < alpha.length; i++) {
            phaseSeries.add(alpha[i], omega[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(phaseSeries);
        return dataset;
    }

    private double parseDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void applyOmegaRenderer(JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new Ellipse2D.Double(-2, -2, 4, 4));
        renderer.setSeriesShapesFilled(0, true);
        renderer.setSeriesPaint(0, Color.RED);
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setAutoRange(true);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);
        plot.setRenderer(renderer);
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}