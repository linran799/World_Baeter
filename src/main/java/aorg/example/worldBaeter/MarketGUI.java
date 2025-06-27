package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarketGUI {
    public static final String GUI_TITLE = "§6§l交易市场"; // 改为public
    private static final int GUI_SIZE = 54; // 大箱子大小

    // 创建新的市场界面
    public static Inventory createMarketGUI(TradeManager tradeManager) {
        Inventory market = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 填充边界（美观）
        ItemStack border = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for(int i = 0; i < 9; i++) {
            market.setItem(i, border); // 顶部边框
            market.setItem(i + 45, border); // 底部边框
        }
        for(int i = 0; i < 6; i++) {
            market.setItem(i * 9, border); // 左侧边框
            market.setItem(i * 9 + 8, border); // 右侧边框
        }

        // 添加实际交易
        addRealTrades(market, tradeManager);

        return market;
    }

    // 创建GUI物品辅助方法
    private static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(name);
            if(lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    // 添加真实交易
    private static void addRealTrades(Inventory market, TradeManager tradeManager) {
        List<TradeManager.Trade> trades = tradeManager.getTrades();
        int slotIndex = 10; // 起始槽位（跳过边框）

        for (TradeManager.Trade trade : trades) {
            if (slotIndex >= 44) break; // 防止超出界面范围

            // 创建交易展示物品
            ItemStack displayItem = trade.getOfferItem().clone();
            ItemMeta meta = displayItem.getItemMeta();

            if (meta != null) {
                // 创建说明信息
                List<String> lore = new ArrayList<>();
                if (trade.getPriceItem() != null) {
                    lore.add(ChatColor.GRAY + "价格: " +
                            trade.getPriceItem().getAmount() + "x " +
                            getItemName(trade.getPriceItem()));
                }

                OfflinePlayer creator = Bukkit.getOfflinePlayer(trade.getCreator());
                if (creator.getName() != null) {
                    lore.add(ChatColor.GRAY + "发布者: " + creator.getName());
                }

                lore.add("");
                lore.add(ChatColor.YELLOW + "点击购买此交易");

                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            market.setItem(slotIndex, displayItem);
            slotIndex++;

            // 跳过边界位置
            if (slotIndex % 9 == 8) slotIndex += 2;
        }
    }

    // 改为public访问权限
    public static String getItemName(ItemStack item) {
        if (item == null) return "未知物品";
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        // 将 MATERIAL_NAME 转换为更友好的格式
        String name = item.getType().name().toLowerCase().replace('_', ' ');
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}