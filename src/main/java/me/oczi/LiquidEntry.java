package me.oczi;

import me.oczi.api.LiquidType;
import me.oczi.api.node.block.LiquidNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class LiquidEntry {
    private final Player player;
    private final LiquidNode node;

    public LiquidEntry(LiquidNode node) {
        this(null, node);
    }

    public LiquidEntry(@Nullable Player player, LiquidNode node) {
        this.player = player;
        this.node = node;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public LiquidType getLiquidType() {
        return node.getLiquidType();
    }

    public LiquidNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "LiquidEntry{node=" + node + "}";
    }
}
