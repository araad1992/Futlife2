package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Player;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 2/02/17.
 * Función:
 */
public class PlayerController {

    private DatabaseHelper helper;
    private Context context;

    public void playerController(){}

    public PlayerController(Context context){
        this.context = context;
        playerController();
    }

    //Funcion que permite la creacion de un jugador nuevo
    public int create(Player player_tmp){
        int res = 0;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            Player player = exists(player_tmp.getUser_id());
            if(player == null) {
                playerDao.create(player_tmp);
                res = 1;
            } else {
                playerDao.update(player);
                res = 2;
            }
        } catch (Exception ex) {
            Log.e("PlayerController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public Player exists(int user_id){
        Player player = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            List<Player> players = playerDao.queryBuilder().where().eq("user_id", user_id).query();
            if(!players.isEmpty()) {
                player = players.get(0);
            }
        } catch (Exception ex) {
            Log.e("PlayerController(exists)", "Error: " + ex.getMessage());
        }
        return player;
    }

    //Funcion que permite la edición de un jugador
    public boolean update(Player Player){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> jugadorDao = helper.getPlayerRuntimeDao();
            jugadorDao.update(Player);
        } catch (Exception ex) {
            res = false;
            Log.e("PlayerController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar toda la informacion del jugador
    public Player show(int code){
        Player Player;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> PlayerDao = helper.getPlayerRuntimeDao();
            Player = PlayerDao.queryForId(code);
        } catch (Exception ex) {
            Player = null;
            Log.e("PlayerController(show)", "Error: " + ex.getMessage());
        }
        return Player;
    }

    public Player find(int player_id){
        Player player;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            List<Player> players = playerDao.queryBuilder()
                    .where()
                    .eq("user_id", player_id)
                    .query();
            if(players.isEmpty()) {
                player = null;
            } else {
                player = players.get(0);
            }
        } catch (Exception ex) {
            player = null;
            Log.e("PlayerController(find)", "Error: " + ex.getMessage());
        }
        return player;
    }

    public List<Player> search(String keyword) {
        List<Player> players = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            players = playerDao.queryBuilder()
                    .where()
                    .like("name", "%" + keyword + "%")
                    .or()
                    .like("username", "%" + keyword + "%")
                    .or()
                    .like("email", "%" + keyword + "%")
                    .query();
        }catch (Exception ex) {
            Log.e("PlayerController(search)", "Error: " + ex.getMessage());
        }
        return players;
    }

    //Función que permite obtener a todos los jugadors de la app
    public List<Player> get(){
        List<Player> players = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            players = playerDao.queryForAll();
        }catch (Exception ex) {
            Log.e("PlayerController(get)", "Error: " + ex.getMessage());
        }
        return players;
    }

    //Funcion que permite eliminar un jugador de la base de datos
    public boolean delete(Player Player){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Player, Integer> playerDao = helper.getPlayerRuntimeDao();
            playerDao.delete(Player);
        } catch (Exception ex) {
            res = false;
            Log.e("PlayerController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
