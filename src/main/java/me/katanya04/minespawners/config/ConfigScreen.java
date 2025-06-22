package me.katanya04.minespawners.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The mod configuration screen, accesible from the "Mods" button in the main menu.
 * The configuration applies to clientside/single player... When using the mod serverside
 * only, just modify the value on the config toml file.
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends GameOptionsScreen {
    protected final SimpleOption<Double> slider;
    protected final List<Item> pickaxes;
    protected PickaxesList pickaxesList;

    protected ConfigScreen(Screen previousScreen) {
        super(previousScreen, null, Text.translatable("config.title"));
        this.slider = new SimpleOption<>(
                "config.drop_chance",
                SimpleOption.emptyTooltip(),
                ConfigScreen::percentValueLabel,
                SimpleOption.DoubleSliderCallbacks.INSTANCE,
                (double) SimpleConfig.DROP_CHANCE.getValue(),
                SimpleConfig.DROP_CHANCE::setValue
        );
        this.pickaxes = SimpleConfig.getAllPickaxes().stream().sorted(
                (p1, p2) -> weirdRounding(getHarvestLevel(p1) - getHarvestLevel(p2))
        ).toList();
    }

    protected int weirdRounding(double x) {
        return (int) (x > 0 ? Math.ceil(x) : Math.floor(x));
    }

    protected double getHarvestLevel(Item pickaxe) {
        return (pickaxe.getDefaultStack().get(DataComponentTypes.TOOL) == null ?
                1 : pickaxe.getDefaultStack().get(DataComponentTypes.TOOL).rules().stream()
                .filter(r -> r.speed().isPresent()).mapToDouble(r -> r.speed().get()).max().orElse(1))
                * (pickaxe.getDefaultStack().get(DataComponentTypes.MAX_DAMAGE) == null ?
                1 : pickaxe.getDefaultStack().get(DataComponentTypes.MAX_DAMAGE));
    }

    private static Text percentValueLabel(Text p_231898_, double p_231899_) {
        return Text.translatable("options.percent_value", p_231898_, (int)(p_231899_ * 100.0));
    }

    @Override
    protected void addOptions() {
        this.body.addSingleOptionEntry(slider);
    }

    @Override
    protected void initBody() {
        this.body = this.layout.addBody(new OptionListWidget(this.client, this.width, this) {
            @Override
            public void position(int width, @NotNull ThreePartsLayoutWidget layout) {
                this.position(width, ConfigScreen.this.slider.createWidget(null).getHeight() + 10, layout.getHeaderHeight());
            }
        });
        this.body.setHeight(slider.createWidget(null).getHeight() + 10);
        this.pickaxesList = this.layout.addBody(new PickaxesList(this, this.client));
        this.addOptions();
    }

    @Override
    protected void refreshWidgetPositions() {
        super.refreshWidgetPositions();
        this.pickaxesList.position(this.width, this.layout);
    }

    @Override
    public void close() {
        SimpleConfig.saveToFile();
        super.close();
    }
}