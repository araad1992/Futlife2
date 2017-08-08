package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.List;

/**
 * Creado por Deimer Villa on 31/10/16.
 * Función:
 */
public class UserController {

    private DatabaseHelper helper;
    private Context context;

    public void userController(){}

    public UserController(Context context){
        this.context = context;
        userController();
    }

    //Funcion que permite la creacion de un usuario nuevo
    public boolean create(User user){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            userDao.create(user);
        } catch (Exception ex) {
            res = false;
            Log.e("UserController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la edicion de un usuario
    public boolean update(User user){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> usuarioDao = helper.getUserRuntimeDao();
            usuarioDao.update(user);
        } catch (Exception ex) {
            res = false;
            Log.e("UserController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar toda la informacion del usuario logueado
    public User show(){
        User user;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            user = userDao.queryForId(1);
        } catch (Exception ex) {
            user = null;
            Log.e("UserController(show)", "Error: " + ex.getMessage());
        }
        return user;
    }

    //Funcion que permite mostrar toda la informacion del usuario logueado
    public User get(int code){
        User user;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            user = userDao.queryForId(code);
        } catch (Exception ex) {
            user = null;
            Log.e("UserController(show)", "Error: " + ex.getMessage());
        }
        return user;
    }

    //Función que permite obtener a todos los usuarios de la app excepto el logueado
    public List<User> all(){
        List<User> players;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            players = userDao.queryBuilder().where().ne("code", 1).query();
        }catch (Exception ex) {
            players = null;
            Log.e("UserController(delete)", "Error: " + ex.getMessage());
        }
        return players;
    }

    //Funcion que permite eliminar un Usuario de la base de datos
    public boolean delete(User user){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            userDao.delete(user);
        } catch (Exception ex) {
            res = false;
            Log.e("UserController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite saber si hay una sesion iniciada
    public boolean session(){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> usuarioDao = helper.getUserRuntimeDao();
            int cantidad = (int)usuarioDao.countOf();
            if(cantidad > 0){
                res = true;
            }
        } catch (Exception ex) {
            Log.e("UserController(session)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite reiniciar la base de datos al cerrar una sesion del usuario
    public boolean logout(){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> usuarioDao = helper.getUserRuntimeDao();
            int id = show().getCode();
            usuarioDao.deleteById(id);
            helper.onResetDataBase();
        }catch (Exception ex){
            res = false;
            Log.e("UserController(logout)", "Error: " + ex.toString());
        }
        return res;
    }

    public boolean changeToken(String new_token){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<User, Integer> userDao = helper.getUserRuntimeDao();
            User user = userDao.queryForId(1);
            user.setToken(new_token);
            userDao.update(user);
        } catch (Exception ex) {
            res = false;
            Log.e("UserController(changeToken)", "Error: " + ex.toString());
        }
        return res;
    }

}
