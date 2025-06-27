package aorg.example.worldBaeter;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WorldBaeter extends JavaPlugin {


    @Override
    public void onEnable() {
        // 注册命令
        getCommand("market").setExecutor(this);
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new MarketListener(), this);
    }

    @Override
    public void onDisable() {
        //hello
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("market")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家可以使用此命令!");
                return true;
            }

            Player player = (Player) sender;
            // 打开交易市场界面
            player.openInventory(MarketGUI.createMarketGUI());
            return true;
        }
        return false;
    }
}
