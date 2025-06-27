package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class MailGUI {
    public static final String GUI_TITLE = "§6§l玩家邮箱";
    private static final int GUI_SIZE = 54;

    public static Inventory createMailGUI(PlayerMailbox mailbox, UUID playerId) {
        Inventory mail = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);

        // 填充边界
        ItemStack border = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for(int i = 0; i < 9; i++) {
            mail.setItem(i, border);
            mail.setItem(i + 45, border);
        }
        for(int i = 0; i < 6; i++) {
            mail.setItem(i * 9, border);
            mail.setItem(i * 9 + 8, border);
        }

        // 添加邮件
        addPlayerMails(mail, mailbox, playerId);

        return mail;
    }

    private static void addPlayerMails(Inventory mail, PlayerMailbox mailbox, UUID playerId) {
        List<PlayerMailbox.MailItem> mails = mailbox.getMails(playerId);
        int slotIndex = 10;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (PlayerMailbox.MailItem mailItem : mails) {
            if (slotIndex >= 44) break;

            ItemStack displayItem = mailItem.getItem().clone();
            ItemMeta meta = displayItem.getItemMeta();

            if (meta != null) {
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "发件人: " + mailItem.getSender());
                lore.add(ChatColor.GRAY + "时间: " + sdf.format(new Date(mailItem.getTimestamp())));
                lore.add("");
                lore.add(ChatColor.YELLOW + "点击领取物品");

                meta.setLore(lore);
                displayItem.setItemMeta(meta);
            }

            mail.setItem(slotIndex, displayItem);
            slotIndex++;

            if (slotIndex % 9 == 8) slotIndex += 2;
        }
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
}