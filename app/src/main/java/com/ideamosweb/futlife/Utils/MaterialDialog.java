package com.ideamosweb.futlife.Utils;

import android.app.AlertDialog;
import android.content.Context;
import com.ideamosweb.futlife.R;
import cn.refactor.lib.colordialog.PromptDialog;
import dmax.dialog.SpotsDialog;

/**
 * Creado por Deimer Villa on 25/10/16.
 * Función: Clase encargada de generar los mensajes comunicativos de la app
 */
public class MaterialDialog {

    //Variable para alerta asincronica
    AlertDialog progressDialog;
    private Context contexto;

    public void dialogs(){}

    public MaterialDialog(Context contexto){
        this.contexto = contexto;
        dialogs();
    }

    public void setContext(Context contexto){
        this.contexto = contexto;
    }

    public void dialogBasic(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_DEFAULT)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void dialogInfo(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void dialogSuccess(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void dialogWarnings(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void dialogErrors(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    //Funcion que lanza una alerta asincronica para los procesos que requieren tiempo
    public void dialogProgress(String title){
        progressDialog = new SpotsDialog(contexto, title, R.style.CustomDialog);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //Funcion que permite cancelar una alerta asincronica
    public void cancelProgress(){
        progressDialog.cancel();
    }

    //Funcion que permite agregar opciones en los dialogs
    public void dialogQuestion(String title, String message){
        new PromptDialog(contexto).setDialogType(PromptDialog.DIALOG_TYPE_DEFAULT)
                .setTitleText(title)
                .setContentText(message)
                .setAnimationEnable(true)
                .show();
    }

}
