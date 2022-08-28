package com.t2pellet.teams.client.ui.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.t2pellet.teams.client.TeamsKeys;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

public abstract class RespondableTeamToast extends TeamToast {

    private boolean responded = false;

    public RespondableTeamToast(String team) {
        super(team);
    }

    public void respond() {
        responded = true;
    }

    @Override
    public String subTitle() {
        String rejectKey = TeamsKeys.REJECT.getLocalizedName();
        String acceptKey = TeamsKeys.ACCEPT.getLocalizedName();
        return I18n.get("teams.toast.respond", rejectKey, acceptKey);
    }

    @NotNull
    @Override
    public Visibility render(@NotNull MatrixStack matrices, @NotNull ToastGui manager, long startTime) {
        if (responded) {
            return Visibility.HIDE;
        }
        return super.render(matrices, manager, startTime);
    }
}
