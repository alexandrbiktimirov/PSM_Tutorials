package org.example.homework9;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeriesCollection;

public class Main extends ApplicationFrame {
        private static final String AXIOM = "X";
        private static final double ANGLE = Math.toRadians(25);
        private static final Map<Character,String> RULES = new HashMap<>();

        static {
                RULES.put('X', "F+[[X]-X]-F[-FX]+X");
                RULES.put('F', "FF");
        }

        public Main(String title, int iterations, double step) {
                super(title);

                String lsys = buildLSystem(iterations);
                JPanel chartPanel = createAnimatedChartPanel(lsys, step);

                chartPanel.setPreferredSize(new Dimension(800,600));
                setContentPane(chartPanel);
        }

        private String buildLSystem(int n) {
                String current = AXIOM;
                StringBuilder next = new StringBuilder();

                for (int i = 0; i < n; i++) {
                        next.setLength(0);
                        for (char c : current.toCharArray()) {
                                next.append(RULES.getOrDefault(c, String.valueOf(c)));
                        }
                        current = next.toString();
                }

                return current;
        }

        private JPanel createAnimatedChartPanel(String instr, double step) {
                XYSeriesCollection empty = new XYSeriesCollection();

                JFreeChart chart = ChartFactory.createXYLineChart(
                        null, null, null, empty,
                        PlotOrientation.VERTICAL,
                        false, false, false
                );

                XYPlot plot = chart.getXYPlot();
                plot.setBackgroundPaint(Color.white);
                plot.getDomainAxis().setVisible(false);
                plot.getRangeAxis().setVisible(false);
                plot.setOutlineVisible(false);

                class State { Point2D.Double p; double theta; }

                Stack<State> stack = new Stack<>();
                State cur = new State();
                cur.p = new Point2D.Double(0,0);
                cur.theta = Math.PI/2;

                List<XYLineAnnotation> segments = new ArrayList<>();
                double minX = cur.p.x, maxX = cur.p.x, minY = cur.p.y, maxY = cur.p.y;

                for (char c : instr.toCharArray()) {
                        switch (c) {
                                case 'F':
                                        double x1 = cur.p.x, y1 = cur.p.y;
                                        double x2 = x1 + step*Math.cos(cur.theta);
                                        double y2 = y1 + step*Math.sin(cur.theta);
                                        segments.add(new XYLineAnnotation(
                                                x1, y1, x2, y2,
                                                new BasicStroke(1.0f),
                                                Color.green
                                        ));
                                        minX = Math.min(minX, Math.min(x1, x2));
                                        maxX = Math.max(maxX, Math.max(x1, x2));
                                        minY = Math.min(minY, Math.min(y1, y2));
                                        maxY = Math.max(maxY, Math.max(y1, y2));
                                        cur.p = new Point2D.Double(x2,y2);
                                        break;
                                case '+':
                                        cur.theta -= ANGLE;
                                        break;
                                case '-':
                                        cur.theta += ANGLE;
                                        break;
                                case '[':
                                        State copy = new State();
                                        copy.p = (Point2D.Double)cur.p.clone();
                                        copy.theta = cur.theta;
                                        stack.push(copy);
                                        break;
                                case ']':
                                        cur = stack.pop();
                                        break;
                        }
                }

                double padX = (maxX-minX)*0.05, padY = (maxY-minY)*0.05;
                plot.getDomainAxis().setAutoRange(false);
                plot.getDomainAxis().setRange(minX-padX, maxX+padX);
                plot.getRangeAxis().setAutoRange(false);
                plot.getRangeAxis().setRange(minY-padY, maxY+padY);

                final ChartPanel panel = new ChartPanel(chart);
                panel.setPopupMenu(null);

                Timer timer = new Timer(20, e -> {
                        if (segments.isEmpty()) {
                                ((Timer)e.getSource()).stop();
                        } else {
                                plot.addAnnotation(segments.removeFirst());
                                panel.repaint();
                        }
                });
                timer.setInitialDelay(500);
                timer.start();

                return panel;
        }

        public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> {
                        Main demo = new Main("L-system Fractal Plant", 7, 5.0);
                        demo.pack();
                        demo.setLocationRelativeTo(null);
                        demo.setDefaultCloseOperation(EXIT_ON_CLOSE);
                        demo.setVisible(true);
                });
        }
}