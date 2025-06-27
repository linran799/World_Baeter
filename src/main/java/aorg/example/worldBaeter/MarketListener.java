package aorg.example.worldBaeter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TradeListener implements Listener {
    private final TradeManager tradeManager;
    private final PlayerMailbox mailbox;

    // 存储玩家交易状态
    private final Map<UUID, List<ItemStack>> tradeInProgress = new HashMap<>();

    public TradeListener(TradeManager tradeManager, PlayerMailbox mailbox) {
        this.tradeManager = tradeManager;
        this.mailbox = mailbox;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 第一步：选择所需物品
        if (event.getView().getTitle().equals(TradeStep1GUI.GUI_TITLE)) {
            handleTradeStep1Click(event);
        }
        // 第二步：设置报酬
        else if (event.getView().getTitle().equals(TradeStep2GUI.GUI_TITLE)) {
            handleTradeStep2Click(event);
        }
        // 市场界面
        else if (event.getView().getTitle().equals(MarketGUI.GUI_TITLE)) {
            handleMarketClick(event);
        }
        // 邮箱界面
        else if (event.getView().getTitle().equals(MailGUI.GUI_TITLE)) {
            handleMailClick(event);
        }
    }

    private void handleTradeStep1Click(InventoryClickEvent event) {
        // 允许玩家在界面中放置物品
        if (event.getRawSlot() < 45) {
            event.setCancelled(false);
            return;
        }

        // 确认按钮点击
        if (event.getRawSlot() == TradeStep1GUI.CONFIRM_SLOT) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getInventory();

            // 收集所需物品
            List<ItemStack> requiredItems = new ArrayList<>();
            for (int i = 0; i < 45; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    requiredItems.add(item.clone());
                }
            }

            if (requiredItems.isEmpty()) {
                player.sendMessage(ChatColor.RED + "请添加至少一个所需物品!");
                return;
            }

            // 保存到临时状态
            tradeInProgress.put(player.getUniqueId(), requiredItems);

            // 打开第二步界面
            player.openInventory(TradeStep2GUI.createTradeStep2GUI(requiredItems));
        }
    }

    private void handleTradeStep2Click(InventoryClickEvent event) {
        // 允许玩家在界面中放置物品
        if (event.getRawSlot() >= 9 && event.getRawSlot() < 45) {
            event.setCancelled(false);
            return;
        }

        // 确认按钮点击
        if (event.getRawSlot() == TradeStep2GUI.CONFIRM_SLOT) {
            Player player = (Player) event.getWhoClicked();
            UUID playerId = player.getUniqueId();

            // 获取所需物品
            List<ItemStack> requiredItems = tradeInProgress.get(playerId);
            if (requiredItems == null || requiredItems.isEmpty()) {
                player.sendMessage(ChatColor.RED + "错误: 交易数据丢失!");
                return;
            }

            Inventory inv = event.getInventory();
            List<ItemStack> rewardItems = new ArrayList<>();

            // 收集报酬物品 (槽位 9-44)
            for (int i = 9; i < 45; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    rewardItems.add(item.clone());
                }
            }

            if (rewardItems.isEmpty()) {
                player.sendMessage(ChatColor.RED + "请设置至少一个报酬物品!");
                return;
            }

            // 检查玩家是否有足够的报酬物品
            for (ItemStack reward : rewardItems) {
                if (!player.getInventory().containsAtLeast(reward, reward.getAmount())) {
                    player.sendMessage(ChatColor.RED + "你没有足够的物品支付报酬: " +
                            reward.getAmount() + "x " + reward.getType());
                    return;
                }
            }

            // 扣除报酬物品
            for (ItemStack reward : rewardItems) {
                player.getInventory().removeItem(reward);
            }

            // 创建交易（简化：只使用第一个所需物品和第一个报酬物品）
            TradeManager.Trade trade = new TradeManager.Trade(
                    playerId,
                    requiredItems.get(0).clone(),
                    rewardItems.get(0).clone()
            );

            // 添加交易到市场
            tradeManager.addTrade(trade);

            player.sendMessage(ChatColor.GREEN + "交易发布成功!");
            player.closeInventory();
            tradeInProgress.remove(playerId);
        }
    }

    private void handleMarketClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // 只处理交易区域
        if (slot < 10 || slot > 44) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        // 查找交易
        TradeManager.Trade selectedTrade = findTradeByDisplayItem(clickedItem);
        if (selectedTrade == null) {
            player.sendMessage(ChatColor.RED + "错误: 找不到对应的交易!");
            return;
        }

        // 检查玩家是否有足够的所需物品
        ItemStack requiredItem = selectedTrade.getRequiredItem();
        if (!player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
            player.sendMessage(ChatColor.RED + "你没有足够的物品完成此交易!");
            return;
        }

        // 执行交易
        player.getInventory().removeItem(requiredItem);

        // 通过邮箱发送物品
        Player creator = Bukkit.getPlayer(selectedTrade.getCreator());
        String creatorName = creator != null ? creator.getName() : "未知玩家";

        // 给买家发送报酬
        mailbox.sendMail(
                player.getUniqueId(),
                creatorName,
                selectedTrade.getRewardItem().clone()
        );

        // 给卖家发送所需物品
        mailbox.sendMail(
                selectedTrade.getCreator(),
                player.getName(),
                requiredItem.clone()
        );

        player.sendMessage(ChatColor.GREEN + "交易完成! 报酬已发送到你的邮箱");

        // 通知卖家
        if (creator != null) {
            creator.sendMessage(ChatColor.GREEN + player.getName() + " 完成了你的交易! 物品已发送到邮箱");
        }

        // 移除交易
        tradeManager.removeTrade(selectedTrade);
        player.closeInventory();
    }

    private void handleMailClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // 只处理邮件区域
        if (slot < 10 || slot > 44) {
            return;
        }

        // 计算邮件索引
        int mailIndex = (slot - 10) % 9 + ((slot - 10) / 9) * 7;
        List<PlayerMailbox.MailItem> mails = mailbox.getMails(player.getUniqueId());

        if (mailIndex >= mails.size()) {
            return;
        }

        PlayerMailbox.MailItem mail = mails.get(mailIndex);

        // 尝试添加物品到玩家背包
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(mail.getItem());

        if (leftover.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "已领取邮件物品");
            mailbox.removeMail(player.getUniqueId(), mailIndex);
            player.closeInventory();
        } else {
            player.sendMessage(ChatColor.RED + "背包空间不足，无法领取物品!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // 第一步界面关闭处理
        if (event.getView().getTitle().equals(TradeStep1GUI.GUI_TITLE)) {
            Player player = (Player) event.getPlayer();
            Inventory inv = event.getInventory();

            // 返还所有物品
            for (int i = 0; i < 45; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().addItem(item);
                }
            }

            tradeInProgress.remove(player.getUniqueId());
        }

        // 第二步界面关闭处理
        else if (event.getView().getTitle().equals(TradeStep2GUI.GUI_TITLE)) {
            Player player = (Player) event.getPlayer();
            Inventory inv = event.getInventory();

            // 返还报酬物品
            for (int i = 9; i < 45; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    player.getInventory().addItem(item);
                }
            }

            // 返还所需物品（从临时状态）
            List<ItemStack> requiredItems = tradeInProgress.get(player.getUniqueId());
            if (requiredItems != null) {
                for (ItemStack item : requiredItems) {
                    player.getInventory().addItem(item);
                }
                tradeInProgress.remove(player.getUniqueId());
            }
        }
    }

    private TradeManager.Trade findTradeByDisplayItem(ItemStack displayItem) {
        for (TradeManager.Trade trade : tradeManager.getTrades()) {
            if (trade.getRequiredItem().isSimilar(displayItem)) {
                return trade;
            }
        }
        return null;
    }
}