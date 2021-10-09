package com.kbi.qwertech.config;

import com.google.common.collect.ImmutableSet;
import com.kbi.qwertech.api.data.QTConfigs;
import net.minecraft.launchwrapper.Launch;

import java.io.*;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.kbi.qwertech.api.data.QTConfigs.L;

public class Config {
    //forge's cfg refuses to dump itself to file at this point, so I'll just use a better format
    private static void handleConfig(Map<String, String> cfg) {
        QTConfigs.patchTechgunsAutoFeeder = getBool("patchTechgunsAutofeeder", cfg);
        QTConfigs.patchTechgunsCrash = getBool("patchTechgunsCrash", cfg);
        QTConfigs.patchImmibisAutoFeeder = getBool("patchImmibisAutoFeeder", cfg);
        QTConfigs.customBumblesMixins = getBool("customBumblesMixins", cfg);
    }

    public static void init() {
        Map<String, String> cfg = new HashMap<>();
        ImmutableSet<? extends Entry<? extends Serializable>> entries = ImmutableSet.of(
                Entry.of("patchTechgunsAutofeeder", true,
                        "patchTechgunsAutofeeder: Makes Techguns' autofeeder inventory accept and consume Gregtech foods. [Side: SERVER | Default: true]"),
                Entry.of("patchTechgunsCrash", true,
                        "patchTechgunsCrash: Prevents world corruption caused by entities touching exposed live wires. [Side: SERVER | Default: true]"),
                Entry.of("patchImmibisAutoFeeder", true,
                        "patchImmibisAutoFeeder: Makes the Immibis' AutoFeeder accept and consume Gregtech foods. [Side: SERVER | Default: true]"),
                Entry.of("customBumblesMixins", true,
                        "customBumblesMixins: Complimentary Mixins for the Custom Bumblebee functionality. [Side: BOTH | Default: true]")

        );
        Path configPath;
        try {
            configPath = Launch.minecraftHome.toPath().resolve("config").resolve("gregtech").resolve("QTMixins.properties");
        } catch (NullPointerException e) {
            configPath = new File(".", "config/gregtech/QTMixins.properties").toPath();
        }
        try {
            boolean changed = false;
            File configurationFile = configPath.toFile();
            if (Files.notExists(configPath) && !configPath.toFile().createNewFile()) {
                L.error("Error creating config file \"" + configurationFile + "\".");
            }
            Properties config = new Properties();
            StringBuilder content = new StringBuilder().append("#QwerTech Mixin Configuration.\n");
            content.append("#Last generated at: ").append(new Date()).append("\n\n");
            FileInputStream input = new FileInputStream(configurationFile);
            config.load(input);
            for (Entry<?> entry : entries) {
                String key = entry.key;
                Object value = entry.value;
                Class<?> cls = entry.cls;
                if (config.containsKey(key)) {
                    Object obj = config.getProperty(key);
                    String s = String.valueOf(obj);
                    if (s.equals("")) {
                        L.error("Error processing configuration file \"" + configurationFile + "\".");
                        L.error("Expected configuration value for " + key + " to be present, found nothing. Using default value \"" + value + "\" instead.");
                        cfg.put(key, value.toString());
                    } else if (cls.equals(Integer.class)) {
                        try {
                            Integer.parseInt(s);
                            cfg.put(key, s);
                        } catch (NumberFormatException e) {
                            L.error("Error processing configuration file \"" + configurationFile + "\".");
                            L.error("Expected configuration value for " + key + " to be an integer, found \"" + s + "\". Using default value \"" + value + "\" instead.");
                            cfg.put(key, value.toString());
                        }
                    } else if (cls.equals(Float.class)) {
                        try {
                            Float.parseFloat(s);
                            cfg.put(key, s);
                        } catch (NumberFormatException e) {
                            L.error("Error processing configuration file \"" + configurationFile + "\".");
                            L.error("Expected configuration value for " + key + " to be a float, found \"" + s + "\". Using default value \"" + value + "\" instead.");
                            cfg.put(key, value.toString());
                        }
                    } else if (cls.equals(Boolean.class)) {
                        if (!"true".equalsIgnoreCase(s) && !"false".equalsIgnoreCase(s)) {
                            L.error("Error processing configuration file \"" + configurationFile + "\".");
                            L.error("Expected configuration value for " + key + " to be a boolean, found \"" + s + "\". Using default value \"" + value + "\" instead.");
                            cfg.put(key, value.toString());
                        } else cfg.put(key, s);
                    }
                } else {
                    changed = true;
                    config.setProperty(key, value.toString());
                    cfg.put(key, value.toString());
                }
                content.append("#").append(entry.comment.get()).append("\n");
                content.append(key).append("=").append(cfg.get(key)).append("\n");
            }
            if (changed) {
                FileWriter fw = new FileWriter(configurationFile, false);
                fw.write(content.toString());
                fw.close();
            }
            handleConfig(cfg);
        } catch (IOException e) {
            L.error("Could not read/write config! Stacktrace: " + e);
        }
    }

    private static int getInt(String s, Map<String, String> cfg) {
        return Integer.parseInt(cfg.get(s));
    }

    private static boolean getBool(String s, Map<String, String> cfg) {
        return Boolean.parseBoolean(cfg.get(s));
    }

    private static class Entry<T> {
        private final String key;
        private final T value;
        private final WeakReference<String> comment;
        private final Class<T> cls;

        private Entry(String key, T value, String comment, Class<T> cls) {
            this.key = key;
            this.value = value;
            this.comment = new WeakReference<>(comment);
            this.cls = cls;
        }

        public static Entry<Integer> of(String key, int value, String comment) {
            return new Entry<>(key, value, comment, Integer.class);
        }

        public static Entry<Float> of(String key, float value, String comment) {
            return new Entry<>(key, value, comment, Float.class);
        }

        public static Entry<Boolean> of(String key, boolean value, String comment) {
            return new Entry<>(key, value, comment, Boolean.class);
        }
    }
}
