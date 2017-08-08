package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 9/11/16.
 * Función:
 */
public class ConsoleController {

    private DatabaseHelper helper;
    private Context context;

    public void consoleController(){}

    public ConsoleController(Context context){
        this.context = context;
        consoleController();
    }

    //Funcion que permite la creacion de una consola nueva
    public boolean create(Console console){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            consoleDao.create(console);
        } catch (Exception ex) {
            res = false;
            Log.e("ConsoleController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la edicion de una console
    public boolean update(Console console){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            consoleDao.update(console);
        } catch (Exception ex) {
            res = false;
            Log.e("ConsoleController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar toda las consolas
    public List<Console> show(){
        List<Console> consoles;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            consoles = consoleDao.queryForAll();
        } catch (Exception ex) {
            consoles = null;
            Log.e("ConsoleController(show)", "Error: " + ex.getMessage());
        }
        return consoles;
    }

    //Funcion que permite mostrar toda las consolas
    public List<Console> show(List<ConsolePreference> preferences){
        List<Console> consoles = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            for (int i = 0; i < preferences.size(); i++) {
                Console console = consoleDao.queryBuilder().where().eq("console_id", preferences.get(i).getConsole_id()).query().get(0);
                consoles.add(console);
            }
        } catch (Exception ex) {
            Log.e("ConsoleController(show)", "Error: " + ex.getMessage());
        }
        return consoles;
    }

    public Console find(int code_console){
        Console console;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            console = consoleDao.queryForId(code_console);
        } catch (Exception ex) {
            console = null;
            Log.e("ConsoleController(find)", "Error: " + ex.getMessage());
        }
        return console;
    }

    //Funcion que permite mostrar todos los juegos
    public Console show(int console_id){
        Console console;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            List<Console> consoles = consoleDao.queryBuilder().where().eq("console_id", console_id).query();
            if(consoles.isEmpty()) {
                console = null;
            } else {
                console = consoles.get(0);
            }
        } catch (Exception ex) {
            console = null;
            Log.e("ConsoleController(show)", "Error: " + ex.getMessage());
        }
        return console;
    }

    //Funcion que permite mostrar toda las consolas
    public List<Console> listSelects(){
        List<Console> consoles;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            consoles = consoleDao.queryBuilder().where().eq("select", true).query();
        } catch (Exception ex) {
            consoles = null;
            Log.e("ConsoleController(show)", "Error: " + ex.getMessage());
        }
        return consoles;
    }

    public boolean stocks(){
        boolean res = false;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            if(consoleDao.queryForAll().size() > 0) {
                res = true;
            }
        } catch (Exception ex) {
            Log.e("ConsoleController(stocks)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite eliminar una consola de la base de datos
    public boolean delete(Console console){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Console, Integer> consoleDao = helper.getConsoleRuntimeDao();
            consoleDao.delete(console);
        } catch (Exception ex) {
            res = false;
            Log.e("ConsoleController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
