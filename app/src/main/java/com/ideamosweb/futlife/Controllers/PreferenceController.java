package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.GamePreference;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 23/11/16.
 * Función:
 */
public class PreferenceController {

    private DatabaseHelper helper;
    private Context context;

    public void preferenceController(){}

    public PreferenceController(Context context){
        this.context = context;
        preferenceController();
    }

    //Funcion que permite la creacion de una consola y los juegos asosiados a esta
    public boolean create(ConsolePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preferenceDao.createOrUpdate(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(create(console))", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la creacion de un juego relacionado a una consola
    public boolean create(GamePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferenceDao.createOrUpdate(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(create(game))", "Error: " + ex.getMessage());
        }
        return res;
    }

    public ConsolePreference find(int preference_id){
        ConsolePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            List<ConsolePreference> consoles = preferenceDao
                    .queryBuilder()
                    .where().eq("preference_id", preference_id)
                    .query();
            if(consoles.isEmpty()){
                preference = null;
            } else {
                preference = consoles.get(0);
            }
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(find)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    public boolean stockConsoles(){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            res = preferenceDao.queryBuilder().where().eq("active", true).query().size() > 0;
        } catch (Exception ex) {
            Log.e("PreferenceController(stockConsoles)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public boolean stockGames(int user_id){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            res = preferenceDao.queryBuilder().where().eq("active", true).and().eq("user_id", user_id).query().size() > 0;
        } catch (Exception ex) {
            Log.e("PreferenceController(stockGames)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public String playerIdConsolePreference(int user_id, int console_id){
        String player_id;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            player_id = preferenceDao
                    .queryBuilder()
                    .where().eq("console_id", console_id)
                    .and().eq("user_id", user_id)
                    .query().get(0).getPlayer_id();
        } catch (Exception ex) {
            player_id = null;
            Log.e("PreferenceController(playerIdConsolePreference)", "Error: " + ex.getMessage());
        }
        return player_id;
    }

    public ConsolePreference existConsole(int user_id, int console_id){
        ConsolePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            List<ConsolePreference> consoles = preferenceDao
                    .queryBuilder()
                    .where().eq("console_id", console_id)
                    .and().eq("user_id", user_id)
                    .query();
            if(consoles.isEmpty()){
                preference = null;
            } else {
                preference = consoles.get(0);
            }
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(existConsole)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    //Funcion que permite mostrar el nombre de todas las consolas
    public List<String> showNames(boolean flag){
        List<String> names = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            if(flag) {
                RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
                List<ConsolePreference> consoles = preferenceDao.queryBuilder().where().eq("active", true).query();
                for (int i = 0; i < consoles.size(); i++) {
                    names.add(consoles.get(i).getName());
                }
            } else {
                RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
                List<GamePreference> games = preferenceDao.queryBuilder().where().eq("active", true).query();
                for (int i = 0; i < games.size(); i++) {
                    names.add(games.get(i).getName());
                }
            }
        } catch (Exception ex) {
            Log.e("PreferenceController(showNames)", "Error: " + ex.getMessage());
        }
        return names;
    }

    public ConsolePreference findForNameConsole(String console_name){
        ConsolePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            List<ConsolePreference> preferences = preferenceDao
                    .queryBuilder()
                    .where().eq("name", console_name)
                    .query();
            if(preferences.isEmpty()) {
                preference = null;
            } else {
                preference = preferences.get(0);
                System.out.println("prefrencia: " + preference.toString());
            }
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(existConsole)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    public List<ConsolePreference> findForNameConsoles(int id, String console_name){
        List<ConsolePreference> preferences = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preferences = preferenceDao
                    .queryBuilder()
                    .groupBy("user_id")
                    .where().eq("name", console_name)
                    .and().not().eq("user_id", id)
                    .query();
        } catch (Exception ex) {
            Log.e("PreferenceController(existConsoles)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    public GamePreference findForNameGame(String game_name){
        GamePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            List<GamePreference> preferences = preferenceDao
                    .queryBuilder()
                    .where().eq("name", game_name)
                    .query();
            if(preferences.isEmpty()) {
                preference = null;
            } else {
                preference = preferences.get(0);
            }
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(findForNameGame)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    public List<GamePreference> findForNameGames(int id, String game_name){
        List<GamePreference> preferences = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferences = preferenceDao
                    .queryBuilder()
                    .groupBy("user_id")
                    .where().eq("name", game_name)
                    .and().not().eq("user_id", id)
                    .query();
        } catch (Exception ex) {
            Log.e("PreferenceController(findForNameGames)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    public GamePreference existGame(int game_id, int console_id, int user_id){
        GamePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            List<GamePreference> games = preferenceDao.queryBuilder()
                    .where().eq("game_id", game_id)
                    .and().eq("console_id", console_id)
                    .and().eq("user_id", user_id)
                    .query();
            if (games.isEmpty( )) {
                preference = null;
            }else {
                preference = games.get(0);
            }
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(existGame)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    //Funcion que permite la edicion de un juego
    public boolean update(ConsolePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preferenceDao.update(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(update(console))", "Error: " + ex.getMessage());
        }
        return res;
    }

    public List<GamePreference> list(int game_id, int user_id){
        List<GamePreference> preferences;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferences = preferenceDao.queryBuilder()
                    .where().eq("game_id", game_id)
                    .and().eq("user_id", user_id)
                    .query();
        } catch (Exception ex) {
            preferences = null;
            Log.e("PreferenceController(list(Game))", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    public List<String> listGames(int console_id){
        List<String> names = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            List<GamePreference> games = preferenceDao.queryBuilder().where().eq("console_id", console_id).query();
            for (int i = 0; i < games.size(); i++) {
                names.add(games.get(i).getName());
            }
        } catch (Exception ex) {
            Log.e("PreferenceController(listGames)", "Error: " + ex.getMessage());
        }
        return names;
    }

    public List<GamePreference> selectedGame(int game_id, int user_id){
        List<GamePreference> preferences = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferences = preferenceDao.queryBuilder()
                    .where().eq("user_id", user_id)
                    .and().eq("game_id", game_id)
                    .and().eq("active", true)
                    .query();
        } catch (Exception ex) {
            Log.e("PreferenceController(selectedGame)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    //Funcion que permite la edicion de un juego
    public boolean update(GamePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferenceDao.update(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(update(game))", "Error: " + ex.getMessage());
        }
        return res;
    }

    public List<ConsolePreference> consoles(int user_id){
        List<ConsolePreference> preferences;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preferences = preferenceDao.queryBuilder()
                    .where().eq("user_id", user_id)
                    .and()
                    .eq("active", true)
                    .query();
        } catch (Exception ex) {
            preferences = null;
            Log.e("PreferenceController(consoles)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    public ConsolePreference consolesValidate(int user_id, int console_id){
        ConsolePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preference = preferenceDao.queryBuilder()
                    .where().eq("user_id", user_id)
                    .and().eq("console_id", console_id)
                    .and().eq("active", true)
                    .query().get(0);
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(consoles)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    public GamePreference gameValidate(int user_id, int console_id, int game_id){
        GamePreference preference;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preference = preferenceDao.queryBuilder()
                    .where().eq("user_id", user_id)
                    .and().eq("console_id", console_id)
                    .and().eq("game_id", game_id)
                    .and().eq("active", true)
                    .query().get(0);
        } catch (Exception ex) {
            preference = null;
            Log.e("PreferenceController(consoles)", "Error: " + ex.getMessage());
        }
        return preference;
    }

    public List<GamePreference> games(int user_id, int console_id){
        List<GamePreference> preferences;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferences = preferenceDao.queryBuilder()
                    .where().eq("user_id", user_id)
                    .and()
                    .eq("console_id", console_id)
                    .and()
                    .eq("active", true)
                    .query();
        } catch (Exception ex) {
            preferences = null;
            Log.e("PreferenceController(games)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    public List<GamePreference> indexGame(int user_id){
        List<GamePreference> preferences;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferences = preferenceDao.queryBuilder()
                    .groupBy("game_id")
                    .where().eq("user_id", user_id)
                    .and()
                    .eq("active", true)
                    .query();
        } catch (Exception ex) {
            preferences = null;
            Log.e("PreferenceController(indexGame)", "Error: " + ex.getMessage());
        }
        return preferences;
    }

    //Funcion que permite eliminar una consola de un usuario
    public boolean delete(ConsolePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            preferenceDao.delete(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(delete(consoles))", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite validar la existencia de una preferencia plana


    //Funcion que permite eliminar un juego de un usuario
    public boolean delete(GamePreference preference){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            preferenceDao.delete(preference);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(delete(games))", "Error: " + ex.getMessage());
        }
        return res;
    }

    public boolean clearConsolePreference(int user_id){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<ConsolePreference, Integer> preferenceDao = helper.getConsolePreferenceRuntimeDao();
            List<ConsolePreference> consoles = preferenceDao.queryForEq("user_id", user_id);
            preferenceDao.delete(consoles);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(clearTable)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public boolean clearGamePreference(int user_id){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<GamePreference, Integer> preferenceDao = helper.getGamePreferenceRuntimeDao();
            List<GamePreference> games = preferenceDao.queryForEq("user_id", user_id);
            preferenceDao.delete(games);
        } catch (Exception ex) {
            res = false;
            Log.e("PreferenceController(clearTable)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
