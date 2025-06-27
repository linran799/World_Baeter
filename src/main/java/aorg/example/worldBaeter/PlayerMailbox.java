package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerMailbox {
    private final WorldBaeter plugin;
    private final Map<UUID, List<MailItem>> playerMails = new HashMap<>();
    private File mailboxFile;

    public PlayerMailbox(WorldBaeter plugin) {
        this.plugin = plugin;
        loadMailboxData();
    }

    public static class MailItem {
        private final String sender;
        private final ItemStack item;
        private final long timestamp;

        public MailItem(String sender, ItemStack item) {
            this.sender = sender;
            this.item = item;
            this.timestamp = System.currentTimeMillis();
        }

        public String getSender() {
            return sender;
        }

        public ItemStack getItem() {
            return item;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    private void loadMailboxData() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        mailboxFile = new File(plugin.getDataFolder(), "mailbox.yml");
        if (!mailboxFile.exists()) {
            try {
                mailboxFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建邮箱数据文件: " + e.getMessage());
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(mailboxFile);
        for (String key : config.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            List<MailItem> mails = new ArrayList<>();

            for (String mailKey : config.getConfigurationSection(key).getKeys(false)) {
                String sender = config.getString(key + "." + mailKey + ".sender");
                ItemStack item = config.getItemStack(key + "." + mailKey + ".item");
                mails.add(new MailItem(sender, item));
            }

            playerMails.put(playerId, mails);
        }
    }

    public void saveMailboxData() {
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, List<MailItem>> entry : playerMails.entrySet()) {
            String playerKey = entry.getKey().toString();
            int index = 0;

            for (MailItem mail : entry.getValue()) {
                String mailKey = "mail" + index++;
                config.set(playerKey + "." + mailKey + ".sender", mail.getSender());
                config.set(playerKey + "." + mailKey + ".item", mail.getItem());
            }
        }

        try {
            config.save(mailboxFile);
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存邮箱数据: " + e.getMessage());
        }
    }

    public void sendMail(UUID playerId, String sender, ItemStack item) {
        List<MailItem> mails = playerMails.getOrDefault(playerId, new ArrayList<>());
        mails.add(new MailItem(sender, item));
        playerMails.put(playerId, mails);

        // 通知在线玩家
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.sendMessage(ChatColor.GREEN + "你有新的邮件! 输入 /mail 查看");
        }

        saveMailboxData();
    }

    public List<MailItem> getMails(UUID playerId) {
        return playerMails.getOrDefault(playerId, new ArrayList<>());
    }

    public void removeMail(UUID playerId, int index) {
        List<MailItem> mails = playerMails.get(playerId);
        if (mails != null && index >= 0 && index < mails.size()) {
            mails.remove(index);
            saveMailboxData();
        }
    }
}