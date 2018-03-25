package me.fzzy.dragonmath.util;

import java.util.Random;

public class Orientation {

    private boolean[] directions = {false, false, false, false};

    public Orientation(boolean north, boolean east, boolean south, boolean west) {
        directions[0] = north;
        directions[1] = east;
        directions[2] = south;
        directions[3] = west;
    }

    public Orientation() {
        this(false, false, false, false);
    }

    private int totalRotated = 0;

    public Orientation rotateNinety() {
        boolean north = north();
        boolean east = east();
        boolean south = south();
        boolean west = west();
        north(east);
        east(south);
        south(west);
        west(north);
        totalRotated += 90;
        return this;
    }

    public void resetRotation() {
        while (totalRotated > 0) {
            boolean north = north();
            boolean east = east();
            boolean south = south();
            boolean west = west();
            north(west);
            east(north);
            south(east);
            west(south);
            totalRotated -= 90;
        }
    }

    public boolean equals(Orientation orientation) {
        if (north() != orientation.north())
            return false;
        if (east() != orientation.east())
            return false;
        if (south() != orientation.south())
            return false;
        if (west() != orientation.west())
            return false;
        return true;
    }

    public int getTotalRotated() {
        return totalRotated;
    }

    public boolean north() {
        return directions[0];
    }

    public boolean east() {
        return directions[1];
    }

    public boolean south() {
        return directions[2];
    }

    public boolean west() {
        return directions[3];
    }

    public void north(boolean set) {
        directions[0] = set;
    }

    public void east(boolean set) {
        directions[1] = set;
    }

    public void south(boolean set) {
        directions[2] = set;
    }

    public void west(boolean set) {
        directions[3] = set;
    }

    public int count() {
        int amtDoors = 0;
        if (north())
            amtDoors++;
        if (east())
            amtDoors++;
        if (south())
            amtDoors++;
        if (west())
            amtDoors++;
        return amtDoors;
    }

    public boolean hasAdjacent() {
        for (int i = 0; i < 4; i++) {

            //previous
            int p = (i - 1) == -1 ? 3 : (i - 1);

            //next
            int n = (i + 1) % 4;

            if ((directions[i] && directions[p]) || (directions[i] && directions[n])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String output = "";
        if (north())
            output += "north:true";
        else
            output += "north:false";
        if (east())
            output += "east:true";
        else
            output += "east:false";
        if (south())
            output += "south:true";
        else
            output += "south:false";
        if (west())
            output += "west:true";
        else
            output += "west:false";
        return output;
    }

}
