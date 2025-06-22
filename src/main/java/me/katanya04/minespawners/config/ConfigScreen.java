package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DoubleOptionSliderWidget;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;

import java.util.List;

/**
 * The mod configuration screen, accesible from the "Mods" button in the main menu.
 * The configuration applies to clientside/single player... When using the mod serverside
 * only, just modify the value on the config toml file.
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends GameOptionsScreen {
    protected DoubleOption slider;
    protected ClickableWidget sliderWidget;
    protected final List<Item> pickaxes;
    protected PickaxesList pickaxesList;

    protected ConfigScreen(Screen previousScreen) {
        super(previousScreen, null, new TranslatableText("config.title"));
        this.pickaxes = SimpleConfig.getAllPickaxes().stream().sorted(
                (p1, p2) -> weirdRounding(getHarvestLevel(p1) - getHarvestLevel(p2))
        ).toList();
        this.slider = new DoubleOption(
                "config.drop_chance",
                0.0,
                1.0,
                0.01f,
                gameOptions -> (double) SimpleConfig.DROP_CHANCE.getValue(),
                (gameOptions, value) -> SimpleConfig.DROP_CHANCE.setValue(value),
                (gameOptions, thisOption) -> new TranslatableText("config.drop_chance")
                        .append(": " + String.format("%.0f", thisOption.get(gameOptions) * 100.0) + "%"),
                client -> List.of()

        );
        this.sliderWidget = new DoubleOptionSliderWidget(null, this.width / 2 - 155, this.height / 6 - 12 + 22, 310, 20, this.slider, List.of()) {
            @Override
            protected void applyValue() {
                slider.set(this.options, slider.getValue(this.value));
            }
        };
    }

    protected int weirdRounding(double x) {
        return (int) (x > 0 ? Math.ceil(x) : Math.floor(x));
    }

    protected double getHarvestLevel(Item pickaxe) {
        return pickaxe.getMiningSpeedMultiplier(null, Blocks.SPAWNER.getDefaultState()) * pickaxe.getMaxDamage();
    }

    @Override
    protected void init() {
        this.pickaxesList = new PickaxesList(this, this.client, this.slider.createButton(null, this.width / 2 - 155, this.height / 6 - 12 + 22, width));
        this.addDrawableChild(pickaxesList);
        this.sliderWidget = new DoubleOptionSliderWidget(null, this.width / 2 - 155, this.height / 6 - 12 + 22, 310, 20, this.slider, List.of()) {
            @Override
            protected void applyValue() {
                slider.set(this.options, slider.getValue(this.value));
            }
        };
        this.addDrawableChild(sliderWidget);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, ScreenTexts.DONE, button -> close()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
    }

    @Override
    public void close() {
        SimpleConfig.saveToFile();
        super.close();
    }
}