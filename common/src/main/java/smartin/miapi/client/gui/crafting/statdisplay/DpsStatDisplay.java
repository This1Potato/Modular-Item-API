package smartin.miapi.client.gui.crafting.statdisplay;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import smartin.miapi.attributes.AttributeRegistry;

public class DpsStatDisplay extends SingleStatDisplayDouble{

    public DpsStatDisplay() {
        super(0, 0, 80, 32, Text.literal("DPS"), Text.empty());
    }

    @Override
    public boolean shouldRender(ItemStack original, ItemStack compareTo) {
        this.original = original;
        this.compareTo = compareTo;
        return true;
    }

    @Override
    public double getValue(ItemStack stack) {
        double attackDamage = AttributeRegistry.getAttribute(stack, EntityAttributes.GENERIC_ATTACK_DAMAGE, EquipmentSlot.MAINHAND, 1);
        double attackSpeed = AttributeRegistry.getAttribute(stack, EntityAttributes.GENERIC_ATTACK_SPEED, EquipmentSlot.MAINHAND, 4);
        return attackDamage * attackSpeed;
    }
}