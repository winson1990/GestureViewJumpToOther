package com.zrf.testgesturev;

public class MoveInfo implements Comparable<Object>{
    
    private Float moveX;
    private Float moveY;
    
    public MoveInfo(Float moveX, Float moveY) {
        this.moveX = moveX;
        this.moveY = moveY;
    }

    @Override
    public String toString() {
        return "moveInfo [moveX=" + moveX + ", moveY=" + moveY + "]";
    }

    public float getMoveX() {
        return moveX;
    }
    public void setMoveX(float moveX) {
        this.moveX = moveX;
    }
    public float getMoveY() {
        return moveY;
    }
    public void setMoveY(float moveY) {
        this.moveY = moveY;
    }

    @Override
    public int compareTo(Object o) {
        MoveInfo moveInfo=(MoveInfo)o; 
        return this.moveY.compareTo(moveInfo.getMoveY());
    }
}
