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
    public static final String GUI_TITLE = "§6§l交易市场";
    private static final int GUI_SIZE = 54;

    public static Inventory createMarketGUI(TradeManager tradeManager) {
        Inventory market = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 填充边界
        ItemStack border = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for(int i = 0; i < 9; i++) {
            market.setItem(i, border);
            market.setItem(i + 45, border);
        }
        for(int i = 0; i < 6; i++) {
            market.setItem(i * 9, border);
            market.setItem(i * 9 + 8, border);
        }

        // 添加实际交易
        addRealTrades(market, tradeManager);

        return market;
    }

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

    private static void addRealTrades(Inventory market, TradeManager tradeManager) {
        List<TradeManager.Trade> trades = tradeManager.getTrades();
        int slotIndex = 10;

        for (TradeManager.Trade trade : trades) {
            if (slotIndex >= 44) break;

            // 安全克隆物品，防止NBT错误
            ItemStack displayItem = safeClone(trade.getRequiredItem());
            if (displayItem == null) {
                displayItem = new ItemStack(Material.BARRIER);
                ItemMeta meta = displayItem.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "无效交易");
                displayItem.setItemMeta(meta);
            }

            ItemMeta meta = displayItem.getItemMeta();
            if (meta != null) {
                List<String> lore = new ArrayList<>();

                if (trade.getRewardItem() != null) {
                    lore.add(ChatColor.GRAY + "报酬: " +
                            trade.getRewardItem().getAmount() + "x " +
                            getItemName(trade.getRewardItem()));
                }

                OfflinePlayer creator = Bukkit.getOfflinePlayer(trade.getCreator());
                if (creator.getName() != null) {
                    lore.add(ChatColor.GRAY + "发布者: " + creator.getName());
                }

                lore.add("");
                lore.add(ChatColor.YELLOW + "点击接受委托");

                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            market.setItem(slotIndex, displayItem);
            slotIndex++;

            if (slotIndex % 9 == 8) slotIndex += 2;
        }
    }

    private static ItemStack safeClone(ItemStack original) {
        if (original == null) return null;
        try {
            return original.clone();
        } catch (Exception e) {
            return new ItemStack(original.getType(), original.getAmount());
        }
    }

    public static String getItemName(ItemStack item) {
        if (item == null) return "未知物品";
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        String name = item.getType().name().toLowerCase().replace('_', ' ');
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}