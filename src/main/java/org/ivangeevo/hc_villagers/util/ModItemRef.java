package org.ivangeevo.hc_villagers.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.ivangeevo.hc_villagers.HCVillagersMod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An item from another mod. Resolves every time {@link #asItem()} is called:
 * <ul>
 *   <li>mod loaded and item registered: the modded item</li>
 *   <li>mod missing, or the ID doesn't exist (renamed/removed): the vanilla fallback</li>
 * </ul>
 * Only call {@code asItem()} once registries are filled (i.e. not during mod init) - see {@link LazyTradeFactory}.
 */
public record ModItemRef(String modId, Identifier id, ItemConvertible fallback) implements ItemConvertible {

    private static final Set<Identifier> WARNED = ConcurrentHashMap.newKeySet();

    /** Item whose registry namespace is the same as the mod ID (the usual case). */
    public static ModItemRef of(String modId, String path, ItemConvertible fallback) {
        return new ModItemRef(modId, Identifier.of(modId, path), fallback);
    }

    public boolean isModLoaded() {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Item asItem() {
        if (isModLoaded()) {
            Item item = Registries.ITEM.getOrEmpty(id).orElse(null);
            if (item != null) {
                return item;
            }
            // Mod is there but the ID isn't
            if (WARNED.add(id)) {
                HCVillagersMod.LOGGER.warn(
                        "[{}] '{}' is loaded but has no item '{}'; using {} in villager trades instead",
                        HCVillagersMod.MOD_ID,
                        modId,
                        id,
                        Registries.ITEM.getId(fallback.asItem())
                );
            }
        }
        return fallback.asItem();
    }
}
