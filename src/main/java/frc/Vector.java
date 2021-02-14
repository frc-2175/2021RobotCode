package frc;

public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector subtract(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public Vector rotate(double theta) {
        double newX = (x * Math.cos(Math.toRadians(theta))) - (y * Math.sin(Math.toRadians(theta)));
        double newY = (x * Math.sin(Math.toRadians(theta))) + (y * Math.cos(Math.toRadians(theta)));
        return new Vector(newX, newY);
    }

    public Vector copy() {
        return new Vector(x, y);
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }
}
