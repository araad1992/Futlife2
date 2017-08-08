package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Balance;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.List;

/**
 * Creado por Deimer Villa on 5/05/17.
 * Función:
 */
public class RechargeController {

    private DatabaseHelper helper;
    private Context context;

    public void rechangeController() {}

    public RechargeController(Context context) {
        this.context = context;
        rechangeController();
    }

    //Funcion que permite la creacion de una rercarga
    public int create(Balance balance_temp){
        int res = 0;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            Balance balance = exists(balance_temp.getBalance_id());
            if(balance == null) {
                balanceDao.create(balance_temp);
                res = 1;
            } else {
                balanceDao.update(balance);
                res = 2;
            }
        } catch (Exception ex) {
            Log.e("RechargeController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public Balance exists(int balance_id){
        Balance balance = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> playerDao = helper.getBalanceRuntimeDao();
            List<Balance> balances = playerDao.queryBuilder().where().eq("balance_id", balance_id).query();
            if(!balances.isEmpty()) {
                balance = balances.get(0);
            }
        } catch (Exception ex) {
            Log.e("RechargeController(exists)", "Error: " + ex.getMessage());
        }
        return balance;
    }

    //Funcion que permite la edicion de una recarga
    public boolean update(Balance balance){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            balanceDao.update(balance);
        } catch (Exception ex) {
            res = false;
            Log.e("RechargeController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar la recarga actual
    public Balance get(){
        Balance Balance;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            Balance = balanceDao.queryForId(1);
        } catch (Exception ex) {
            Balance = null;
            Log.e("RechargeController(show)", "Error: " + ex.getMessage());
        }
        return Balance;
    }

    //Funcion que permite mostrar todas las recargas
    public List<Balance> show(){
        List<Balance> balances;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            balances = balanceDao.queryForAll();
        } catch (Exception ex) {
            balances = null;
            Log.e("RechargeController(show)", "Error: " + ex.getMessage());
        }
        return balances;
    }

    //FUncion para obtener una recarga por su id
    public Balance show(int balance_id){
        Balance balance;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            List<Balance> challenges = balanceDao.queryBuilder()
                    .where()
                    .eq("balance_id", balance_id)
                    .query();
            if(challenges.isEmpty()) {
                balance = null;
            } else {
                balance = challenges.get(0);
            }
        } catch (Exception ex) {
            balance = null;
            Log.e("RechargeController(show)", "Error: " + ex.getMessage());
        }
        return balance;
    }

    //Funcion que permite eliminar una recarga de la base de datos
    public boolean delete(Balance balance){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Balance, Integer> balanceDao = helper.getBalanceRuntimeDao();
            balanceDao.delete(balance);
        } catch (Exception ex) {
            res = false;
            Log.e("RechargeController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
