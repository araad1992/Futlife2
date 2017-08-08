package com.ideamosweb.futlife.Utils;

import android.content.Context;
import android.widget.Toast;
import es.dmoral.toasty.Toasty;

/**
 * Creado por Deimer Villa on 19/01/17.
 * Función:
 */
public class ToastMessages {

    //Variable para alerta asincronica
    private Context context;

    public void toasts(){}

    public ToastMessages(Context context){
        this.context = context;
        toasts();
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void toastNormal(String message) {
        Toasty.normal(context, message).show();
    }

    public void toastInfo(String message){
        Toasty.info(context, message, Toast.LENGTH_LONG, true).show();
    }

    public void toastSuccess(String message){
        Toasty.success(context, message, Toast.LENGTH_LONG, true).show();
    }

    public void toastWarning(String message) {
        Toasty.warning(context, message, Toast.LENGTH_LONG, true).show();
    }

    public void toastError(String message){
        Toasty.error(context, message, Toast.LENGTH_LONG, true).show();
    }

}
