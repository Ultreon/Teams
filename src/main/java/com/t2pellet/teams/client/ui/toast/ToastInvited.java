package com.t2pellet.teams.client.ui.toast;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastInvited extends RespondableTeamToast {

    public ToastInvited(String team) {
        super(team);
    }

    @Override
    public String title() {
        return I18n.get("teams.toast.invite", team);
    }

}
