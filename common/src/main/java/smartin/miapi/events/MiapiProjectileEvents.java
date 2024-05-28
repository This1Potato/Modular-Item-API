package smartin.miapi.events;

import com.redpxnda.nucleus.event.PrioritizedEvent;
import dev.architectury.event.EventResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.entity.ItemProjectileEntity;

public final class MiapiProjectileEvents {
    public static final PrioritizedEvent<ModularProjectileEntityHit> MODULAR_PROJECTILE_ENTITY_HIT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ModularProjectileEntityHit> MODULAR_PROJECTILE_ENTITY_POST_HIT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ModularProjectileBlockHit> MODULAR_PROJECTILE_BLOCK_HIT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ModularProjectileTick> MODULAR_PROJECTILE_TICK = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ItemProjectileCompound> MODULAR_PROJECTILE_NBT_WRITE = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ItemProjectileCompound> MODULAR_PROJECTILE_NBT_READ = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ItemProjectileDataTracker> MODULAR_PROJECTILE_DATA_TRACKER_INIT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ItemProjectileDataTracker> MODULAR_PROJECTILE_DATA_TRACKER_SET = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<PlayerPickupEvent> MODULAR_PROJECTILE_PICK_UP = PrioritizedEvent.createEventResult();

    public static final PrioritizedEvent<CrossbowContext> MODULAR_CROSSBOW_PRE_SHOT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<CrossbowContextEvent> MODULAR_CROSSBOW_LOAD = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<CrossbowContextEvent> MODULAR_CROSSBOW_LOAD_AFTER = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<CrossbowContext> MODULAR_CROSSBOW_POST_SHOT = PrioritizedEvent.createEventResult();

    public static final PrioritizedEvent<ModularBowShot> MODULAR_BOW_SHOT = PrioritizedEvent.createEventResult();
    public static final PrioritizedEvent<ModularBowShot> MODULAR_BOW_POST_SHOT = PrioritizedEvent.createEventResult();

    public static class ModularProjectileEntityHitEvent {
        public EntityHitResult entityHitResult;
        public ItemProjectileEntity projectile;
        @Nullable
        public DamageSource damageSource;
        public float damage;

        public ModularProjectileEntityHitEvent(EntityHitResult entityHitResult, ItemProjectileEntity projectile, @Nullable DamageSource damageSource, float damage) {
            this.entityHitResult = entityHitResult;
            this.projectile = projectile;
            this.damageSource = damageSource;
            this.damage = damage;
        }
    }

    public static class ModularProjectileBlockHitEvent {
        public BlockHitResult blockHitResult;
        public ItemProjectileEntity projectile;

        public ModularProjectileBlockHitEvent(BlockHitResult blockHitResult, ItemProjectileEntity projectile) {
            this.blockHitResult = blockHitResult;
            this.projectile = projectile;
        }
    }

    public static class ModularBowShotEvent {
        public PersistentProjectileEntity projectile;
        public ItemStack bowStack;
        public LivingEntity shooter;

        public ModularBowShotEvent(PersistentProjectileEntity projectile, ItemStack bowStack, LivingEntity shooter) {
            this.projectile = projectile;
            this.bowStack = bowStack;
            this.shooter = shooter;
        }
    }

    public interface CrossbowContext {
        EventResult shoot(LivingEntity player, ItemStack crossbow);
    }

    public interface CrossbowContextEvent {
        EventResult load(CrossbowLoadingContext context);
    }

    public static class CrossbowLoadingContext {
        public LivingEntity player;
        public ItemStack crossbow;
        public ItemStack loadingProjectile;

        public CrossbowLoadingContext(LivingEntity player, ItemStack crossbow, ItemStack loadingProjectile) {
            this.player = player;
            this.crossbow = crossbow;
            this.loadingProjectile = loadingProjectile;
        }
    }

    public interface ItemProjectileCompound {
        EventResult nbtEvent(ItemProjectileEntity projectile, NbtCompound nbtCompound);
    }

    public interface PlayerPickupEvent {
        EventResult pickup(PlayerEntity entity, ItemProjectileEntity projectile);
    }

    public interface ItemProjectileDataTracker {
        EventResult dataTracker(ItemProjectileEntity projectile, DataTracker nbtCompound);
    }

    public interface ModularProjectileBlockHit {
        EventResult hit(ModularProjectileBlockHitEvent event);
    }

    public interface ModularProjectileTick {
        EventResult tick(ItemProjectileEntity event);
    }

    public interface ModularBowShot {
        EventResult call(ModularBowShotEvent event);
    }

    public interface ModularProjectileEntityHit {
        EventResult hit(ModularProjectileEntityHitEvent event);
    }
}
