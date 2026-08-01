package dev.tr7zw.itemswapper.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        Path correct = Path.of("config", baseName + ".json");
        Path misnamed = Path.of("config", baseName + ".json.json");
        if (!Files.isRegularFile(misnamed)) {
            return;
        }

        Path backup = null;
        try {
            if (Files.exists(correct)) {
                backup = Path.of("config", baseName + ".json.bak");
                Files.move(correct, backup, StandardCopyOption.REPLACE_EXISTING);
            }
            Files.move(misnamed, correct);
            if (backup != null) {
                Files.deleteIfExists(backup);
            }
        } catch (IOException e) {
            if (backup != null && Files.exists(backup) && !Files.exists(correct)) {
                try {
                    Files.move(backup, correct);
                } catch (IOException ignored) {
                    // Leave ConfigManager to create a fresh config if restore also fails.
                }
            }
        }
    }
}
