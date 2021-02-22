package net.ufinator.position.commands;

import net.ufinator.position.Position;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PositionCMD implements CommandExecutor {

    private final File positionFile = new File(Position.INSTANCE.getDataFolder(), "position.yml");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        FileConfiguration config = YamlConfiguration.loadConfiguration(positionFile);
        try {
            if (args[0].equalsIgnoreCase("get")) {
                String name;
                try {
                    name = args[1].toLowerCase();
                } catch (ArrayIndexOutOfBoundsException e) {
                    sendMessage(player, "§cOne Parameter is missing! Use the Command correct: §7/position get {position name}");
                    return true;
                }
                String pos = config.getString("position." + args[1].toLowerCase());
                if (pos == null) {
                    sendMessage(player, "§cNo position was found with that name! §7(" + args[1] + ")");
                    return true;
                }
                String posx = config.getString("position." + name + ".x");
                String posy = config.getString("position." + name + ".y");
                String posz = config.getString("position." + name + ".z");
                sendMessage(player, "§b§lX: §r" + posx + " §b§lY: §r" + posy + " §b§lZ: §r" + posz + " §7(" + args[1] + ")");
                return true;
            } else if (args[0].equalsIgnoreCase("set")) {
                if (args.length <= 1) {
                    sendMessage(player, "§cOne parameter is missing! Use the command correct: §7/position set {position name}");
                    return true;
                }
                Location location = player.getLocation();
                String available = config.getString("position." + args[1].toLowerCase());
                int locx = (int) Math.floor(location.getX());
                int locy = (int) Math.floor(location.getY());
                int locz = (int) Math.floor(location.getZ());
                config.set("position." + args[1].toLowerCase() + ".x", locx);
                config.set("position." + args[1].toLowerCase() + ".y", locy);
                config.set("position." + args[1].toLowerCase() + ".z", locz);
                try {
                    config.save(positionFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (available == null) {
                    Bukkit.broadcastMessage(Position.PREFIX + "§7New position! §bName: §r" + args[1].toLowerCase() + " §6X: §r" + locx + " §6Y: §r" + locy + " §6Z: §r" + locz + " §7(" + player.getName() + ")");
                } else {
                    Bukkit.broadcastMessage(Position.PREFIX + "§7Position changed! §bName: §r" + args[1].toLowerCase() + " §6X: §r" + locx + " §6Y: §r" + locy + " §6Z: §r" + locz + " §7(" + player.getName() + ")");
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                try {
                    ArrayList<String> list = new ArrayList<>(Objects.requireNonNull(config.getConfigurationSection("position")).getKeys(false));
                    String newList = String.join("§r, §6", list);
                    if (newList.equals("")) {
                        sendMessage(player, "§cThere are no positions saved!");
                    } else {
                        sendMessage(player, "§aAvailable positions: §6" + newList);
                    }
                } catch (NullPointerException e) {
                    sendMessage(player, "§cThere are no positions saved!");
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length <= 1) {
                    sendMessage(player, "§cOne Parameter is missing! Use the command correct: §7/position remove {position name}");
                    return true;
                }
                String posName = config.getString("position." + args[1].toLowerCase());
                if (posName == null){
                    sendMessage(player, "§cNo position was found with this name! §7(" + args[1] + ")");
                    return true;
                }
                config.set("position." + args[1].toLowerCase(), null);
                try {
                    config.save(positionFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMessage(player, "§6" + args[1] + " §ahas been successfully deleted!");
            } else {
                sendMessage(player, "§cParameter not found! §8(" + args[0] + ")§c Use the command correct: §7/position {get/set/remove/list} [Value 2]");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            sendMessage(player, "§cOne parameter is missing! Use the command correct: §7/position {get/set/remove/list} [Value 2]");
            return true;
        }
        return true;
    }
    public void sendMessage(Player player, String message) {
        player.sendMessage(Position.PREFIX + message);
    }
}
