package smartin.miapi.modules.edit_options;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import smartin.miapi.client.gui.InteractAbleWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EditOptionIcon extends InteractAbleWidget {
    Identifier texture;
    EditOption editOption;
    Consumer<EditOption> select;
    Supplier<EditOption> getSelected;
    int u;
    int v;
    int textureWidth;
    int textureHeight;
    String langKey = "";

    public EditOptionIcon(int x, int y, int width, int height, Consumer<EditOption> select, Supplier<EditOption> getSelected, Identifier textureIdentifier, int u, int v, int textureHeight, int textureWidth, EditOption option) {
        this(x, y, width, height, select, getSelected, textureIdentifier, u, v, textureHeight, textureWidth, "", option);
    }

    public EditOptionIcon(int x, int y, int width, int height, Consumer<EditOption> select, Supplier<EditOption> getSelected, Identifier textureIdentifier, int u, int v, int textureHeight, int textureWidth, String langKey, EditOption option) {
        super(x, y, width, height, Text.empty());
        this.select = select;
        this.getSelected = getSelected;
        this.u = u;
        this.v = v;
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
        texture = textureIdentifier;
        editOption = option;
        this.langKey = langKey;
    }

    public boolean isSelected() {
        return editOption == getSelected.get();
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        float vOffset = 0;
        if (isSelected()) {
            vOffset = this.getHeight();
        }
        drawContext.drawTexture(texture, getX(), getY(), (float) u, vOffset + v, getWidth(), getHeight(), textureWidth, textureHeight);
        if (!langKey.equals("")) {
            if (isMouseOver(mouseX, mouseY)) {
                drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable(langKey), mouseX, mouseY);
            }
        }
        //drawContext.drawTooltip(MinecraftClient.getInstance().textRenderer, Text.translatable(langKey), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && !isSelected()) {
            select.accept(editOption);
            return true;
        }
        return false;
    }
}
