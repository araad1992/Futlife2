package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Platform;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

/**
 * Creado por Deimer Villa on 12/02/17.
 * Función:
 */
public class PlatformController {

    private DatabaseHelper helper;
    private Context context;

    public void platformController(){}

    public PlatformController(Context context){
        this.context = context;
        platformController();
    }

    //Funcion que permite la creacion de una plataforma nueva
    public boolean create(Platform platform){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            platformDao.create(platform);
        } catch (Exception ex) {
            res = false;
            Log.e("PlatformController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la edicion de una plataforma
    public boolean update(Platform platform){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            platformDao.update(platform);
        } catch (Exception ex) {
            res = false;
            Log.e("PlatformController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar todas las plataformas
    public List<Platform> show(){
        List<Platform> platforms;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            platforms = platformDao.queryForAll();
        } catch (Exception ex) {
            platforms = null;
            Log.e("PlatformController(show)", "Error: " + ex.getMessage());
        }
        return platforms;
    }

    //Funcion que permite mostrar una sola plataforma por id
    public Platform show(int platform_id){
        Platform platform;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            List<Platform> platforms = platformDao.queryBuilder().where().eq("platform_id", platform_id).query();
            if(platforms.isEmpty()) {
                platform = null;
            } else {
                platform = platforms.get(0);
            }
        } catch (Exception ex) {
            platform = null;
            Log.e("PlatformController(show)", "Error: " + ex.getMessage());
        }
        return platform;
    }

    public Platform find(int code){
        Platform platform;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            platform = platformDao.queryForId(code);
        } catch (Exception ex) {
            platform = null;
            Log.e("PlatformController(find)", "Error: " + ex.getMessage());
        }
        return platform;
    }

    public boolean stock(){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            if(platformDao.queryForAll().size() > 0) {
                res = true;
            }
        } catch (Exception ex) {
            Log.e("PlatformController(stock)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite eliminar una plataforma de la base de datos
    public boolean delete(Platform platform){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Platform, Integer> platformDao = helper.getPlatformRuntimeDao();
            platformDao.delete(platform);
        } catch (Exception ex) {
            res = false;
            Log.e("PlatformController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
