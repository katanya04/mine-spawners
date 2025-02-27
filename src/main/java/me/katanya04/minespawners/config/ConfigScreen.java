package me.katanya04.minespawners.config;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Configuration screen of the mod
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    static final NumberFormat formatter = NumberFormat.getInstance(Locale.US);
    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
    }
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
        try {
            SimpleConfig.saveToFile();
        } catch (IOException e) {
            System.err.println("IO Exception while saving minespawners config values: " + e);
            e.printStackTrace();
        }
        if (this.client != null)
            this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        TextWidget titleDrop = new TextWidget(MutableText.of(new LiteralTextContent("Drop chance: ")), this.client.textRenderer);
        titleDrop.setX(15);
        titleDrop.setY(15);
        this.addDrawableChild(titleDrop);
        this.customDrawableList.add(titleDrop);

        SliderWidget dropChance = new SliderWidget(titleDrop.getX() + titleDrop.getWidth() + 15, 15 + this.client.textRenderer.fontHeight / 2 - 20 / 2, 100, 20,
                MutableText.of(new LiteralTextContent(formatter.format(SimpleConfig.DROP_CHANCE.getValue() * 100) + " %")), SimpleConfig.DROP_CHANCE.getValue()) {
            @Override
            protected void updateMessage() {
                this.setMessage(MutableText.of(new LiteralTextContent(formatter.format(SimpleConfig.DROP_CHANCE.getValue() * 100) + " %")));
            }
            @Override
            protected void applyValue() {
                SimpleConfig.DROP_CHANCE.setValue(Float.parseFloat(formatter.format(this.value)));
            }
        };
        dropChance.setTooltip(Tooltip.of(Text.of(SimpleConfig.DROP_CHANCE.tooltip), Text.of(SimpleConfig.DROP_CHANCE.tooltip)));
        this.addDrawableChild(dropChance);
        this.customDrawableList.add(dropChance);
        ButtonWidget returnButton = ButtonWidget.builder(Text.of("Done"), (btn) -> this.close()).dimensions(width / 2 - 40,
                (int) (height * 0.9 - 20), 80, 20).tooltip(Tooltip.of(Text.literal(
                        "Press to save configuration and return to previous menu"))).build();
        this.addDrawableChild(returnButton);
        this.customDrawableList.add(returnButton);
    }
}
