package org.ivangeevo.hc_villagers.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.ivangeevo.hc_villagers.datagen.provider.ButcherTradesProvider;
import org.ivangeevo.hc_villagers.datagen.provider.ClericTradesProvider;
import org.ivangeevo.hc_villagers.datagen.provider.FarmerTradesProvider;
import org.ivangeevo.hc_villagers.datagen.provider.LibrarianTradesProvider;

public class HCVillagersDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(FarmerTradesProvider::new);
        pack.addProvider(LibrarianTradesProvider::new);
        pack.addProvider(ButcherTradesProvider::new);
        //pack.addProvider(BlacksmithTradesProvider::new);
        pack.addProvider(ClericTradesProvider::new);
    }
}
