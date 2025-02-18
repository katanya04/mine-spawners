package me.katanya04.minespawners.config;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Configuration screen of the mod
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    private final List<Drawable> customDrawableList = Lists.newArrayList();
    public Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.of("Minespawners Configuration"));
        this.parent = parent;
    }

    /**
     * Override render method because on 1.20 and 1.20.1 it doesn't draw the background, but it does on 1.20.2 (unify behaviour)
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.client != null && this.client.world != null) {
            context.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.renderBackgroundTexture(context);
        }
        for (Drawable drawable : this.customDrawableList) {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public void close() {
        if (parent != null && this.client != null)
            this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        int x1 = width / 30, y1 = height / 30;
        int mainMargin = width / 50, spacing = width / 50;
        int index = 0;
        for (ConfigValue<?> c : SimpleConfig.values) {
            InputWidget inputWidget = new InputWidget(x1 + mainMargin, y1 + mainMargin + spacing * index, c, textRenderer);
            this.addDrawableChild(inputWidget);
            this.customDrawableList.add(inputWidget);
            index++;
        }
        ButtonWidget returnButton = ButtonWidget.builder(Text.of("Done"), (btn) -> {
            try {
                SimpleConfig.saveToFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not save config data for minespawners mod: " + e);
            } finally {
                this.close();
            }
        }).dimensions(width / 2 - 40, (int) (height * 0.9 - 20), 80, 20).tooltip(
                Tooltip.of(Text.literal("Press to save configuration and return to previous menu"))).build();
        this.addDrawableChild(returnButton);
        this.customDrawableList.add(returnButton);
    }
}
