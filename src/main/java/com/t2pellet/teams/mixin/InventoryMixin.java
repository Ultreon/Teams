package com.t2pellet.teams.mixin;

import com.t2pellet.teams.TeamsMod;
import com.t2pellet.teams.client.TeamsModClient;
import com.t2pellet.teams.client.core.ClientTeam;
import com.t2pellet.teams.client.ui.menu.TeamsLonelyScreen;
import com.t2pellet.teams.client.ui.menu.TeamsMainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public class InventoryMixin extends Screen {

    // TODO : Fix team button position not updating when the recipe book is opened / closed

    private static final ResourceLocation TEAMS_BUTTON_TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/buttonsmall.png");

    @Shadow private float xMouse;
    @Shadow private float yMouse;

    protected InventoryMixin(ITextComponent title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo info) {
        if (!Objects.requireNonNull(TeamsModClient.client.gameMode).hasInfiniteItems()) {
            InventoryAccessor screen = ((InventoryAccessor) ((Object) this));
            addButton(new ImageButton(screen.getLeftPos() + screen.getImageWidth() - 19, screen.getTopPos() + 4, 15, 14, 0, 0, 13, TEAMS_BUTTON_TEXTURE, (button) -> {
                if (ClientTeam.INSTANCE.isInTeam()) {
                    TeamsModClient.client.setScreen(new TeamsMainScreen(TeamsModClient.client.screen));

                } else {
                    TeamsModClient.client.setScreen(new TeamsLonelyScreen(TeamsModClient.client.screen));
                }
            }));
        }
    }
}
