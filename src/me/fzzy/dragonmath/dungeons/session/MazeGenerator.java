package me.fzzy.dragonmath.dungeons.session;

import javafx.util.Pair;
import me.fzzy.dragonmath.util.Orientation;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Random;

public class MazeGenerator {

    private int xSize;
    private int zSize;

    private int startX;
    private int startZ;

    private Orientation[][] rooms;
    private ArrayList<Pair<Integer, Integer>> stack;

    private Pair<Integer, Integer> currentRoom;

    public MazeGenerator(int xSize, int zSize, int startX, int startZ) {
        this.xSize = xSize;
        this.zSize = zSize;
        this.startX = startX;
        this.startZ = startZ;
    }

    public void setSize(int x, int z) {
        this.xSize = x;
        this.zSize = z;
    }

    public Orientation[][] getRooms() {
        return rooms;
    }

    public int getxSize() {
        return xSize;
    }

    public int getzSize() {
        return zSize;
    }

    public Orientation[][] generate() {
        rooms = new Orientation[xSize][zSize];
        for (int x = 0; x < rooms.length; x++) {
            Orientation[] row = rooms[x];
            for (int z = 0; z < row.length; z++) {
                rooms[x][z] = new Orientation();
            }
        }
        stack = new ArrayList<>();
        currentRoom = new Pair<>(startX, startZ);
        while (stack.size() < xSize * zSize) {
            Pair<Integer, Integer> next = getRandomAdjacentNotVisitedRoom(currentRoom.getKey(), currentRoom.getValue());
            Orientation currentOrientation = rooms[currentRoom.getKey()][currentRoom.getValue()];

            if (!stack.contains(currentRoom))
                stack.add(currentRoom);
            if (next != null) {
                Orientation nextOrientation = rooms[next.getKey()][next.getValue()];
                if (isNorth(next, currentRoom)) {
                    currentOrientation.north(true);
                    nextOrientation.south(true);
                } else if (isEast(next, currentRoom)) {
                    currentOrientation.east(true);
                    nextOrientation.west(true);
                } else if (isSouth(next, currentRoom)) {
                    currentOrientation.south(true);
                    nextOrientation.north(true);
                } else if (isWest(next, currentRoom)) {
                    currentOrientation.west(true);
                    nextOrientation.east(true);
                }

                currentRoom = next;
            } else {
                goBack();
            }
        }
        return rooms;
    }

    public void goBack() {
        int amtBack = 0;
        while (getRandomAdjacentNotVisitedRoom(currentRoom.getKey(), currentRoom.getValue()) == null) {
            amtBack++;

            if (stack.size() - amtBack == 0) {
                //FINISHED
                break;
            }

            currentRoom = stack.get(stack.size() - amtBack);
        }
    }

    public ArrayList<Pair<Integer, Integer>> getStack() {
        return stack;
    }

    public boolean isNorth(Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        if (first.getKey().equals(second.getKey())) {
            if (first.getValue() < second.getValue())
                return true;
        }
        return false;
    }

    public boolean isEast(Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        if (first.getValue().equals(second.getValue())) {
            if (first.getKey() > second.getKey())
                return true;
        }
        return false;
    }

    public boolean isSouth(Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        if (first.getKey().equals(second.getKey())) {
            if (first.getValue() > second.getValue())
                return true;
        }
        return false;
    }

    public boolean isWest(Pair<Integer, Integer> first, Pair<Integer, Integer> second) {
        if (first.getValue().equals(second.getValue())) {
            if (first.getKey() < second.getKey())
                return true;
        }
        return false;
    }

    public Pair<Integer, Integer> getRandomAdjacentNotVisitedRoom(int x, int z) {
        Pair<Integer, Integer> northCheck = new Pair<>(x, z - 1);
        Pair<Integer, Integer> eastCheck = new Pair<>(x + 1, z);
        Pair<Integer, Integer> southCheck = new Pair<>(x, z + 1);
        Pair<Integer, Integer> westCheck = new Pair<>(x - 1, z);

        Orientation orientation = new Orientation();

        if (!stack.contains(northCheck) && z != 0)
            orientation.north(true);
        if (!stack.contains(eastCheck) && x != xSize - 1)
            orientation.east(true);
        if (!stack.contains(southCheck) && z != zSize - 1)
            orientation.south(true);
        if (!stack.contains(westCheck) && x != 0)
            orientation.west(true);

        if (orientation.count() == 0)
            return null;
        int random = new Random().nextInt(orientation.count());
        int count = 0;
        if (orientation.north()) {
            if (count == random)
                return new Pair<>(x, z - 1);
            count++;
        }
        if (orientation.east()) {
            if (count == random)
                return new Pair<>(x + 1, z);
            count++;
        }
        if (orientation.south()) {
            if (count == random)
                return new Pair<>(x, z + 1);
            count++;
        }
        if (orientation.west()) {
            if (count == random)
                return new Pair<>(x - 1, z);
        }
        return null;
    }

}
