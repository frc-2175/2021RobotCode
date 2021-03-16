package frc.math;

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

    public Vector multiply(double a) {
        return new Vector (x*a, y*a); 
    }

    public Vector divide(double a) {
        return new Vector (x/a, y/a); 
    }

    public double magnitude() {
        return Math.sqrt(x*x + y*y);
    }

    public Vector normalized() {
        return divide(magnitude());
    }

    public Vector rotate(double theta) {
        double newX = (x * Math.cos(Math.toRadians(theta))) - (y * Math.sin(Math.toRadians(theta)));
        double newY = (x * Math.sin(Math.toRadians(theta))) + (y * Math.cos(Math.toRadians(theta)));
        return new Vector(newX, newY);
    }

    public Vector copy() {
        return new Vector(x, y);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }

        Vector other = (Vector) obj;

        return Math.abs(other.x - x) < 0.01 && Math.abs(other.y - y) < 0.01;
    }

    public String toString() {
        return x + ", " + y;
    }
}