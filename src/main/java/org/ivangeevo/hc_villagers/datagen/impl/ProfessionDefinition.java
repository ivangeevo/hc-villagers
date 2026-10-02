package org.ivangeevo.hc_villagers.datagen.impl;

import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import java.util.TreeMap;

public final class ProfessionDefinition {
    final Identifier profession;
    final String name;
    boolean replace;
    Optional<List<Integer>> slots = Optional.empty();
    Optional<List<Integer>> required = Optional.empty();
    boolean inline;
    final TreeMap<Integer, LevelDefinition> levels = new TreeMap<>();

    private ProfessionDefinition(Identifier profession) {
        this.profession = profession;
        this.name = profession.getPath();
    }

    public static ProfessionDefinition of(String professionId) { return new ProfessionDefinition(Identifier.of(professionId)); }

    public ProfessionDefinition replace() {
        this.replace = true;
        return this;
    }

    public ProfessionDefinition slots(Integer... values) {
        this.slots = Optional.of(Arrays.asList(values));
        return this;
    }

    public ProfessionDefinition required(Integer... values) {
        this.required = Optional.of(Arrays.asList(values));
        return this;
    }

    public ProfessionDefinition inline() {
        this.inline = true;
        return this;
    }

    public ProfessionDefinition level(int level, LevelDefinition def) {
        levels.put(level, def);
        return this;
    }
}