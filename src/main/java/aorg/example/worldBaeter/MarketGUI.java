package aorg.example.worldBaeter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MarketGUI {
    private static final String GUI_TITLE = "§6§l交易市场";
    private static final int GUI_SIZE = 54; // 大箱子大小

    // 创建新的市场界面
    public static Inventory createMarketGUI() {
        Inventory market = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 填充边界（美观）
        ItemStack border = createGuiItem(Material.BLACK_STAINED_GLASS_PANE,  Component.text(" "));
        for(int i = 0; i < 9; i++) {
            market.setItem(i, border); // 顶部边框
            market.setItem(i + 45, border); // 底部边框
        }
        for(int i = 0; i < 6; i++) {
            market.setItem(i * 9, border); // 左侧边框
            market.setItem(i * 9 + 8, border); // 右侧边框
        }

        // 添加示例交易（后续会替换为真实数据）
        addSampleTrades(market);

        return market;
    }

    // 创建GUI物品辅助方法
    private static ItemStack createGuiItem(Material material, Component name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.displayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    // 添加示例交易（开发阶段使用）
    private static void addSampleTrades(Inventory market) {
        // 交易1：用钻石换绿宝石
        market.setItem(19, createGuiItem(
                Material.DIAMOND,
                Component.text("§a购买钻石"),
                "§7价格: §62 绿宝石",
                "§7库存: §e10",
                "",
                "§e点击购买"
        ));

        // 交易2：用金锭换铁锭
        market.setItem(21, createGuiItem(
                Material.GOLD_INGOT,
                Component.text("§a购买金锭"),
                "§7价格: §63 铁锭",
                "§7库存: §e5",
                "",
                "§e点击购买"
        ));

        // 交易3：用海洋之星换钻石
        market.setItem(23, createGuiItem(
                Material.HEART_OF_THE_SEA,
                Component.text("§a购买海洋之星"),
                "§7价格: §610 钻石",
                "§7库存: §e3",
                "",
                "§e点击购买"
        ));
    }
}