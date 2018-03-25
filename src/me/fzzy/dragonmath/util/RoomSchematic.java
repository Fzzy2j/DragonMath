package me.fzzy.dragonmath.util;

import java.io.*;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NBTOutputStream;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.SchematicReader;
import com.sk89q.worldedit.extent.clipboard.io.SchematicWriter;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.operation.RunContext;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import me.fzzy.dragonmath.DragonMath;
import me.fzzy.dragonmath.dungeons.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RoomSchematic {

    private File file;

    public RoomSchematic(File file) {
        this.file = file;
    }

    public Clipboard getClipboard(WorldData data) {
        /*SchematicFormat schematic = SchematicFormat.getFormat(file);
        try {
            return schematic.load(file);
        } catch (IOException | DataException e) {
            e.printStackTrace();
        }*/

        try {
            return ClipboardFormat.SCHEMATIC.load(file).getClipboard();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            FileInputStream os = new FileInputStream(file.getAbsolutePath());
            SchematicReader reader = new SchematicReader(new NBTInputStream(os));
            return reader.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    public static Clipboard getClipboard(Player player) {
        try {
            return DragonMath.we.getSession(player).getClipboard().getClipboard();
        } catch (EmptyClipboardException e) {
            e.printStackTrace();
        }
        return null;
       /* WorldEdit we = DragonMath.we.getWorldEdit();

        LocalPlayer localPlayer = DragonMath.we.wrapPlayer(player);
        LocalSession localSession = we.getSession(localPlayer);
        ClipboardHolder selection = null;
        try {
            selection = localSession.getClipboard();
        } catch (EmptyClipboardException e) {
            e.printStackTrace();
        }
        EditSession editSession = localSession.createEditSession(localPlayer);

        Vector min = selection.getClipboard().getMinimumPoint();
        Vector max = selection.getClipboard().getMaximumPoint();

        editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(editSession);
        return clipboard;*/
    }

    public boolean delete() {
        return file.delete();
    }

    public boolean exists() {
        return file.exists();
    }

    public EditSession paste(Location pasteLoc, int yRotate) {
        try {
            //FileInputStream os = new FileInputStream(file.getAbsolutePath());
            //SchematicReader reader = new SchematicReader(new NBTInputStream(os));
            //WorldData worldData = new BukkitWorld(pasteLoc.getWorld()).getWorldData();
            Schematic schem = ClipboardFormat.SCHEMATIC.load(file);
            Clipboard clipboard = schem.getClipboard();

            if (clipboard != null) {
                if (yRotate == 0)
                    clipboard.setOrigin(clipboard.getMinimumPoint());
                else if (yRotate == 90)
                    clipboard.setOrigin(clipboard.getMinimumPoint().add(clipboard.getDimensions().getBlockX() - 1, 0, 0));
                else if (yRotate == 180)
                    clipboard.setOrigin(clipboard.getMinimumPoint().add(clipboard.getDimensions().getBlockX() - 1, 0, clipboard.getDimensions().getBlockZ() - 1));
                else if (yRotate == 270)
                    clipboard.setOrigin(clipboard.getMinimumPoint().add(0, 0, clipboard.getDimensions().getBlockZ() - 1));
                Vector to = new Vector(pasteLoc.getBlockX(), pasteLoc.getBlockY(), pasteLoc.getBlockZ());

                //ClipboardHolder holder = new ClipboardHolder(clipboard, worldData);
                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(yRotate);
                return schem.paste(new BukkitWorld(pasteLoc.getWorld()), to, false, true, transform);
                //holder.setTransform(holder.getTransform().combine(transform));

                //EditSession es = new EditSession(new BukkitWorld(pasteLoc.getWorld()), Integer.MAX_VALUE);

                /*Operation op = holder.createPaste(es, es.getWorld().getWorldData()).ignoreAirBlocks(false).to(to).build();
                //ForwardExtentCopy op = new ForwardExtentCopy(clipboard, clipboard.getRegion(), clipboard.getRegion().getMinimumPoint(), new BukkitWorld(pasteLoc.getWorld()), to);
                //ForwardExtentCopy op = (ForwardExtentCopy) holder.createPaste(clipboard, DragonMath.worldData).to(to).build();
                try {
                    Operations.complete(op);
                } catch (WorldEditException e) {
                    e.printStackTrace();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            String path = DragonMath.instance.getDataFolder().getAbsolutePath() + File.separator + room.getDungeon().getName() + File.separator;
            if (room.getType() == RoomType.STANDARD) {
                path += "rooms" + File.separator + room.getSchematicName();
            }
            if (room.getType() == RoomType.END) {
                path += "endRooms" + File.separator + room.getSchematicName();
            }
            File dir = new File(path);

            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
            editSession.enableQueue();

            SchematicFormat schematic = SchematicFormat.getFormat(file);
            CuboidClipboard clipboard = schematic.load(file);

            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), true);
            editSession.flushQueue();
        } catch (DataException | IOException | MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }*/
        return null;
    }

    public void save(Player player, Dungeon dungeon) {
            /*String path = DragonMath.instance.getDataFolder() + File.separator + dungeon.getName() + File.separator + "rooms";
            File schematic = new File(path + File.separator + "");
            File dir = new File(path);*/

        LocalSession ls = DragonMath.we.getSession(player);
        try {
            CuboidRegion region = new CuboidRegion(ls.getSelectionWorld(), ls.getWorldSelection().getMinimumPoint(), ls.getWorldSelection().getMaximumPoint());
            try {
                new Schematic(region).save(file, ClipboardFormat.SCHEMATIC);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }

        /*LocalSession ls = DragonMath.we.getSession(player);
        try {
            FileOutputStream os = new FileOutputStream(file.getAbsolutePath());
            SchematicWriter writer = new SchematicWriter(new NBTOutputStream(os));
            writer.write(ls.getClipboard().getClipboard(), new BukkitWorld(player.getWorld()).getWorldData());
        } catch (IOException | EmptyClipboardException e) {
            e.printStackTrace();
        }*/

            /*WorldEdit we = DragonMath.we.getWorldEdit();

            LocalPlayer localPlayer = DragonMath.we.wrapPlayer(player);
            LocalSession localSession = we.getSession(localPlayer);
            ClipboardHolder selection = localSession.getClipboard();
            EditSession editSession = localSession.createEditSession(localPlayer);

            Vector min = selection.getClipboard().getMinimumPoint();
            Vector max = selection.getClipboard().getMaximumPoint();

            editSession.enableQueue();
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            clipboard.copy(editSession);
            SchematicFormat.MCEDIT.save(clipboard, file);
            editSession.flushQueue();*/
    }

}