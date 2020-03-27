package me.wolfyscript.customcrafting.gui.main_gui.buttons;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiHandler;
import me.wolfyscript.utilities.api.inventory.button.ButtonState;
import me.wolfyscript.utilities.api.inventory.button.buttons.DummyButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class PatronButton extends DummyButton {

    private String minecraftName, name;
    private UUID uuid;

    private ItemStack head;

    public PatronButton(String name, String minecraftName, String uuid, ItemStack head) {
        super("patron." + name.replace(" ", "_").toLowerCase(Locale.ROOT), new ButtonState("", Material.PLAYER_HEAD, null));
        this.minecraftName = minecraftName;
        this.name = name;

        if (!uuid.isEmpty() && head == null) {
            this.uuid = UUID.fromString(uuid);
            try {
                URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "") + "?unsigned=false");
                InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
                JsonObject textureProperty = (JsonObject) new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0);
                this.head = WolfyUtilities.getSkullByValue(textureProperty.get("value").getAsString());

            } catch (IOException e) {
                System.err.println("Could not get skin data from session servers!");
            }
        } else if (head != null) {
            this.head = head;
        }
        if (this.head == null) {
            this.head = new ItemStack(Material.CREEPER_HEAD);
        }

        ItemMeta itemMeta = this.head.getItemMeta();
        itemMeta.setDisplayName("§6§l" + name);
        if (!minecraftName.isEmpty()) {
            itemMeta.setLore(Arrays.asList("§8aka. " + minecraftName));
        }
        this.head.setItemMeta(itemMeta);

    }

    public PatronButton(String name, String minecraftName, String uuid) {
        this(name, minecraftName, uuid, null);
    }

    public PatronButton(String name, String minecraftName, ItemStack head) {
        this(name, minecraftName, "", head);
    }

    public PatronButton(String name, String minecraftName) {
        this(name, minecraftName, "", null);
    }

    @Override
    public void render(GuiHandler guiHandler, Player player, Inventory inventory, int slot, boolean help) {

        inventory.setItem(slot, head);
    }
}
