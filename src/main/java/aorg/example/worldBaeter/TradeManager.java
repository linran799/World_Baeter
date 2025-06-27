package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TradeManager {
    static {
        ConfigurationSerialization.registerClass(Trade.class);
    }

    private final WorldBaeter plugin;
    private final List<Trade> trades = new ArrayList<>();
    private File tradeFile;

    public TradeManager(WorldBaeter plugin) {
        this.plugin = plugin;
        loadTrades();
    }

    public static class Trade implements java.util.Map<String, Object> {
        private final UUID creator;
        private final ItemStack requiredItem;
        private final ItemStack rewardItem;

        public Trade(UUID creator, ItemStack requiredItem, ItemStack rewardItem) {
            this.creator = creator;
            this.requiredItem = requiredItem;
            this.rewardItem = rewardItem;
        }

        public Trade(Map<String, Object> map) {
            this.creator = UUID.fromString((String) map.get("creator"));
            this.requiredItem = (ItemStack) map.get("requiredItem");
            this.rewardItem = (ItemStack) map.get("rewardItem");
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("creator", creator.toString());
            map.put("requiredItem", requiredItem);
            map.put("rewardItem", rewardItem);
            return map;
        }

        public UUID getCreator() {
            return creator;
        }

        public ItemStack getRequiredItem() {
            return requiredItem;
        }

        public ItemStack getRewardItem() {
            return rewardItem;
        }
    }

    private void loadTrades() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        tradeFile = new File(plugin.getDataFolder(), "trades.yml");
        if (!tradeFile.exists()) {
            try {
                tradeFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建交易数据文件: " + e.getMessage());
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(tradeFile);
        if (config.contains("trades")) {
            List<?> tradeList = config.getList("trades");
            if (tradeList != null) {
                for (Object obj : tradeList) {
                    if (obj instanceof Trade) {
                        trades.add((Trade) obj);
                    }
                }
            }
        }
        plugin.getLogger().info("已加载 " + trades.size() + " 个交易");
    }

    public void saveTrades() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("trades", trades);

        try {
            config.save(tradeFile);
            plugin.getLogger().info("已保存 " + trades.size() + " 个交易");
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存交易数据: " + e.getMessage());
        }
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
        saveTrades();
    }

    public boolean removeTrade(Trade trade) {
        boolean result = trades.remove(trade);
        if (result) {
            saveTrades();
        }
        return result;
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }
}