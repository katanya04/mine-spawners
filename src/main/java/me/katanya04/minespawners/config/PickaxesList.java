package me.katanya04.minespawners.config;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PickaxesList extends ContainerObjectSelectionList<PickaxesList.@NotNull Entry> {
    public static final int BUTTON_SIZE = 20;
    public static final int BUTTON_MARGIN = 10;
    private final ConfigScreen configScreen;

    public PickaxesList(ConfigScreen configScreen, Minecraft minecraft) {
        super(minecraft, configScreen.width, configScreen.layout.getContentHeight() - (configScreen.slider.createButton(null).getHeight() + 10),
                configScreen.layout.getHeaderHeight() + configScreen.slider.createButton(null).getHeight() + 10, BUTTON_SIZE + BUTTON_MARGIN);
        this.configScreen = configScreen;
        setEntries();
    }

    protected void setEntries() {
        this.clearEntries();
        this.addEntry(new TitleEntry(Component.translatable("config.blacklisted_pickaxes")));
        int initialX = (getRowWidth() - getButtonsPerRow() * (BUTTON_MARGIN + BUTTON_SIZE) + BUTTON_MARGIN) / 2 + 25;
        for (int i = 0; i < configScreen.pickaxes.size(); i += getButtonsPerRow()) {
            this.addEntry(new RowEntry(configScreen.pickaxes.subList(i, Math.min(i + getButtonsPerRow(), configScreen.pickaxes.size())), initialX));
        }
    }

    @Override
    public void updateSize(int width, HeaderAndFooterLayout layout) {
        this.updateSizeAndPosition(width, layout.getContentHeight() - (configScreen.slider.createButton(null).getHeight() + 10),
                layout.getHeaderHeight() + configScreen.slider.createButton(null).getHeight() + 10);
        setEntries();
        refreshScrollAmount();
    }

    @Override
    public int getRowWidth() {
        return configScreen.width - 50;
    }

    public int getButtonsPerRow() {
        return getRowWidth() / (BUTTON_SIZE + BUTTON_MARGIN);
    }

    @Environment(EnvType.CLIENT)
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<me.katanya04.minespawners.config.PickaxesList.@NotNull Entry> {}

    @Environment(EnvType.CLIENT)
    public class RowEntry extends me.katanya04.minespawners.config.PickaxesList.Entry {
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
        public @NotNull List<? extends NarratableEntry> narratables() {
            return Arrays.asList(this.buttons);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return Arrays.asList(this.buttons);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            Arrays.stream(this.buttons).forEach(b -> {
                b.setY(this.getY());
                b.render(context, mouseX, mouseY, deltaTicks);
            });
        }
    }

    @Environment(EnvType.CLIENT)
    public class TitleEntry extends me.katanya04.minespawners.config.PickaxesList.Entry {
        final Component title;
        private final int width;

        public TitleEntry(final Component title) {
            this.title = title;
            this.width = PickaxesList.this.minecraft.font.width(this.title);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Override
                public NarratableEntry.@NotNull NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput builder) {
                    builder.add(NarratedElementType.TITLE, TitleEntry.this.title);
                }
            });
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            context.drawString(
                    PickaxesList.this.minecraft.font, this.title, PickaxesList.this.width / 2 - this.width / 2,
                    this.getY() + this.getHeight() / 2 - PickaxesList.this.minecraft.font.lineHeight / 2, -1, true
            );
        }
    }
}
