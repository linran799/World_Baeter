package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class MarketListener implements Listener {
    private final TradeManager tradeManager;
    private final PlayerMailbox mailbox;

    public MarketListener(TradeManager tradeManager, PlayerMailbox mailbox) {
        this.tradeManager = tradeManager;
        this.mailbox = mailbox;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 市场界面
        if (event.getView().getTitle().equals(MarketGUI.GUI_TITLE)) {
            handleMarketClick(event);
        }
        // 邮箱界面
        else if (event.getView().getTitle().equals(MailGUI.GUI_TITLE)) {
            handleMailClick(event);
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

    private TradeManager.Trade findTradeByDisplayItem(ItemStack displayItem) {
        for (TradeManager.Trade trade : tradeManager.getTrades()) {
            if (trade.getRequiredItem() != null &&
                    trade.getRequiredItem().isSimilar(displayItem)) {
                return trade;
            }
        }
        return null;
    }
}