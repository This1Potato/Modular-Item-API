package smartin.miapi.events;

import com.redpxnda.nucleus.event.PrioritizedEvent;
import dev.architectury.event.EventResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.blocks.ModularWorkBenchEntity;
import smartin.miapi.entity.ItemProjectileEntity;
import smartin.miapi.modules.material.GeneratedMaterial;

import java.util.List;

public class MiapiEvents {
    public static PrioritizedEvent<LivingHurt> LIVING_HURT = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<LivingHurt> LIVING_HURT_AFTER = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<EntityRide> START_RIDING = PrioritizedEvent.createLoop(); // only fires on successful rides, and is not cancellable (if I wanted to make it cancellable, i would add mixinextras)
    public static PrioritizedEvent<EntityRide> STOP_RIDING = PrioritizedEvent.createLoop();
    public static PrioritizedEvent<BlockCraftingStatUpdate> BLOCK_STAT_UPDATE = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<ItemCraftingStatUpdate> ITEM_STAT_UPDATE = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<GeneratedMaterialEvent> GENERATED_MATERIAL = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<BlockBreakEvent> BLOCK_BREAK_EVENT = PrioritizedEvent.createEventResult();
    public static PrioritizedEvent<PlayerTickEvent> PLAYER_TICK_START = PrioritizedEvent.createLoop();
    public static PrioritizedEvent<PlayerTickEvent> PLAYER_TICK_END = PrioritizedEvent.createLoop();

    public static class LivingHurtEvent {
        public final LivingEntity livingEntity;
        public DamageSource damageSource;
        public float amount;

        public LivingHurtEvent(LivingEntity livingEntity, DamageSource damageSource, float amount) {
            this.livingEntity = livingEntity;
            this.damageSource = damageSource;
            this.amount = amount;

        }

        public ItemStack getCausingItemStack() {
            if (damageSource.getSource() instanceof ProjectileEntity projectile && (projectile instanceof ItemProjectileEntity itemProjectile)) {
                return itemProjectile.asItemStack();

            }
            if (damageSource.getAttacker() instanceof LivingEntity attacker) {
                return attacker.getMainHandStack();
            }
            return ItemStack.EMPTY;
        }
    }

    public interface PlayerTickEvent {
        EventResult tick(PlayerEntity player);
    }


    public interface GeneratedMaterialEvent {
        EventResult generated(GeneratedMaterial material, ItemStack mainIngredient, List<Item> tools, boolean isClient);
    }

    public interface BlockBreakEvent {
        void breakBlock(ServerWorld world, BlockPos pos, ItemStack tool, IntProvider experience);
    }

    public interface LivingHurt {
        EventResult hurt(LivingHurtEvent event);
    }

    public interface BlockCraftingStatUpdate {
        EventResult call(ModularWorkBenchEntity bench, @Nullable PlayerEntity player);
    }

    public interface ItemCraftingStatUpdate {
        EventResult call(ModularWorkBenchEntity bench, Iterable<ItemStack> inventory, @Nullable PlayerEntity player);
    }

    public interface EntityRide {
        void ride(Entity passenger, Entity vehicle);
    }
}
