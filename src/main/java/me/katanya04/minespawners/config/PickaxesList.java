package me.katanya04.minespawners.config;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PickaxesList extends ElementListWidget<PickaxesList.Entry> {
    public static final int BUTTON_SIZE = 20;
    public static final int BUTTON_MARGIN = 10;
    private final ConfigScreen configScreen;

    public PickaxesList(ConfigScreen configScreen, MinecraftClient minecraft) {
        super(minecraft, configScreen.width, configScreen.layout.getContentHeight() - (configScreen.slider.createWidget(null).getHeight() + 10),
                configScreen.layout.getHeaderHeight() + configScreen.slider.createWidget(null).getHeight() + 10, BUTTON_SIZE + BUTTON_MARGIN);
        this.configScreen = configScreen;
        setEntries();
    }

    protected void setEntries() {
        this.clearEntries();
        this.addEntry(new TitleEntry(Text.translatable("config.blacklisted_pickaxes")));
        int initialX = (getRowWidth() - getButtonsPerRow() * (BUTTON_MARGIN + BUTTON_SIZE) + BUTTON_MARGIN) / 2 + 25;
        for (int i = 0; i < configScreen.pickaxes.size(); i += getButtonsPerRow()) {
            this.addEntry(new RowEntry(configScreen.pickaxes.subList(i, Math.min(i + getButtonsPerRow(), configScreen.pickaxes.size())), initialX));
        }
    }

    @Override
    public void position(int width, ThreePartsLayoutWidget layout) {
        this.position(width, layout.getContentHeight() - (configScreen.slider.createWidget(null).getHeight() + 10),
                layout.getHeaderHeight() + configScreen.slider.createWidget(null).getHeight() + 10);
        setEntries();
        refreshScroll();
    }

    @Override
    public int getRowWidth() {
        return configScreen.width - 50;
    }

    public int getButtonsPerRow() {
        return getRowWidth() / (BUTTON_SIZE + BUTTON_MARGIN);
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ElementListWidget.Entry<Entry> {}

    @Environment(EnvType.CLIENT)
    public class RowEntry extends Entry {
        private final PickaxeButton[] buttons;
        
        public RowEntry(List<Item> pickaxes, int initialX) {
            this.buttons = new PickaxeButton[pickaxes.size()];
            int x = initialX, i = 0;
            for (Item pickaxe : pickaxes) {
                buttons[i++] = new PickaxeButton(x, 0, BUTTON_SIZE, pickaxe, PickaxesList.this.configScreen);
                x += BUTTON_SIZE + BUTTON_MARGIN;
            }
        }

        @Override
        public @NotNull List<? extends Selectable> selectableChildren() {
            return Arrays.asList(this.buttons);
        }

        @Override
        public void render(
                DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress
        ) {
            Arrays.stream(this.buttons).forEach(b -> {
                b.setY(y);
                b.render(context, mouseX, mouseY, tickProgress);
            });
        }

        @Override
        public @NotNull List<? extends Element> children() {
            return Arrays.asList(this.buttons);
        }
    }

    @Environment(EnvType.CLIENT)
    public class TitleEntry extends Entry {
        final Text title;
        private final int width;

        public TitleEntry(final Text title) {
            this.title = title;
            this.width = PickaxesList.this.client.textRenderer.getWidth(this.title);
        }

        @Override
        public @NotNull List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable() {
                @Override
                public Selectable.SelectionType getType() {
                    return Selectable.SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, TitleEntry.this.title);
                }
            });
        }

        @Override
        public void render(
                DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress
        ) {
            context.drawText(
                    PickaxesList.this.client.textRenderer, this.title, PickaxesList.this.width / 2 - this.width / 2,
                    y + entryHeight / 2 - PickaxesList.this.client.textRenderer.fontHeight / 2, -1, true
            );
        }

        @Override
        public @NotNull List<? extends Element> children() {
            return List.of();
        }
    }
}
