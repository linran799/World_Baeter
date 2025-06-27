package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TradeCreationGUI {
    public static final String GUI_TITLE = ChatColor.GREEN + "发布新交易";

    // GUI中的特殊槽位定义
    public static final int OFFER_SLOT = 11;   // 提供的物品槽位
    public static final int PRICE_SLOT = 15;   // 要求的报酬槽位
    public static final int CONFIRM_SLOT = 22; // 确认按钮槽位

    public static Inventory createTradeCreationGUI() {
        Inventory tradeInv = Bukkit.createInventory(null, 27, GUI_TITLE);

        // 填充背景
        ItemStack background = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 27; i++) {
            tradeInv.setItem(i, background);
        }

        // 添加说明物品
        tradeInv.setItem(4, createGuiItem(Material.BOOK, ChatColor.YELLOW + "如何发布交易",
                ChatColor.GRAY + "1. 左侧放入你要提供的物品",
                ChatColor.GRAY + "2. 右侧放入你要求的报酬",
                ChatColor.GRAY + "3. 点击底部绿宝石确认发布",
                "",
                ChatColor.RED + "注意: 发布交易会立即收取报酬物品!"
        ));

        // 添加提供物品槽位
        tradeInv.setItem(OFFER_SLOT, createGuiItem(Material.LIME_STAINED_GLASS_PANE,
                ChatColor.GREEN + "← 提供物品",
                ChatColor.GRAY + "放入你要提供的物品"));

        // 添加报酬物品槽位
        tradeInv.setItem(PRICE_SLOT, createGuiItem(Material.RED_STAINED_GLASS_PANE,
                ChatColor.RED + "报酬物品 →",
                ChatColor.GRAY + "放入你要求的报酬"));

        // 添加确认按钮
        tradeInv.setItem(CONFIRM_SLOT, createGuiItem(Material.EMERALD,
                ChatColor.GREEN + "确认发布交易"));

        return tradeInv;
    }

    public static ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}