package aorg.example.worldBaeter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    // 交易数据结构
    public static class Trade implements org.bukkit.configuration.serialization.ConfigurationSerializable {
        private final UUID creator;
        private final ItemStack offerItem;
        private final ItemStack priceItem;

        public Trade(UUID creator, ItemStack offerItem, ItemStack priceItem) {
            this.creator = creator;
            this.offerItem = offerItem;
            this.priceItem = priceItem;
        }

        public Trade(java.util.Map<String, Object> map) {
            this.creator = UUID.fromString((String) map.get("creator"));
            this.offerItem = (ItemStack) map.get("offerItem");
            this.priceItem = (ItemStack) map.get("priceItem");
        }

        @Override
        public java.util.Map<String, Object> serialize() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("creator", creator.toString());
            map.put("offerItem", offerItem);
            map.put("priceItem", priceItem);
            return map;
        }

        public UUID getCreator() {
            return creator;
        }

        public ItemStack getOfferItem() {
            return offerItem;
        }

        public ItemStack getPriceItem() {
            return priceItem;
        }
    }

    private void loadTrades() {
        // 确保数据文件夹存在
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