package dev.tr7zw.itemswapper.config;

import java.io.File;

import dev.tr7zw.transition.config.ConfigManager;
import lombok.*;

@Getter
public class ConfigHolder {

    static {
        // TRansition's ConfigManager always appends ".json". Older beta builds
        // incorrectly passed names that already included the extension, producing
        // "*.json.json" files. Prefer those live configs when migrating.
        migrateMisnamedConfig("itemswapper");
        migrateMisnamedConfig("itemswapper-server-cache");
    }

    @Getter
    private final static ConfigHolder instance = new ConfigHolder();
    private final ConfigManager<Config> general = new ConfigManager<>("itemswapper", Config::new,
            ConfigUpgrader::upgradeConfig);
    private final ConfigManager<CacheServerAddresses> serverCache = new ConfigManager<>("itemswapper-server-cache",
            CacheServerAddresses::new, null);

    public void save() {
        general.writeConfig();
        serverCache.writeConfig();
    }

    public void reset() {
        general.reset();
        serverCache.reset();
    }

    private static void migrateMisnamedConfig(String baseName) {
        File correct = new File("config", baseName + ".json");
        File misnamed = new File("config", baseName + ".json.json");
        if (!misnamed.isFile()) {
            return;
        }
        if (correct.exists() && !correct.delete()) {
            return;
        }
        // Best-effort migration; ConfigManager will create a fresh file if rename fails.
        misnamed.renameTo(correct);
    }
}
