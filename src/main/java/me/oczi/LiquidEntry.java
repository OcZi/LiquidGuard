package me.oczi;

import me.oczi.api.LiquidType;
import me.oczi.api.node.block.LiquidNode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiquidEntry implements Lock {
    private final Lock lock = new ReentrantLock();

    private final Player player;
    private final LiquidNode node;

    LiquidEntry(LiquidNode node) {
        this(null, node);
    }

    LiquidEntry(@Nullable Player player, LiquidNode node) {
        this.player = player;
        this.node = node;
    }

    public static LiquidEntry of(@Nullable Player player, LiquidNode node) {
        return new LiquidEntry(player, node);
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

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long l, @NotNull TimeUnit timeUnit) throws InterruptedException {
        return lock.tryLock(l, timeUnit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @NotNull
    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }
}
