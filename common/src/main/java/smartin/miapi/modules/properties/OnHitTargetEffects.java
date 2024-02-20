package smartin.miapi.modules.properties;

import dev.architectury.event.EventResult;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import smartin.miapi.events.MiapiEvents;
import smartin.miapi.modules.properties.util.ModuleProperty;

import java.util.ArrayList;
import java.util.List;

public class OnHitTargetEffects extends PotionEffectProperty {
    public static String KEY = "on_hit_potion";
    public OnHitTargetEffects property;

    public OnHitTargetEffects() {
        super(KEY);
        property = this;

        MiapiEvents.LIVING_HURT.register((listener) -> {
            if (listener.damageSource.getAttacker() instanceof LivingEntity livingEntity && !livingEntity.getWorld().isClient()) {
                applyEffects(listener.livingEntity, livingEntity, livingEntity, this::isTargetOther);
            }
            if (listener.damageSource.getAttacker() instanceof LivingEntity livingEntity && !livingEntity.getWorld().isClient()) {
                applyEffects(livingEntity, livingEntity, livingEntity, this::isTargetSelf);
            }
            return EventResult.pass();
        });

        LoreProperty.loreSuppliers.add(itemStack -> {
            List<Text> lines = new ArrayList<>();
            for (EffectHolder effectHolder : getStatusEffects(itemStack)) {
                if (effectHolder.isGuiVisibility()) {
                    Text text = effectHolder.getPotionDescription();
                    if (isTargetSelf(effectHolder)) {
                        lines.add(Text.translatable("miapi.potion.target.self.tooltip", text));
                    } else {
                        lines.add(Text.translatable("miapi.potion.target.other.tooltip", text));
                    }
                }
            }
            if (!lines.isEmpty()) {
                lines.add(0, Text.translatable("miapi.potion.target.on_hit"));
            }
            return lines;
        });
    }

    public boolean isTargetOther(EffectHolder holder, EquipmentSlot slot) {
        return isTargetOther(holder);
    }

    public boolean isTargetOther(EffectHolder holder) {
        return !ModuleProperty.getBoolean(holder.rawData(), "target_self", holder.moduleInstance(), false);
    }

    public boolean isTargetSelf(EffectHolder holder, EquipmentSlot slot) {
        return !isTargetOther(holder);
    }

    public boolean isTargetSelf(EffectHolder holder) {
        return !isTargetOther(holder);
    }
}
