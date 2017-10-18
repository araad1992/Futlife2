package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Challenge;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 23/03/17.
 * Función:
 */
public class ChallengeController {

    private DatabaseHelper helper;
    private Context context;

    public void challengeController() {}

    public ChallengeController(Context context) {
        this.context = context;
        challengeController();
    }

    //Funcion que permite la creacion de un nuevo reto
    public boolean create(Challenge challenge){
        boolean res = true;
        try {
            //helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challengeDao.create(challenge);
        } catch (Exception ex) {
            res = false;
            Log.e("ChallengeController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la edicion de un reto
    public boolean update(Challenge challenge){
        boolean res = true;
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challengeDao.update(challenge);
        } catch (Exception ex) {
            res = false;
            Log.e("ChallengeController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar toda los retos
    public List<Challenge> show(){
        List<Challenge> challenges = null;
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challenges = challengeDao.queryForAll();
        } catch (Exception ex) {
            challenges = null;
            Log.e("ChallengeController(show)", "Error: " + ex.getMessage());
        }
        return challenges;
    }

    public Challenge show(int challenge_id){
        Challenge challenge = null;
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //List<Challenge> challenges = challengeDao.queryBuilder()
            //        .where()
            //        .eq("challenge_id", challenge_id)
            //        .query();
            //if(challenges.isEmpty()) {
            //    challenge = null;
            //} else {
            //    challenge = challenges.get(0);
            //}
        } catch (Exception ex) {
            challenge = null;
            Log.e("ChallengeController(show)", "Error: " + ex.getMessage());
        }
        return challenge;
    }

    public Challenge show(int user_id, int challenge_id){
        Challenge challenge = null;
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //List<Challenge> challenges = challengeDao.queryBuilder()
            //        .where()
            //        .eq("player_one", user_id)
            //        .and()
            //        .eq("challenge_id", challenge_id)
            //        .query();
            //if(challenges.isEmpty()) {
            //    challenge = null;
            //} else {
            //    challenge = challenges.get(0);
            //}
        } catch (Exception ex) {
            challenge = null;
            Log.e("ChallengeController(show)", "Error: " + ex.getMessage());
        }
        return challenge;
    }

    public List<Challenge> list(int user_id){
        List<Challenge> challenges = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challenges = challengeDao.queryBuilder()
            //        .orderBy("updated_at", false)
            //        .where()
            //        .eq("player_one", user_id)
            //        .or().eq("player_two", user_id)
            //        .query();
        } catch (Exception ex) {
            Log.e("ChallengeController(list)", "Error: " + ex.getMessage());
        }
        return challenges;
    }

    public List<Challenge> get(){
        List<Challenge> challenges = new ArrayList<>();
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challenges = challengeDao.queryBuilder()
            //        .orderBy("challenge_id", false)
            //        .where()
            //        .eq("type", "abierto")
            //        .and().eq("state", "en espera")
            //        .query();
        } catch (Exception ex) {
            challenges = new ArrayList<>();
            Log.e("ChallengeController(get)", "Error: " + ex.getMessage());
        }
        return challenges;
    }

    //Funcion que permite eliminar una consola de la base de datos
    public boolean delete(Challenge challenge){
        boolean res = true;
        try {
            //helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            //RuntimeExceptionDao<Challenge, Integer> challengeDao = helper.getChallengeRuntimeDao();
            //challengeDao.delete(challenge);
        } catch (Exception ex) {
            res = false;
            Log.e("ChallengeController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
