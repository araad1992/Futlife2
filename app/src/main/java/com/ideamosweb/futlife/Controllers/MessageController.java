package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Message;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 18/04/17.
 * Función:
 */
public class MessageController {

    private DatabaseHelper helper;
    private Context context;

    public void messageController(){}

    public MessageController(Context context){
        this.context = context;
        messageController();
    }

    //Funcion que permite la creacion de un nuevo mensaje en la base de datos
    public boolean create(Message message){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            messageDao.create(message);
        } catch (Exception ex) {
            res = false;
            Log.e("MessageController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite la actualizacion de un mensaje
    public boolean update(Message message){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            messageDao.update(message);
        } catch (Exception ex) {
            res = false;
            Log.e("MessageController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar todos los mensajes
    public List<Message> show(int player_one, int player_two) {
        List<Message> messages = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            messages = messageDao.queryBuilder()
                    .orderBy("message_id", true)
                    .where().eq("from_user", player_one)
                    .or().eq("to_user", player_one)
                    .and()
                    .eq("from_user", player_two)
                    .or().eq("to_user", player_two)
                    .query();
        } catch (Exception ex) {
            Log.e("MessageController(show)", "Error: " + ex.getMessage());
        }
        return messages;
    }

    //Funcion que permite mostrar un mensaje unico
    public Message show(int message_id){
        Message message = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            List<Message> messages = messageDao.queryBuilder().where().eq("message_id", message_id).query();
            if(!messages.isEmpty()) {
                message = messages.get(0);
            }
        } catch (Exception ex) {
            Log.e("MessageController(show)", "Error: " + ex.getMessage());
        }
        return message;
    }

    //Funcion que permite mostrar un mensaje unico por codigo local
    public Message find(int code){
        Message message = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            message = messageDao.queryForId(code);
        } catch (Exception ex) {
            Log.e("MessageController(show)", "Error: " + ex.getMessage());
        }
        return message;
    }

    //Funcion que permite eliminar un mensaje de la base de datos
    public boolean delete(Message message){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Message, Integer> messageDao = helper.getMessageRuntimeDao();
            messageDao.delete(message);
        } catch (Exception ex) {
            res = false;
            Log.e("MessageController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
