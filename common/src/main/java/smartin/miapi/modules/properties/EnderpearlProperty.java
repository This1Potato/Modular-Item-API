package smartin.miapi.modules.properties;

import com.google.gson.JsonElement;
import dev.architectury.event.EventResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import smartin.miapi.entity.ItemProjectileEntity;
import smartin.miapi.events.MiapiProjectileEvents;
import smartin.miapi.modules.ItemModule;
import smartin.miapi.modules.properties.util.ModuleProperty;

/**
 * This property gives a projectile ender pearl behaviour
 */
public class EnderpearlProperty implements ModuleProperty {
    //TODO:rework this into complexBooleanProperty
    public static final String KEY = "is_enderpearl";
    public static EnderpearlProperty property;

    public EnderpearlProperty() {
        property = this;
        MiapiProjectileEvents.MODULAR_PROJECTILE_ENTITY_HIT.register(event -> {
            if (isEnderPearl(event.projectile)) {
                if (onCollision(event.projectile, event.entityHitResult)) {
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.pass();
        });
        MiapiProjectileEvents.MODULAR_PROJECTILE_BLOCK_HIT.register(event -> {
            if (isEnderPearl(event.projectile)) {
                if (onCollision(event.projectile, event.blockHitResult)) {
                    return EventResult.interruptTrue();
                }
            }
            return EventResult.pass();
        });
    }

    /**
     * copied straigth from {@link net.minecraft.entity.projectile.thrown.EnderPearlEntity}
     *
     * @param projectile the projectile in question
     * @param hitResult  the Hitresult
     * @return if the player was teleported
     */
    protected boolean onCollision(ItemProjectileEntity projectile, HitResult hitResult) {
        Random random = Random.create();
        for (int i = 0; i < 32; ++i) {
            projectile.getWorld().addParticle(ParticleTypes.PORTAL, projectile.getX(), projectile.getY() + random.nextDouble() * 2.0, projectile.getZ(), random.nextGaussian(), 0.0, random.nextGaussian());
        }
        if (!projectile.getWorld().isClient && !projectile.isRemoved()) {
            Entity entity = projectile.getOwner();
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                if (serverPlayerEntity.networkHandler.isConnectionOpen() && serverPlayerEntity.getWorld() == projectile.getWorld() && !serverPlayerEntity.isSleeping()) {
                    EndermiteEntity endermiteEntity;
                    if (random.nextFloat() < 0.05f && projectile.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && (endermiteEntity = EntityType.ENDERMITE.create(projectile.getWorld())) != null) {
                        endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
                        projectile.getWorld().spawnEntity(endermiteEntity);
                    }
                    if (entity.hasVehicle()) {
                        serverPlayerEntity.requestTeleportAndDismount(projectile.getX(), projectile.getY(), projectile.getZ());
                    } else {
                        entity.requestTeleport(projectile.getX(), projectile.getY(), projectile.getZ());
                    }
                    entity.onLanding();
                    entity.damage(projectile.getDamageSources().fall(), 5.0f);
                }
            } else if (entity != null) {
                entity.requestTeleport(projectile.getX(), projectile.getY(), projectile.getZ());
                entity.onLanding();
            }
            projectile.discard();
            return true;
        }
        return false;
    }

    public static boolean isEnderPearl(ItemProjectileEntity projectile) {
        return isEnderPearl(projectile.asItemStack());
    }

    public static boolean isEnderPearl(ItemStack itemStack) {
        JsonElement element = ItemModule.getMergedProperty(itemStack, property);
        if (element != null) {
            return element.getAsBoolean();
        }
        return false;
    }

    @Override
    public boolean load(String moduleKey, JsonElement data) throws Exception {
        data.getAsBoolean();
        return true;
    }
}
