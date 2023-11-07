package smartin.miapi.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.modules.properties.render.ModelProperty;
import smartin.miapi.registries.RegistryInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static smartin.miapi.client.SpriteLoader.miapiModels;

@Environment(EnvType.CLIENT)
public class CustomModel implements UnbakedModel, BakedModel {
    public static LivingEntity currentEntity;
    public static boolean isModularItem(Identifier identifier) {
        return RegistryInventory.modularItems.get(identifier.toString().replace("item/", "")) != null;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, net.minecraft.util.math.random.Random random) {
        return new ArrayList<>();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public Sprite getParticleSprite() {
        Identifier stoneTextureId = new Identifier("minecraft", "block/stone");
        return MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(stoneTextureId);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }


    @Override
    public Collection<Identifier> getModelDependencies() {
        return miapiModels;
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader) {
        miapiModels.forEach(modelLoader::apply);
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        ModelProperty.textureGetter = textureGetter;
        DynamicBakery.dynamicBaker = baker;
        return this;
    }

    @Override
    public String toString() {
        return "CustomModel{" +
                '}';
    }
}
