package com.t2pellet.teams.client.ui.menu;

import com.t2pellet.teams.TeamsMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class TeamsInputScreen extends TeamsScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(TeamsMod.MODID, "textures/gui/smaller_background.png");
    private static final ITextComponent DEFAULT_TEXT = new TranslationTextComponent("teams.menu.input");
    private static final ITextComponent GO_BACK_TEXT = new TranslationTextComponent("teams.menu.return");
    private static final int WIDTH = 120;
    private static final int HEIGHT = 110;

    protected TextFieldWidget inputField;
    protected Button submitButton;
    private String prevInputText = "";

    public TeamsInputScreen(Screen parent, ITextComponent title) {
        super(parent, title);
    }

    @Override
    protected void init() {
        super.init();
        inputField = addButton(new TextFieldWidget(client.font, x + (getWidth() - 100) / 2, y + 10, 100, 20, DEFAULT_TEXT));
        submitButton = addButton(new Button(x + (getWidth() - 100) / 2, y + HEIGHT - 55, 100, 20, getSubmitText(), this::onSubmit));
        submitButton.active = submitCondition();
        addButton(new Button(x + (getWidth() - 100) / 2, y + HEIGHT - 30, 100, 20, GO_BACK_TEXT, button -> {
            client.setScreen(parent);
        }));
    }

    @Override
    public void tick() {
        if (!prevInputText.equals(inputField.getValue())) {
            submitButton.active = submitCondition();
        }
    }

    @Override
    protected int getWidth() {
        return WIDTH;
    }

    @Override
    protected int getHeight() {
        return HEIGHT;
    }

    @Override
    protected ResourceLocation getBackgroundTexture() {
        return TEXTURE;
    }

    @Override
    protected float getBackgroundScale() {
        return 1.0F;
    }

    protected abstract ITextComponent getSubmitText();

    protected abstract void onSubmit(Button widget);

    protected abstract boolean submitCondition();

}
