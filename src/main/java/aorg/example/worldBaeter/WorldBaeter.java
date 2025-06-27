package aorg.example.worldBaeter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldBaeter extends JavaPlugin {
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        getLogger().info("交易市场插件已启用!");

        // 初始化交易管理器
        tradeManager = new TradeManager(this);

        // 注册命令
        getCommand("market").setExecutor(this);
        getCommand("addtrade").setExecutor(this);

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new MarketListener(tradeManager), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("交易市场插件已禁用");
        tradeManager.saveTrades(); // 保存所有交易
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
            player.openInventory(MarketGUI.createMarketGUI(tradeManager));
            return true;

        } else if (cmd.getName().equalsIgnoreCase("addtrade")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只有玩家可以使用此命令!");
                return true;
            }

            Player player = (Player) sender;

            // 检查权限
            if (!player.hasPermission("worldbaeter.market.add")) {
                player.sendMessage("§c你没有权限发布交易!");
                return true;
            }

            // 打开交易发布界面
            player.openInventory(TradeCreationGUI.createTradeCreationGUI());
            return true;
        }
        return false;
    }
}