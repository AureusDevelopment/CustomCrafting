package me.wolfyscript.customcrafting.gui.item_creator.buttons;

import me.wolfyscript.customcrafting.data.TestCache;
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ActionButton;
import me.wolfyscript.utilities.util.inventory.PlayerHeadUtils;
import org.bukkit.attribute.AttributeModifier;

import java.util.Locale;

public class AttributeModeButton extends ActionButton {

    public AttributeModeButton(AttributeModifier.Operation operation, String headURLValue) {
        super("attribute."+operation.toString().toLowerCase(Locale.ROOT), PlayerHeadUtils.getViaURL(headURLValue), (guiHandler, player, inventory, i, inventoryClickEvent) -> {
            ((TestCache) guiHandler.getCustomCache()).getItems().setAttribOperation(operation);
            return true;
        }, (replacements, guiHandler, player, itemStack, i, b) -> {
            replacements.put("%C%", ((TestCache) guiHandler.getCustomCache()).getItems().getAttribOperation().equals(operation) ? "§a" : "§4");
            return itemStack;
        });
    }
}
