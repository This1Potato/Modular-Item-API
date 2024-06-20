package smartin.miapi.client.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class ItemMiapiModel implements MiapiModel {

    final Supplier<ItemStack> stackSupplier;
    final Matrix4f matrix4f;

    public ItemMiapiModel(Supplier<ItemStack> stack,Matrix4f matrix4f){
        this.stackSupplier = stack;
        this.matrix4f = matrix4f;
    }

    @Override
    public void render(MatrixStack matrices, ItemStack stack, ModelTransformationMode transformationMode, float tickDelta, VertexConsumerProvider vertexConsumers, LivingEntity entity, int light, int overlay) {
        MinecraftClient.getInstance().world.getProfiler().push("ItemOnTopRendering");
        matrices.push();
        matrices.multiplyPositionMatrix(matrix4f);
        ItemStack modelStack = stackSupplier.get();
        if(modelStack.getItem() instanceof FireworkRocketItem){
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                modelStack,
                ModelTransformationMode.FIXED,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0);
        matrices.pop();
        MinecraftClient.getInstance().world.getProfiler().pop();
    }
}
