package org.example.homework9;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.DefaultXYZDataset;

public class Main {
    private int N;
    private int T;
    private int[][] oldGrid;
    private int[][] newGrid;

    public Main(int N, int T, int flag) throws IOException {
        this.N = N;
        this.T = T;
        oldGrid = new int[N][N];
        newGrid = new int[N][N];

        if (flag == 1) {
            loadConfig("config.txt");
        } else {
            randomConfig(15);
        }
    }

    private void loadConfig(String filename) throws IOException {
        List<int[]> coords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int row = Integer.parseInt(parts[1].trim());
                int col = Integer.parseInt(parts[0].trim());
                coords.add(new int[] { row, col });
            }
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                oldGrid[i][j] = 0;
            }
        }
        for (int[] c : coords) {
            oldGrid[c[0]][c[1]] = 1;
        }
    }

    private void randomConfig(int percentAlive) {
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                oldGrid[i][j] = (rand.nextInt(100) < percentAlive) ? 1 : 0;
            }
        }
    }

    private int liveNeighbours(int i, int j) {
        int count = 0;
        for (int di = -1; di <= 1; di++) {
            for (int dj = -1; dj <= 1; dj++) {
                if (di == 0 && dj == 0) continue;
                int x = (i + di + N) % N;
                int y = (j + dj + N) % N;
                count += oldGrid[x][y];
            }
        }
        return count;
    }

    public void play(int survive1, int survive2, int birth) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HHmm").format(new Date());
        File dir = new File("Game of life " + timestamp);
        if (!dir.exists()) dir.mkdir();

        // initial plot
        saveGenerationPlot(0, dir);

        for (int t = 1; t <= T; t++) {
            System.out.println("At time level " + t);
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    int live = liveNeighbours(i, j);
                    if (oldGrid[i][j] == 1 && live < survive1) {
                        newGrid[i][j] = 0;
                    } else if (oldGrid[i][j] == 1 && (live == survive1 || live == survive2)) {
                        newGrid[i][j] = 1;
                    } else if (oldGrid[i][j] == 1 && live > birth) {
                        newGrid[i][j] = 0;
                    } else if (oldGrid[i][j] == 0 && live == birth) {
                        newGrid[i][j] = 1;
                    } else {
                        newGrid[i][j] = oldGrid[i][j];
                    }
                }
            }
            // update and plot
            for (int i = 0; i < N; i++) {
                System.arraycopy(newGrid[i], 0, oldGrid[i], 0, N);
            }
            saveGenerationPlot(t, dir);
        }
    }

    private void saveGenerationPlot(int gen, File dir) throws IOException {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        double[] xValues = new double[N * N];
        double[] yValues = new double[N * N];
        double[] zValues = new double[N * N];
        int idx = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                xValues[idx] = j;
                yValues[idx] = (N - 1 - i);
                zValues[idx] = oldGrid[i][j];
                idx++;
            }
        }
        dataset.addSeries("Grid", new double[][] { xValues, yValues, zValues });

        NumberAxis xAxis = new NumberAxis("X");
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);
        NumberAxis yAxis = new NumberAxis("Y");
        yAxis.setLowerMargin(0);
        yAxis.setUpperMargin(0);

        XYBlockRenderer renderer = new XYBlockRenderer();
        renderer.setBlockWidth(1.0);
        renderer.setBlockHeight(1.0);
        LookupPaintScale paintScale = new LookupPaintScale(0.0, 1.0, Color.WHITE);
        paintScale.add(0.0, Color.WHITE);
        paintScale.add(1.0, Color.BLACK);
        renderer.setPaintScale(paintScale);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart(plot);
        PaintScaleLegend legend = new PaintScaleLegend(paintScale, new NumberAxis("Alive"));
        legend.setPosition(RectangleEdge.RIGHT);
        chart.addSubtitle(legend);

        File out = new File(dir, "generation" + gen + ".png");
        int width = Math.max(300, N * 5);
        int height = Math.max(300, N * 5);
        ChartUtils.saveChartAsPNG(out, chart, width, height);
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Please enter your own rules in s1,s2,birth format: ");
        String[] rules = sc.nextLine().split(",");
        System.out.print("Please provide size of the grid: ");
        int size = Integer.parseInt(sc.nextLine());
        System.out.print("Please provide number of generations: ");
        int gens = Integer.parseInt(sc.nextLine());
        System.out.print("Load config from file (1) or random (2)? ");
        int flag = Integer.parseInt(sc.nextLine());

        int s1 = Integer.parseInt(rules[0].trim());
        int s2 = Integer.parseInt(rules[1].trim());
        int birth = Integer.parseInt(rules[2].trim());

        Main game = new Main(size, gens, flag);
        game.play(s1, s2, birth);
        sc.close();
    }
}
