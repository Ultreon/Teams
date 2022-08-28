package com.t2pellet.teams.mixin;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface InventoryAccessor {

    @Accessor("leftPos")
    int getLeftPos();
    @Accessor("topPos")
    int getTopPos();
    @Accessor("imageWidth")
    int getImageWidth();

}
