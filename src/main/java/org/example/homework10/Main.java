package org.example.homework10;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {
    private static final int GRID_SIZE = 100;
    private final boolean[][] cellsAlive = new boolean[GRID_SIZE][GRID_SIZE];
    private final JLabel[][] cellLabels = new JLabel[GRID_SIZE][GRID_SIZE];
    private final Random random = new Random();
    private final Color aliveColor = new Color(144, 238, 144);

    private int underpopulationThreshold = 2;
    private int overpopulationThreshold = 3;
    private int reproductionThreshold = 3;

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1,  0},           {1,  0},
            {-1,  1}, {0,  1}, {1,  1}
    };

    public Main() {
        super("Game of Life");
        initializeCells();
        setupUI();
        startSimulation();
    }

    private void initializeCells() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                cellsAlive[y][x] = random.nextDouble() < 0.30;
            }
        }
    }

    private void setupUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel control = new JPanel(new GridBagLayout());
        control.setBackground(Color.WHITE);
        control.setPreferredSize(new Dimension(800, 100));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField underField = new JTextField(String.valueOf(underpopulationThreshold), 3);
        JTextField overField = new JTextField(String.valueOf(overpopulationThreshold), 3);
        JTextField replField  = new JTextField(String.valueOf(reproductionThreshold), 3);
        JButton applyBtn = new JButton("Apply Rules");
        applyBtn.addActionListener(_ -> {
            try {
                underpopulationThreshold = Integer.parseInt(underField.getText());
                overpopulationThreshold = Integer.parseInt(overField.getText());
                reproductionThreshold = Integer.parseInt(replField.getText());
                JOptionPane.showMessageDialog(this, "Rules updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; control.add(new JLabel("Underpopulation:"), gbc);
        gbc.gridx = 1; control.add(underField, gbc);
        gbc.gridx = 2; control.add(new JLabel("Overpopulation:"), gbc);
        gbc.gridx = 3; control.add(overField, gbc);
        gbc.gridx = 4; control.add(new JLabel("Reproduction:"), gbc);
        gbc.gridx = 5; control.add(replField, gbc);
        gbc.gridx = 6; gbc.gridwidth = 2; control.add(applyBtn, gbc);

        add(control, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setPreferredSize(new Dimension(800, 800));
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                JLabel cell = new JLabel();
                cell.setOpaque(true);
                cell.setBackground(cellsAlive[y][x] ? aliveColor : Color.BLACK);
                cellLabels[y][x] = cell;
                gridPanel.add(cell);
            }
        }
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startSimulation() {
        Timer timer = new Timer(100, _ -> {
            stepEvolution();
            updateUI();
        });
        timer.start();
    }

    private void stepEvolution() {
        boolean[][] next = new boolean[GRID_SIZE][GRID_SIZE];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                int aliveCount = countAliveNeighbors(x, y);
                next[y][x] = willBeAlive(cellsAlive[y][x], aliveCount);
            }
        }
        for (int y = 0; y < GRID_SIZE; y++) {
            System.arraycopy(next[y], 0, cellsAlive[y], 0, GRID_SIZE);
        }
    }

    private int countAliveNeighbors(int x, int y) {
        int count = 0;
        for (int[] d : DIRECTIONS) {
            int nx = (x + d[0] + GRID_SIZE) % GRID_SIZE;
            int ny = (y + d[1] + GRID_SIZE) % GRID_SIZE;
            if (cellsAlive[ny][nx]) count++;
        }
        return count;
    }

    private boolean willBeAlive(boolean alive, int neighbors) {
        if (alive) {
            if (neighbors < underpopulationThreshold) return false;
            return neighbors <= overpopulationThreshold;
        } else {
            return neighbors == reproductionThreshold;
        }
    }

    private void updateUI() {
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                cellLabels[y][x].setBackground(cellsAlive[y][x] ? aliveColor : Color.BLACK);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}