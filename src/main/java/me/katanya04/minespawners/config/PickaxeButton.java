package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.List;

/**
 * A square button with an image of a pickaxe, upon clicking toggles the blacklisted state of that pickaxe
 */
@Environment(EnvType.CLIENT)
public class PickaxeButton extends ButtonWidget {
    public final Item pickaxe;
    public final Screen screen;
    public final MinecraftClient client;
    public PickaxeButton(int x, int y, int size, Item pickaxe, Screen screen, MinecraftClient client) {
        super(x, y, size, size, Text.of(""),
                self -> {
                    List<String> blacklistedPickaxes = SimpleConfig.BLACKLISTED_PICKAXES.getValue();
                    if (blacklistedPickaxes.contains(Registry.ITEM.getId(pickaxe).getNamespace() + ":" + Registry.ITEM.getId(pickaxe).getPath())) {
                        blacklistedPickaxes.remove(Registry.ITEM.getId(pickaxe).getNamespace() + ":" + Registry.ITEM.getId(pickaxe).getPath());
                    } else {
                        blacklistedPickaxes.add(Registry.ITEM.getId(pickaxe).getNamespace() + ":" + Registry.ITEM.getId(pickaxe).getPath());
                    }
                    SimpleConfig.BLACKLISTED_PICKAXES.setValue(blacklistedPickaxes);
                }
        );
        this.pickaxe = pickaxe;
        this.screen = screen;
        this.client = client;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        int itemX = this.x + (this.width - 16) / 2;
        int itemY = this.y + (this.height - 16) / 2;
        this.client.getItemRenderer().renderInGui(this.pickaxe.getDefaultStack(), itemX, itemY);
        if (this.isHovered())
            this.screen.renderTooltip(matrices, this.pickaxe.getName(), mouseX, mouseY);
        if (SimpleConfig.BLACKLISTED_PICKAXES.contains(Registry.ITEM.getId(pickaxe).getNamespace() + ":" + Registry.ITEM.getId(pickaxe).getPath())) {
            this.client.textRenderer.draw(matrices,
                    Text.of("X").getWithStyle(Style.EMPTY.withBold(true)).get(0),
                    this.x, this.y, 0xFFFF0000);
        }
    }
}
