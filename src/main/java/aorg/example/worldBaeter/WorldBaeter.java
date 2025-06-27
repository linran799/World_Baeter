package aorg.example.worldBaeter;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
        getServer().getPluginManager().registerEvents(new MarketListener(tradeManager, mailbox), this);
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
                if (args.length < 2) {
                    player.sendMessage("§c用法: /addtrade require:物品ID:数量 reward:物品ID:数量");
                    player.sendMessage("§e示例: /addtrade require:stone:20 reward:diamond:1");
                    return true;
                }

                try {
                    // 解析需求物
                    String[] requireParts = args[0].split(":");
                    Material requireMaterial = Material.valueOf(requireParts[1].toUpperCase());
                    int requireAmount = Integer.parseInt(requireParts[2]);

                    // 解析报酬
                    String[] rewardParts = args[1].split(":");
                    Material rewardMaterial = Material.valueOf(rewardParts[1].toUpperCase());
                    int rewardAmount = Integer.parseInt(rewardParts[2]);

                    // 创建物品
                    ItemStack requireItem = new ItemStack(requireMaterial, requireAmount);
                    ItemStack rewardItem = new ItemStack(rewardMaterial, rewardAmount);

                    // 创建交易
                    TradeManager.Trade trade = new TradeManager.Trade(
                            player.getUniqueId(),
                            requireItem,
                            rewardItem
                    );

                    // 添加到市场
                    tradeManager.addTrade(trade);
                    player.sendMessage("§a交易发布成功!");
                } catch (Exception e) {
                    player.sendMessage("§c交易创建失败: " + e.getMessage());
                    player.sendMessage("§e正确格式: /addtrade require:物品ID:数量 reward:物品ID:数量");
                    player.sendMessage("§e示例: /addtrade require:stone:20 reward:diamond:1");
                }
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