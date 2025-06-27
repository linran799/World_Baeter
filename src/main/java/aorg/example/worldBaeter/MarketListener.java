package aorg.example.worldBaeter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MarketListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查是否是交易市场界面
        if (!event.getView().getTitle().equals(MarketGUI.GUI_TITLE)) {
            return;
        }

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

        // 处理交易（这里只是示例）
        player.sendMessage(ChatColor.GREEN + "你尝试购买: " +
                ChatColor.YELLOW + clickedItem.getItemMeta().getDisplayName());
        // TODO: 添加实际交易逻辑
    }
}