package org.example.homework1;

public class Main {
    public static void calculateSin(double x, boolean isInRadian) {
        if (!isInRadian) x = Math.toRadians(x);

        x = x % (2 * Math.PI);

        if (x > Math.PI) x -= 2 * Math.PI;
        if (x < -Math.PI) x += 2 * Math.PI;

        double result = x;
        double term = x;
        double denominator = 1;

        for (int i = 1; i < 5; i++) {
            term *= -x * x;
            denominator *= (2 * i) * (2 * i + 1);
            result += term / denominator;
        }

        System.out.println("Result of calculateSin(): sin(x) ≈ " + result);
        System.out.println("Result of Math.sin()    : sin(x) ≈ " + Math.sin(x));
    }

    public static void main(String[] args) {
        calculateSin(87,true);
    }
}
