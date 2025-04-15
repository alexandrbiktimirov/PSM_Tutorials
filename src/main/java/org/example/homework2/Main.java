import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Main {
    // Fixed acceleration values (user cannot change these)
    private static final double GX = 0.0;
    private static final double GY = -10.0;

    // Swing components for user input and chart display
    private JFrame frame;
    private JTextField velocityField, angleField, dragField, massField, dtField;
    private ChartPanel chartPanel; // JFreeChart panel

    public Main() {
        // Create and set up the window
        frame = new JFrame("Projectile Motion Simulator (JFreeChart)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create the input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.add(new JLabel("Initial Velocity (m/s):"));
        velocityField = new JTextField("50");
        inputPanel.add(velocityField);

        inputPanel.add(new JLabel("Launch Angle (deg):"));
        angleField = new JTextField("45");
        inputPanel.add(angleField);

        inputPanel.add(new JLabel("Drag Coefficient (k):"));
        dragField = new JTextField("0.1");
        inputPanel.add(dragField);

        inputPanel.add(new JLabel("Mass (kg):"));
        massField = new JTextField("1");
        inputPanel.add(massField);

        inputPanel.add(new JLabel("Time Step (s):"));
        dtField = new JTextField("0.1");
        inputPanel.add(dtField);

        JButton simulateButton = new JButton("Simulate");
        inputPanel.add(simulateButton);
        // Add a placeholder to keep the grid alignment
        inputPanel.add(new JLabel(""));

        frame.add(inputPanel, BorderLayout.NORTH);

        // Create an empty dataset and chart for initialization
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Projectile Motion",
                "X Position",
                "Y Position",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        frame.add(chartPanel, BorderLayout.CENTER);

        // Set up the simulate button action
        simulateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulateAndDisplay();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Runs the simulations and updates the chart
    private void simulateAndDisplay() {
        try {
            double v0 = Double.parseDouble(velocityField.getText());
            double angleDeg = Double.parseDouble(angleField.getText());
            double k = Double.parseDouble(dragField.getText());
            double m = Double.parseDouble(massField.getText());
            double dt = Double.parseDouble(dtField.getText());

            // Compute trajectories using both methods
            ArrayList<double[]> eulerTrajectory = simulateEuler(v0, angleDeg, k, m, dt);
            ArrayList<double[]> midpointTrajectory = simulateMidpoint(v0, angleDeg, k, m, dt);

            // Create JFreeChart series for each method
            XYSeries eulerSeries = new XYSeries("Euler");
            for (double[] point : eulerTrajectory) {
                eulerSeries.add(point[0], point[1]);
            }

            XYSeries midpointSeries = new XYSeries("Midpoint");
            for (double[] point : midpointTrajectory) {
                midpointSeries.add(point[0], point[1]);
            }

            // Build the dataset and create a new chart
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(eulerSeries);
            dataset.addSeries(midpointSeries);

            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Projectile Motion",
                    "X Position",
                    "Y Position",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,  // include legend
                    true,  // tooltips
                    false  // URLs
            );

            // Update the chart panel with the new chart
            chartPanel.setChart(chart);
            chartPanel.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Simulation using Euler's method
    private ArrayList<double[]> simulateEuler(double v0, double angleDeg, double k, double m, double dt) {
        ArrayList<double[]> trajectory = new ArrayList<>();

        // Initial conditions
        double Sx = 0, Sy = 0;
        double rad = Math.toRadians(angleDeg);
        double Vx = v0 * Math.cos(rad);
        double Vy = v0 * Math.sin(rad);
        trajectory.add(new double[]{Sx, Sy});

        // Continue until the projectile falls below ground level (Sy < 0)
        while (Sy >= 0) {
            double DSx = Vx * dt;  // change in Sx
            double DSy = Vy * dt;  // change in Sy

            // Change in velocity: DVx and DVy
            double DVx = (GX - (k / m) * Vx) * dt;
            double DVy = (GY - (k / m) * Vy) * dt;

            // Update positions and velocities
            Sx += DSx;
            Sy += DSy;
            Vx += DVx;
            Vy += DVy;

            trajectory.add(new double[]{Sx, Sy});
        }
        return trajectory;
    }

    // Simulation using the Midpoint method (Improved Euler)
    private ArrayList<double[]> simulateMidpoint(double v0, double angleDeg, double k, double m, double dt) {
        ArrayList<double[]> trajectory = new ArrayList<>();

        // Initial conditions
        double Sx = 0, Sy = 0;
        double rad = Math.toRadians(angleDeg);
        double Vx = v0 * Math.cos(rad);
        double Vy = v0 * Math.sin(rad);
        trajectory.add(new double[]{Sx, Sy});

        // Continue until the projectile falls below ground level (Sy < 0)
        while (Sy >= 0) {
            // First compute Euler increments
            double DSx = Vx * dt;
            double DSy = Vy * dt;
            double DVx = (GX - (k / m) * Vx) * dt;
            double DVy = (GY - (k / m) * Vy) * dt;

            // Compute midpoint velocities
            double Vx_2 = Vx + 0.5 * DVx;
            double Vy_2 = Vy + 0.5 * DVy;

            // Recompute increments using midpoint values
            double DSx2 = Vx_2 * dt;
            double DSy2 = Vy_2 * dt;
            double DVx2 = (GX - (k / m) * Vx_2) * dt;
            double DVy2 = (GY - (k / m) * Vy_2) * dt;

            // Update positions and velocities using midpoint increments
            Sx += DSx2;
            Sy += DSy2;
            Vx += DVx2;
            Vy += DVy2;

            trajectory.add(new double[]{Sx, Sy});
        }
        return trajectory;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}