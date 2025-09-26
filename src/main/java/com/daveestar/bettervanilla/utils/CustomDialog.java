package com.daveestar.bettervanilla.utils;

import java.util.ArrayList;
import java.util.List;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogBase.DialogAfterAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback.Options;
import net.md_5.bungee.api.ChatColor;

public class CustomDialog {
  public static Dialog createConfirmationDialog(String title, String message, String errorMessage,
      List<DialogInput> inputs,
      DialogActionCallback yesCallback, DialogActionCallback noCallback) {
    boolean hasError = errorMessage != null && !errorMessage.isEmpty();

    DialogBody dialogBody1 = DialogBody.plainMessage(Component.text(""));
    DialogBody dialogBody2 = DialogBody
        .plainMessage(Component.text(ChatColor.RED + "" + ChatColor.BOLD + "» " + ChatColor.YELLOW + title));
    DialogBody dialogBody3 = DialogBody.plainMessage(Component.text(ChatColor.GRAY + message));
    DialogBody dialogBody4 = DialogBody.plainMessage(Component.text(""));
    DialogBody dialogBodyError = DialogBody.plainMessage(Component.text(ChatColor.RED + errorMessage));
    DialogBody dialogBodyErrorSpacer = DialogBody.plainMessage(Component.text(""));

    List<DialogBody> body = new ArrayList<>(List.of(dialogBody1, dialogBody2, dialogBody3, dialogBody4));
    if (hasError) {
      body.add(dialogBodyError);
      body.add(dialogBodyErrorSpacer);
    }

    DialogBase dialogBase = DialogBase.builder(Component.text(""))
        .inputs(inputs)
        .body(body)
        .afterAction(DialogAfterAction.CLOSE)
        .build();

    if (yesCallback == null) {
      yesCallback = (view, audience) -> {
      };
    }

    DialogAction yesAction = DialogAction.customClick(yesCallback, Options.builder().uses(1).build());
    ActionButton yesButton = ActionButton.builder(Component.text("Apply"))
        .action(yesAction).build();

    if (noCallback == null) {
      noCallback = (view, audience) -> {
      };
    }

    DialogAction noAction = DialogAction.customClick(noCallback, Options.builder().uses(1).build());
    ActionButton noButton = ActionButton.builder(Component.text("Cancel")).action(noAction).build();

    DialogType dialogType = DialogType.confirmation(yesButton, noButton);

    return Dialog
        .create(builder -> builder.empty()
            .base(dialogBase)
            .type(dialogType));
  }

  public static DialogInput createTextInput(String key, String label, String initialValue) {
    return DialogInput
        .text(key, Component.text(label))
        .initial(initialValue)
        .maxLength(Integer.MAX_VALUE)
        .build();
  }
}
