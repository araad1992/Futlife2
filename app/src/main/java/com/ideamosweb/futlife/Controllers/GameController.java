package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.GamePreference;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 9/11/16.
 * Función:
 */
public class GameController {

    private DatabaseHelper helper;
    private Context context;

    public void gameController(){}

    public GameController(Context context){
        this.context = context;
        gameController();
    }

    //Funcion que permite la creacion de un nuevo juego
    public boolean create(Game game){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            gameDao.create(game);
        } catch (Exception ex) {
            res = false;
            Log.e("GameController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la edicion de un juego
    public boolean update(Game game){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            gameDao.update(game);
        } catch (Exception ex) {
            res = false;
            Log.e("GameController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar todos los juegos
    public List<Game> show(){
        List<Game> games;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            games = gameDao.queryForAll();
        } catch (Exception ex) {
            games = null;
            Log.e("GameController(show)", "Error: " + ex.getMessage());
        }
        return games;
    }

    //Funcion que permite mostrar todos los juegos
    public List<Game> show(List<GamePreference> preferences){
        List<Game> games = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            for (int i = 0; i < preferences.size(); i++) {
                Game game = gameDao.queryBuilder().where().eq("game_id", preferences.get(i).getGame_id()).query().get(0);
                games.add(game);
            }
        } catch (Exception ex) {
            Log.e("GameController(show)", "Error: " + ex.getMessage());
        }
        return games;
    }

    //Funcion que permite mostrar todos los juegos
    public Game show(int code){
        Game game;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            game = gameDao.queryForId(code);
        } catch (Exception ex) {
            game = null;
            Log.e("GameController(show)", "Error: " + ex.getMessage());
        }
        return game;
    }

    //Funcion que permite mostrar un juego por su id
    public Game find(int game_id){
        Game game = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            List<Game> games = gameDao.queryBuilder().where().eq("game_id", game_id).query();
            if(!games.isEmpty()) {
                game = games.get(0);
            }
        } catch (Exception ex) {
            Log.e("GameController(find)", "Error: " + ex.getMessage());
        }
        return game;
    }

    //Funcion que permite mostrar toda las consolas
    public List<Game> selects(){
        List<Game> consoles;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            consoles = gameDao.queryBuilder().where().eq("select", true).query();
        } catch (Exception ex) {
            consoles = null;
            Log.e("GameController(show)", "Error: " + ex.getMessage());
        }
        return consoles;
    }

    public boolean stocks(){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            if(gameDao.queryForAll().size() > 0) {
                res = true;
            }
        } catch (Exception ex) {
            Log.e("GameController(stocks)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite eliminar un juego de la base de datos
    public boolean delete(Game game){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Game, Integer> gameDao = helper.getGameRuntimeDao();
            gameDao.delete(game);
        } catch (Exception ex) {
            res = false;
            Log.e("GameController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
