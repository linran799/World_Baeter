package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TradeStep1GUI {
    public static final String GUI_TITLE = ChatColor.GREEN + "设置所需物品";
    public static final int CONFIRM_SLOT = 53;
    public static final int INFO_SLOT = 49;

    public static Inventory createTradeStep1GUI() {
        Inventory inv = Bukkit.createInventory(null, 54, GUI_TITLE);

        // 填充背景
        ItemStack background = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, background);
        }

        // 添加说明
        inv.setItem(INFO_SLOT, createGuiItem(Material.BOOK, ChatColor.YELLOW + "设置所需物品",
                ChatColor.GRAY + "1. 在下方放入你需要的物品",
                ChatColor.GRAY + "2. 可以放入多个物品",
                ChatColor.GRAY + "3. 点击右侧绿宝石继续"
        ));

        // 添加确认按钮
        inv.setItem(CONFIRM_SLOT, createGuiItem(Material.EMERALD_BLOCK,
                ChatColor.GREEN + "下一步 →"));

        return inv;
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