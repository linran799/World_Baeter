package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class TradeStep2GUI {
    public static final String GUI_TITLE = ChatColor.GREEN + "设置报酬";
    public static final int CONFIRM_SLOT = 53;
    public static final int REQUIRED_SLOT = 45;
    public static final int INFO_SLOT = 49;

    public static Inventory createTradeStep2GUI(List<ItemStack> requiredItems) {
        Inventory inv = Bukkit.createInventory(null, 54, GUI_TITLE);

        // 填充背景
        ItemStack background = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) {
            // 确认按钮位置留空
            if (i != CONFIRM_SLOT && i != INFO_SLOT) {
                inv.setItem(i, background);
            }
        }

        // 显示所需物品
        int slot = REQUIRED_SLOT;
        for (ItemStack item : requiredItems) {
            if (slot >= 54) break;
            inv.setItem(slot, item);
            slot++;
        }

        // 添加说明
        inv.setItem(INFO_SLOT, createGuiItem(Material.BOOK, ChatColor.YELLOW + "设置报酬",
                ChatColor.GRAY + "1. 在下方放入报酬物品",
                ChatColor.GRAY + "2. 可以放入多个物品",
                ChatColor.GRAY + "3. 点击右侧绿宝石完成",
                "",
                ChatColor.RED + "注意: 发布交易会立即收取报酬物品!"
        ));

        // 添加确认按钮
        inv.setItem(CONFIRM_SLOT, createGuiItem(Material.EMERALD_BLOCK,
                ChatColor.GREEN + "发布交易"));

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