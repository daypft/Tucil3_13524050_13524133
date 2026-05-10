package backend;

public class Tile { 
    enum Type {
        P,
        X,
        L,
        O,
        Z
    }

    public int row, col;
    public Type type;
    public int order = -1;
    public int cost;
    public boolean hasBeenPassed;
    public Tile right, left, up, down;
    public Tile previousOrder, nextOrder;

    public Tile(int row, int col, Type type) {
        this.row = row;
        this.col = col;
        this.type = type;
        this.hasBeenPassed = false;
    }

    public Boolean canEnter() {
        if (order != -1 && !hasBeenPassed) {
            return hasPassedPrevious();
        }
        return true;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setRight(Tile right) {
        this.right = right;
    }

    public void setLeft(Tile left) {
        this.left = left;
    }

    public void setUp(Tile up) {
        this.up = up;
    }

    public void setDown(Tile down) {
        this.down = down;
    }

    public void setPreviousOrder(Tile previousOrder) {
        this.previousOrder = previousOrder;
    }

    public void setNextOrder(Tile nextOrder) {
        this.nextOrder = nextOrder;
    }

    public boolean hasPassedPrevious() {
        if (order == 0 || order == -1) {
            return true;
        }
        return previousOrder != null && previousOrder.hasBeenPassed;
    }


    public Tile nextRight() { return right; }
    public Tile nextLeft() { return left; }
    public Tile nextUp() { return up; }
    public Tile nextDown() { return down; }

    public boolean isWall() { return type == Type.X; }
    public boolean isLava() { return type == Type.L; }
    public boolean isGoal() { return type == Type.O; }
    public boolean isStart() { return type == Type.Z; }
    public boolean isPath() { return type == Type.P; }

    
}
