package aorg.example.worldBaeter;

import org.bukkit.ChatColor;
import org.bukkit.Material; // 添加Material导入
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MarketListener implements Listener {
    private final TradeManager tradeManager;

    public MarketListener(TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查是否是交易市场界面
        if (event.getView().getTitle().equals(MarketGUI.GUI_TITLE)) {
            handleMarketClick(event);
        }
        // 检查是否是交易创建界面
        else if (event.getView().getTitle().equals(TradeCreationGUI.GUI_TITLE)) {
            handleTradeCreationClick(event);
        }
    }

    private void handleMarketClick(InventoryClickEvent event) {
        event.setCancelled(true); // 防止玩家移动物品

        // 只处理玩家点击
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // 只处理有效区域（10-44是交易区域）
        if (slot < 10 || slot > 44) {
            return;
        }

        // 获取点击的物品
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        // 查找对应的交易
        TradeManager.Trade selectedTrade = findTradeByDisplayItem(clickedItem);
        if (selectedTrade == null) {
            player.sendMessage(ChatColor.RED + "错误: 找不到对应的交易!");
            return;
        }

        // 检查玩家是否有足够的物品购买
        ItemStack priceItem = selectedTrade.getPriceItem();
        if (priceItem == null) {
            player.sendMessage(ChatColor.RED + "错误: 交易价格无效!");
            return;
        }

        if (!player.getInventory().containsAtLeast(priceItem, priceItem.getAmount())) {
            player.sendMessage(ChatColor.RED + "你没有足够的物品购买此交易!");
            return;
        }

        // 执行交易
        player.getInventory().removeItem(priceItem);
        player.getInventory().addItem(selectedTrade.getOfferItem().clone());

        // 通知玩家
        player.sendMessage(ChatColor.GREEN + "成功购买交易! 获得了 " +
                selectedTrade.getOfferItem().getAmount() + "x " +
                MarketGUI.getItemName(selectedTrade.getOfferItem()));

        // 移除交易（可选）
        tradeManager.removeTrade(selectedTrade);
        player.closeInventory();
    }

    private void handleTradeCreationClick(InventoryClickEvent event) {
        // 防止玩家移动背景物品
        if (event.getRawSlot() != TradeCreationGUI.OFFER_SLOT &&
                event.getRawSlot() != TradeCreationGUI.PRICE_SLOT &&
                event.getRawSlot() != TradeCreationGUI.CONFIRM_SLOT) {
            event.setCancelled(true);
        }

        // 只处理玩家点击
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // 处理确认按钮点击
        if (slot == TradeCreationGUI.CONFIRM_SLOT) {
            Inventory inv = event.getInventory();
            ItemStack offerItem = inv.getItem(TradeCreationGUI.OFFER_SLOT);
            ItemStack priceItem = inv.getItem(TradeCreationGUI.PRICE_SLOT);

            // 验证物品
            if (offerItem == null || offerItem.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "请提供要交易的物品!");
                return;
            }

            if (priceItem == null || priceItem.getType() == Material.AIR) {
                player.sendMessage(ChatColor.RED + "请设置交易要求的报酬!");
                return;
            }

            // 检查玩家是否有足够的报酬物品
            if (!player.getInventory().containsAtLeast(priceItem, priceItem.getAmount())) {
                player.sendMessage(ChatColor.RED + "你没有足够的物品支付报酬!");
                return;
            }

            // 创建交易
            TradeManager.Trade trade = new TradeManager.Trade(
                    player.getUniqueId(),
                    offerItem.clone(),
                    priceItem.clone()
            );

            // 添加交易到市场
            tradeManager.addTrade(trade);

            // 扣除报酬物品
            player.getInventory().removeItem(priceItem);

            player.sendMessage(ChatColor.GREEN + "交易发布成功!");
            player.closeInventory();
        }
    }

    private TradeManager.Trade findTradeByDisplayItem(ItemStack displayItem) {
        for (TradeManager.Trade trade : tradeManager.getTrades()) {
            if (trade.getOfferItem().isSimilar(displayItem)) {
                return trade;
            }
        }
        return null;
    }

    @EventHandler
    public void onTradeCreationClose(InventoryCloseEvent event) {
        if (!event.getView().getTitle().equals(TradeCreationGUI.GUI_TITLE)) {
            return;
        }

        // 只处理玩家
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        // 返还玩家在界面中放置的物品
        ItemStack offerItem = inv.getItem(TradeCreationGUI.OFFER_SLOT);
        if (offerItem != null && offerItem.getType() != Material.AIR) {
            player.getInventory().addItem(offerItem);
        }

        ItemStack priceItem = inv.getItem(TradeCreationGUI.PRICE_SLOT);
        if (priceItem != null && priceItem.getType() != Material.AIR) {
            player.getInventory().addItem(priceItem);
        }
    }
}