package smartin.miapi.forge.mixin.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import org.spongepowered.asm.mixin.Mixin;
import smartin.miapi.forge.compat.ModularItemInject;
import smartin.miapi.item.modular.items.ModularChestPlate;

@Mixin(value = ModularChestPlate.class)
public abstract class ModularChestplateMixin implements ModularItemInject {
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return canPerformActionModular(stack, toolAction);
    }
}