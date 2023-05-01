package smartin.miapi.item.modular.properties.render;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.ItemStack;
import smartin.miapi.Miapi;
import smartin.miapi.client.model.DynamicBakedModel;
import smartin.miapi.item.modular.ItemModule;
import smartin.miapi.item.modular.Transform;
import smartin.miapi.item.modular.TransformStack;
import smartin.miapi.item.modular.properties.ModuleProperty;
import smartin.miapi.item.modular.properties.SlotProperty;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelMergeProperty implements ModuleProperty {
    public static final String KEY = "modelMerge";
    public static ModuleProperty property;

    public ModelMergeProperty() {
        property = this;
        ModelProperty.modelTransformers.add(
                new ModelProperty.ModelTransformer() {

                    @Override
                    public List<ModelProperty.TransformedUnbakedModel> unBakedTransform(List<ModelProperty.TransformedUnbakedModel> list, ItemStack stack) {
                        ItemModule.ModuleInstance root = ItemModule.getModules(stack);
                        List<Json> toMerge = new ArrayList<>();
                        root.allSubModules().forEach(moduleInstance -> {
                            JsonElement data = moduleInstance.getProperties().get(property);
                            if (data != null) {
                                Type type = new TypeToken<List<Json>>() {
                                }.getType();
                                List<Json> jsonDatas = Miapi.gson.fromJson(data, type);
                                jsonDatas.forEach(jsonData -> {
                                    TransformStack transformStack = SlotProperty.getTransformStack(SlotProperty.getSlotIn(moduleInstance));
                                    if (jsonData.transform == null) {
                                        jsonData.transform = Transform.IDENTITY;
                                    } else {
                                        jsonData.transform.origin = null;
                                    }
                                    Transform from = transformStack.get(jsonData.from).copy();
                                    from = from.merge(jsonData.transform);
                                    jsonData.transform = from;
                                    toMerge.add(jsonData);
                                });
                            }
                        });

                        List<ModelProperty.TransformedUnbakedModel> newList = new ArrayList<>(list);
                        list.forEach(unbakedModel -> {
                            toMerge.forEach(json -> {
                                if (json.from.equals(unbakedModel.transform().primary)) {
                                    TransformStack stack1 = unbakedModel.transform().copy();
                                    stack1.add(json.to, json.transform);
                                    stack1.primary = json.to;
                                    stack1.add(json.to, json.transform);
                                    ModelProperty.TransformedUnbakedModel transformedUnbakedModel1 = new ModelProperty.TransformedUnbakedModel(stack1, unbakedModel.unbakedModel(), unbakedModel.instance());
                                    newList.add(transformedUnbakedModel1);
                                }
                            });
                        });
                        return newList;
                    }
                });
    }

    @Override
    public boolean load(String moduleKey, JsonElement data) throws Exception {
        return true;
    }

    static class Json {
        public String from;
        public String to;
        public Transform transform;
    }
}
