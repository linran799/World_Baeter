package aorg.example.worldBaeter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class WorldBaeter extends JavaPlugin {
    private TradeManager tradeManager;
    private PlayerMailbox mailbox;

    @Override
    public void onEnable() {
        getLogger().info("交易市场插件已启用!");

        // 初始化交易管理器和邮箱系统
        tradeManager = new TradeManager(this);
        mailbox = new PlayerMailbox(this);

        // 注册命令
        getCommand("market").setExecutor(this);
        getCommand("addtrade").setExecutor(this);
        getCommand("mail").setExecutor(this);

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new TradeListener(tradeManager, mailbox), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("交易市场插件已禁用");
        tradeManager.saveTrades();
        mailbox.saveMailboxData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c只有玩家可以使用此命令!");
            return true;
        }

        Player player = (Player) sender;

        switch (cmd.getName().toLowerCase()) {
            case "market":
                player.openInventory(MarketGUI.createMarketGUI(tradeManager));
                return true;

            case "addtrade":
                if (!player.hasPermission("worldbaeter.market.add")) {
                    player.sendMessage("§c你没有权限发布交易!");
                    return true;
                }
                player.openInventory(TradeStep1GUI.createTradeStep1GUI());
                return true;

            case "mail":
                player.openInventory(MailGUI.createMailGUI(mailbox, player.getUniqueId()));
                return true;
        }
        return false;
    }

    public PlayerMailbox getMailbox() {
        return mailbox;
    }
}