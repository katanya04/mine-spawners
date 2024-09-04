package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;

/**
 * Configuration screen of the mod
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    public Screen parent;
    public ConfigScreen(Screen parent) {
        super(Text.of("Minespawners Configuration"));
        this.parent = parent;
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
    }
}
