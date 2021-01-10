package me.wolfyscript.customcrafting.handlers;

import me.wolfyscript.customcrafting.CustomCrafting;
import me.wolfyscript.customcrafting.configs.MainConfig;
import me.wolfyscript.customcrafting.recipes.Types;
import me.wolfyscript.customcrafting.recipes.types.ICustomRecipe;
import me.wolfyscript.customcrafting.utils.ChatUtils;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.api.config.ConfigAPI;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import me.wolfyscript.utilities.api.network.database.sql.SQLDataBase;
import me.wolfyscript.utilities.libraries.com.fasterxml.jackson.core.JsonProcessingException;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseHandler extends SQLDataBase {

    private final CustomCrafting customCrafting;
    private final WolfyUtilities api;
    private final Chat chat;
    private final ConfigAPI configAPI;
    private final LanguageAPI languageAPI;
    private final MainConfig mainConfig;

    public DataBaseHandler(WolfyUtilities api, MainConfig mainConfig, CustomCrafting customCrafting) {
        super(api, mainConfig.getDatabaseHost(), mainConfig.getDatabaseSchema(), mainConfig.getDatabaseUsername(), mainConfig.getDatabasePassword(), mainConfig.getDatabasePort());
        this.api = WolfyUtilities.get(customCrafting);
        this.chat = api.getChat();
        this.customCrafting = customCrafting;
        this.configAPI = api.getConfigAPI();
        this.languageAPI = api.getLanguageAPI();
        this.mainConfig = customCrafting.getConfigHandler().getConfig();
        init();
    }

    public void init() {
        try {
            PreparedStatement itemsTable = open().prepareStatement("CREATE TABLE IF NOT EXISTS customcrafting_items(rNamespace VARCHAR(255) null, rKey VARCHAR(255) null, rData LONGTEXT null, constraint customcrafting_items_namespacekey UNIQUE (rNamespace, rKey));");
            itemsTable.executeUpdate();
            itemsTable.close();
            PreparedStatement recipesTable = open().prepareStatement("CREATE TABLE IF NOT EXISTS customcrafting_recipes(rNamespace VARCHAR(255) null, rKey VARCHAR(255) null, rType TINYTEXT null, rData LONGTEXT null, constraint customcrafting_items_namespacekey UNIQUE (rNamespace, rKey));");
            recipesTable.executeUpdate();
            recipesTable.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadRecipes(RecipeHandler recipeHandler) throws SQLException {
        chat.sendConsoleMessage("$msg.startup.recipes.recipes$");
        PreparedStatement recipesQuery = open().prepareStatement("SELECT * FROM customcrafting_recipes");
        ResultSet resultSet = recipesQuery.executeQuery();
        if (resultSet == null) {
            return;
        }
        while (resultSet.next()) {
            String namespace = resultSet.getString("rNamespace");
            String key = resultSet.getString("rKey");
            NamespacedKey namespacedKey = new NamespacedKey(namespace, key);
            chat.sendConsoleMessage("> " + namespacedKey.toString());
            ICustomRecipe<?> recipe = getRecipe(namespacedKey);
            if (recipe != null) {
                recipeHandler.registerRecipe(recipe);
            } else {
                chat.sendConsoleMessage("Error loading recipe \"" + namespacedKey.toString() + "\". Couldn't find recipe in DataBase!");
            }
        }
        recipesQuery.close();
        close();
    }

    public void loadItems() throws SQLException {
        chat.sendConsoleMessage("$msg.startup.recipes.items$");
        PreparedStatement itemsQuery = open().prepareStatement("SELECT * FROM customcrafting_items");
        ResultSet resultSet = itemsQuery.executeQuery();
        if (resultSet == null) return;
        while (resultSet.next()) {
            String namespace = resultSet.getString("rNamespace");
            String key = resultSet.getString("rKey");
            String data = resultSet.getString("rData");
            if (namespace != null && key != null && data != null && !data.equals("{}")) {
                chat.sendConsoleMessage("> " + namespace + ":" + key);
                try {
                    Registry.CUSTOM_ITEMS.register(new NamespacedKey(namespace, key), JacksonUtil.getObjectMapper().readValue(data, CustomItem.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                chat.sendConsoleMessage("Error loading item \"" + namespace + ":" + key + "\". Invalid namespacedkey or data!");
            }
        }
        itemsQuery.close();
        close();
    }

    public boolean hasRecipe(NamespacedKey namespacedKey) {
        try {
            ResultSet resultSet = getRecipeData(namespacedKey);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getRecipeData(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = open().prepareStatement("SELECT rType, rData FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            return executeQuery(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ICustomRecipe<?> getRecipe(NamespacedKey namespacedKey) {
        ResultSet resultSet = getRecipeData(namespacedKey);
        try {
            while (resultSet.next()) {
                String typeID = resultSet.getString("rType");
                String data = resultSet.getString("rData");
                try {
                    return Types.valueOf(typeID).getInstance(namespacedKey, JacksonUtil.getObjectMapper().readTree(data));
                } catch (Exception ex) {
                    ChatUtils.sendRecipeItemLoadingError(namespacedKey.getNamespace(), namespacedKey.getKey(), typeID, ex);
                }
            }
            resultSet.getStatement().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addRecipe(ICustomRecipe<?> data) {
        try {
            PreparedStatement pState = open().prepareStatement("INSERT INTO customcrafting_recipes (rNamespace, rKey, rType, rData) VALUES (?, ?, ?, ?)");
            pState.setString(1, data.getNamespacedKey().getNamespace());
            pState.setString(2, data.getNamespacedKey().getKey());
            pState.setString(3, data.getRecipeType().getId());
            pState.setString(4, JacksonUtil.getObjectMapper().writeValueAsString(data));
            executeAsyncUpdate(pState);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(ICustomRecipe<?> data) {
        if (hasRecipe(data.getNamespacedKey())) {
            try {
                PreparedStatement pState = open().prepareStatement("UPDATE customcrafting_recipes SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, data.getNamespacedKey().getNamespace());
                pState.setString(3, data.getNamespacedKey().getKey());
                executeAsyncUpdate(pState);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            addRecipe(data);
        }
    }

    public void removeRecipe(String namespace, String key) {
        try {
            PreparedStatement pState = open().prepareStatement("DELETE FROM customcrafting_recipes WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespace);
            pState.setString(2, key);
            executeAsyncUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasItem(NamespacedKey namespacedKey) {
        try {
            ResultSet resultSet = getItem(namespacedKey);
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet getItem(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = open().prepareStatement("SELECT rData FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            return pState.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addItem(NamespacedKey namespacedKey, CustomItem data) {
        try {
            PreparedStatement pState = open().prepareStatement("INSERT INTO customcrafting_items (rNamespace, rKey, rData) VALUES (?, ?, ?)");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            pState.setString(3, JacksonUtil.getObjectMapper().writeValueAsString(data));
            executeAsyncUpdate(pState);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void updateItem(NamespacedKey namespacedKey, CustomItem data) {
        if (hasItem(namespacedKey)) {
            try {
                PreparedStatement pState = open().prepareStatement("UPDATE customcrafting_items SET rData=? WHERE rNamespace=? AND rKey=?");
                pState.setString(1, JacksonUtil.getObjectMapper().writeValueAsString(data));
                pState.setString(2, namespacedKey.getNamespace());
                pState.setString(3, namespacedKey.getKey());
                executeAsyncUpdate(pState);
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
            }
        } else {
            addItem(namespacedKey, data);
        }
    }

    public void removeItem(NamespacedKey namespacedKey) {
        try {
            PreparedStatement pState = open().prepareStatement("DELETE FROM customcrafting_items WHERE rNamespace=? AND rKey=?");
            pState.setString(1, namespacedKey.getNamespace());
            pState.setString(2, namespacedKey.getKey());
            executeAsyncUpdate(pState);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
