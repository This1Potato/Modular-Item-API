package smartin.miapi.modules.properties.render;

import com.google.gson.JsonElement;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import smartin.miapi.Miapi;
import smartin.miapi.client.modelrework.ItemMiapiModel;
import smartin.miapi.client.modelrework.MiapiItemModel;
import smartin.miapi.client.modelrework.MiapiModel;
import smartin.miapi.item.modular.Transform;
import smartin.miapi.modules.ItemModule;
import smartin.miapi.modules.properties.util.ModuleProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemModelProperty implements ModuleProperty {
    public static final String KEY = "item_model";
    public static ItemModelProperty property;

    public ItemModelProperty() {
        property = this;
        MiapiItemModel.modelSuppliers.add(new MiapiItemModel.ModelSupplier() {
            @Override
            public List<MiapiModel> getModels(@Nullable String key, ItemModule.ModuleInstance model, ItemStack stack) {
                JsonElement element = model.getProperties().get(property);
                List<MiapiModel> models = new ArrayList<>();
                if (element != null) {
                    element.getAsJsonArray().forEach(element1 -> {
                        ModelJson modelJson = Miapi.gson.fromJson(element1, ModelJson.class);
                        Supplier<ItemStack> stackSupplier = (()->{
                            if ("item_nbt".equals(modelJson.type)) {
                                //Miapi.LOGGER.warn(modelJson.model);
                                NbtCompound itemCompound = stack.getOrCreateNbt().getCompound(modelJson.model);
                                if(!itemCompound.isEmpty()){
                                    //Miapi.LOGGER.warn(stack.getOrCreateNbt().toString());
                                    ItemStack modelItemstack = ItemStack.fromNbt(stack.getOrCreateNbt().getCompound(modelJson.model));
                                    //Miapi.LOGGER.warn("rendering ItemModel "+modelItemstack.getTranslationKey());
                                    return modelItemstack;
                                }
                            }
                            //Miapi.LOGGER.warn("rendering Empty ItemModel");
                            return ItemStack.EMPTY;
                        });
                        ItemMiapiModel miapiModel = new ItemMiapiModel(stackSupplier, modelJson.transform.toMatrix());
                        models.add(miapiModel);
                    });
                }
                return models;
            }
        });
    }

    @Override
    public boolean load(String moduleKey, JsonElement data) throws Exception {
        return true;
    }

    static class ModelJson {
        public String type;
        public String model;
        public Transform transform = Transform.IDENTITY;
    }
}