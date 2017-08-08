package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Social;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * Creado por Deimer Villa on 23/02/17.
 * Función:
 */
public class SocialController {

    private DatabaseHelper helper;
    private Context context;

    public void socialController(){}

    public SocialController(Context context){
        this.context = context;
        socialController();
    }

    //Funcion que permite la creacion de un usuario nuevo
    public boolean create(Social social){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Social, Integer> userDao = helper.getSocialRuntimeDao();
            userDao.create(social);
        } catch (Exception ex) {
            res = false;
            Log.e("SocialController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar toda la informacion del usuario logueado
    public Social show(){
        Social social;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Social, Integer> socialDao = helper.getSocialRuntimeDao();
            social = socialDao.queryForId(1);
        } catch (Exception ex) {
            social = null;
            Log.e("SocialController(show)", "Error: " + ex.getMessage());
        }
        return social;
    }

}
