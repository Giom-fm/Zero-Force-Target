package main.net;

public class Pos2D {
    private int x, y;

    public Pos2D(Pos2D pos) {
        this.x = pos.getX();
        this.y = pos.getY();
    }

    public Pos2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return Integer
                .parseInt(Integer.toString(this.getX()) + Integer.toString(prime) + Integer.toString(this.getY()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Pos2D) {
            Pos2D pos = (Pos2D) obj;
            return pos.getX() == this.x && pos.getY() == this.y;
        }
        return false;
    }

}
