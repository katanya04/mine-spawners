package me.katanya04.minespawners.config;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.item.Item;

/**
 * A square button with an image of a pickaxe, upon clicking toggles the blacklisted state of that pickaxe
 */
@Environment(EnvType.CLIENT)
public class PickaxeButton extends Button {
    public final Item pickaxe;
    public final Screen screen;
    public PickaxeButton(int x, int y, int size, Item pickaxe, Screen screen) {
        super(x, y, size, size, net.minecraft.network.chat.Component.empty(),
                self -> {
                    List<String> blacklistedPickaxes = SimpleConfig.BLACKLISTED_PICKAXES.getValue();
                    if (blacklistedPickaxes.contains(pickaxe.toString())) {
                        blacklistedPickaxes.remove(pickaxe.toString());
                    } else {
                        blacklistedPickaxes.add(pickaxe.toString());
                    }
                    SimpleConfig.BLACKLISTED_PICKAXES.setValue(blacklistedPickaxes);
                },
                supplier ->
                        MutableComponent.create(new PlainTextContents.LiteralContents(pickaxe.getDefaultInstance().getItemName().getString()))
        );
        this.pickaxe = pickaxe;
        this.screen = screen;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        int itemX = this.getX() + (this.width - 16) / 2;
        int itemY = this.getY() + (this.height - 16) / 2;
        context.fakeItem(this.pickaxe.getDefaultInstance(), itemX, itemY);
        if (this.isHovered())
            context.setTooltipForNextFrame(this.screen.getFont(), this.pickaxe.getDefaultInstance(), mouseX, mouseY);
        if (SimpleConfig.BLACKLISTED_PICKAXES.contains(pickaxe.toString())) {
            context.text(this.screen.getFont(),
                    MutableComponent.create(new PlainTextContents.LiteralContents("X")).withStyle(ChatFormatting.BOLD),
                    getX(), getY(), 0xFFFF0000, true);
        }
    }
}
