package com.ideamosweb.futlife.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.ideamosweb.futlife.Models.Console;
import com.ideamosweb.futlife.Models.ConsolePreference;
import com.ideamosweb.futlife.Models.Game;
import com.ideamosweb.futlife.Models.GamePreference;
import com.ideamosweb.futlife.Models.Message;
import com.ideamosweb.futlife.Models.Platform;
import com.ideamosweb.futlife.Models.Balance;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.Models.Social;
import com.ideamosweb.futlife.Models.User;
import com.ideamosweb.futlife.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * Creado por Deimer Villa on 31/10/16.
 * Función:
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "futlife.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    //Tablas de la base de datos
    private Dao<User, Integer> userDao = null;
    private RuntimeExceptionDao<User, Integer> userRuntimeDao = null;
    private Dao<Social, Integer> socialDao = null;
    private RuntimeExceptionDao<Social, Integer> socialRuntimeDao = null;
    private Dao<Balance, Integer> balanceDao = null;
    private RuntimeExceptionDao<Balance, Integer> balanceRuntimeDao = null;
    private Dao<Platform, Integer> platformDao = null;
    private RuntimeExceptionDao<Platform, Integer> platformRuntimeDao = null;
    private Dao<Console, Integer> consoleDao = null;
    private RuntimeExceptionDao<Console, Integer> consoleRuntimeDao = null;
    private Dao<Game, Integer> gameDao = null;
    private RuntimeExceptionDao<Game, Integer> gameRuntimeDao = null;
    private Dao<ConsolePreference, Integer> consolePreferencesDao = null;
    private RuntimeExceptionDao<ConsolePreference, Integer> consolePreferencesRuntimeDao = null;
    private Dao<GamePreference, Integer> gamePreferencesDao = null;
    private RuntimeExceptionDao<GamePreference, Integer> gamePreferencesRuntimeDao = null;
    private Dao<Report, Integer> reportDao = null;
    private RuntimeExceptionDao<Report, Integer> reportRuntimeDao = null;
    private Dao<Message, Integer> messageDao = null;
    private RuntimeExceptionDao<Message, Integer> messageRuntimeDao = null;

    /*Funcion que permite crear la base de datos cuando inicia la aplicacion
    * Usa como parametros;
    * @param sqLiteDatabase -> extension de la base de datos para sqlite
    * @param source -> variable para la conexion a los recursos de sqlite
    */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource source) {
        try {
            TableUtils.createTable(source, User.class);
            TableUtils.createTable(source, Social.class);
            TableUtils.createTable(source, Balance.class);
            TableUtils.createTable(source, ConsolePreference.class);
            TableUtils.createTable(source, GamePreference.class);
            TableUtils.createTable(source, Report.class);
            TableUtils.createTable(source, Message.class);
            //Configuración
            TableUtils.createTable(source, Platform.class);
            TableUtils.createTable(source, Console.class);
            TableUtils.createTable(source, Game.class);
        } catch (SQLException sqlEx) {
            Log.e("DatabaseHelper(onCreate)", "Error: " + sqlEx.getMessage());
            throw new RuntimeException(sqlEx);
        }
    }

    /*Funcion que permite actualizar la base de datos cuando sea necesario
    * Usa como parametros;
    * @param db -> extension de la base de datos
    * @param source -> variable para la conexion a la base de datos
    * @param oldVersion -> numero de version actual de la base de datos
    * @param newVersion -> numero de la nueva version de la base de datos
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(source, User.class, true);
            TableUtils.dropTable(source, Social.class, true);
            TableUtils.dropTable(source, Balance.class, true);
            TableUtils.dropTable(source, Platform.class, true);
            TableUtils.dropTable(source, Console.class, true);
            TableUtils.dropTable(source, Game.class, true);
            TableUtils.dropTable(source, ConsolePreference.class, true);
            TableUtils.dropTable(source, GamePreference.class, true);
            TableUtils.dropTable(source, Report.class, true);
            TableUtils.dropTable(source, Message.class, true);
            onCreate(db, source);
        } catch (SQLException sqlEx) {
            Log.e("DatabaseHelper(onUpgrade)", "Error: " + sqlEx.getMessage());
            Log.e(DatabaseHelper.class.getSimpleName(), "Imposible eliminar la base de datos", sqlEx);
        }
    }

    /*Funcion que permite resetear la base de datos cuando se cierra la sesión del usuario
    * Todos los datos de la aplicación son borrados para evitar información basura.
    * No recibe ningun parametro para funcionar.
    */
    public void onResetDataBase(){
        try {
            //Se eliminan las tablas existentes
            ConnectionSource source = this.getConnectionSource();
            TableUtils.dropTable(source, User.class, true);
            TableUtils.dropTable(source, Social.class, true);
            TableUtils.dropTable(source, Balance.class, true);
            TableUtils.dropTable(source, ConsolePreference.class, true);
            TableUtils.dropTable(source, GamePreference.class, true);
            TableUtils.dropTable(source, Report.class, true);
            TableUtils.dropTable(source, Message.class, true);
            //Recreacion de las tablas
            TableUtils.createTable(source, User.class);
            TableUtils.createTable(source, Social.class);
            TableUtils.createTable(source, Balance.class);
            TableUtils.createTable(source, ConsolePreference.class);
            TableUtils.createTable(source, GamePreference.class);
            TableUtils.createTable(source, Report.class);
            TableUtils.createTable(source, Message.class);
        }catch (SQLException sqlEx){
            Log.e("DatabaseHelper(onResetDataBase)", "Error: " + sqlEx.getMessage());
            throw new RuntimeException(sqlEx);
        }
    }

    public void close(){
        super.close();
        userDao = null;
        userRuntimeDao = null;
        socialDao = null;
        socialRuntimeDao = null;
        balanceDao = null;
        balanceRuntimeDao = null;
        platformDao = null;
        platformRuntimeDao = null;
        consoleDao = null;
        consoleRuntimeDao = null;
        gameDao = null;
        gameRuntimeDao = null;
        consolePreferencesDao = null;
        gamePreferencesDao = null;
        consolePreferencesRuntimeDao = null;
        gamePreferencesRuntimeDao = null;
        reportDao = null;
        reportRuntimeDao = null;
        messageDao = null;
        messageRuntimeDao = null;
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if(userDao == null) userDao = getDao(User.class);
        return userDao;
    }
    public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() {
        if(userRuntimeDao == null) userRuntimeDao = getRuntimeExceptionDao(User.class);
        return userRuntimeDao;
    }

    public Dao<Social, Integer> getSocialDao() throws SQLException {
        if(socialDao == null) socialDao = getDao(Social.class);
        return socialDao;
    }
    public RuntimeExceptionDao<Social, Integer> getSocialRuntimeDao() {
        if(socialRuntimeDao == null) socialRuntimeDao = getRuntimeExceptionDao(Social.class);
        return socialRuntimeDao;
    }

    public Dao<Balance, Integer> getBalanceDao() throws SQLException {
        if(balanceDao == null) balanceDao = getDao(Balance.class);
        return balanceDao;
    }
    public RuntimeExceptionDao<Balance, Integer> getBalanceRuntimeDao() {
        if(balanceRuntimeDao == null) balanceRuntimeDao = getRuntimeExceptionDao(Balance.class);
        return balanceRuntimeDao;
    }

    public Dao<Platform, Integer> getPlatformDao() throws SQLException {
        if(platformDao == null) platformDao = getDao(Platform.class);
        return platformDao;
    }
    public RuntimeExceptionDao<Platform, Integer> getPlatformRuntimeDao() {
        if(platformRuntimeDao == null) platformRuntimeDao = getRuntimeExceptionDao(Platform.class);
        return platformRuntimeDao;
    }

    public Dao<Console, Integer> getConsoleDao() throws SQLException {
        if(consoleDao == null) consoleDao = getDao(Console.class);
        return consoleDao;
    }
    public RuntimeExceptionDao<Console, Integer> getConsoleRuntimeDao() {
        if(consoleRuntimeDao == null) consoleRuntimeDao = getRuntimeExceptionDao(Console.class);
        return consoleRuntimeDao;
    }

    public Dao<Game, Integer> getGameDao() throws SQLException {
        if(gameDao == null) gameDao = getDao(Game.class);
        return gameDao;
    }
    public RuntimeExceptionDao<Game, Integer> getGameRuntimeDao() {
        if(gameRuntimeDao == null) gameRuntimeDao = getRuntimeExceptionDao(Game.class);
        return gameRuntimeDao;
    }

    public Dao<ConsolePreference, Integer> getConsolePreferenceDao() throws SQLException {
        if(consolePreferencesDao == null) consolePreferencesDao = getDao(ConsolePreference.class);
        return consolePreferencesDao;
    }
    public RuntimeExceptionDao<ConsolePreference, Integer> getConsolePreferenceRuntimeDao() {
        if(consolePreferencesRuntimeDao == null) consolePreferencesRuntimeDao = getRuntimeExceptionDao(ConsolePreference.class);
        return consolePreferencesRuntimeDao;
    }

    public Dao<GamePreference, Integer> getGamePreferenceDao() throws SQLException {
        if(gamePreferencesDao == null) gamePreferencesDao = getDao(GamePreference.class);
        return gamePreferencesDao;
    }
    public RuntimeExceptionDao<GamePreference, Integer> getGamePreferenceRuntimeDao() {
        if(gamePreferencesRuntimeDao == null) gamePreferencesRuntimeDao = getRuntimeExceptionDao(GamePreference.class);
        return gamePreferencesRuntimeDao;
    }

    public Dao<Report, Integer> getReportDao() throws SQLException {
        if(reportDao == null) reportDao = getDao(Report.class);
        return reportDao;
    }
    public RuntimeExceptionDao<Report, Integer> getReportRuntimeDao() {
        if(reportRuntimeDao == null) reportRuntimeDao = getRuntimeExceptionDao(Report.class);
        return reportRuntimeDao;
    }

    public Dao<Message, Integer> getMessageDao() throws SQLException {
        if(messageDao == null) messageDao = getDao(Message.class);
        return messageDao;
    }
    public RuntimeExceptionDao<Message, Integer> getMessageRuntimeDao() {
        if(messageRuntimeDao == null) messageRuntimeDao = getRuntimeExceptionDao(Message.class);
        return messageRuntimeDao;
    }

}
