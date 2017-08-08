package com.ideamosweb.futlife.Controllers;

import android.content.Context;
import android.util.Log;
import com.ideamosweb.futlife.Database.DatabaseHelper;
import com.ideamosweb.futlife.Models.Report;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import java.util.ArrayList;
import java.util.List;

/**
 * Creado por Deimer Villa on 8/06/17.
 * Función:
 */
public class ReportController {

    private DatabaseHelper helper;
    private Context context;

    public void reportController(){}

    public ReportController(Context context){
        this.context = context;
        reportController();
    }

    //Funcion que permite la creacion de un nuevo reporte
    public int create(Report report_temp){
        int res = 0;
        try {
            helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> reportDao = helper.getReportRuntimeDao();
            Report report = exists(report_temp.getReport_id());
            if(report == null) {
                reportDao.create(report_temp);
                res = 1;
            } else {
                reportDao.update(report);
                res = 2;
            }

        } catch (Exception ex) {
            Log.e("ReportController(create)", "Error: " + ex.getMessage());
        }
        return res;
    }

    public Report exists(int report_id){
        Report report = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> playerDao = helper.getReportRuntimeDao();
            List<Report> reports = playerDao.queryBuilder().where().eq("report_id", report_id).query();
            if(!reports.isEmpty()) {
                report = reports.get(0);
            }
        } catch (Exception ex) {
            Log.e("ReportController(exists)", "Error: " + ex.getMessage());
        }
        return report;
    }

    //Funcion que permite la edicion de un reporte
    public boolean update(Report report){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> reportDao = helper.getReportRuntimeDao();
            reportDao.update(report);
        } catch (Exception ex) {
            res = false;
            Log.e("ReportController(update)", "Error: " + ex.getMessage());
        }
        return res;
    }

    //Funcion que permite mostrar un reporte por su id
    public Report show(int report_id){
        Report report = null;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> reportDao = helper.getReportRuntimeDao();
            List<Report> reports = reportDao.queryBuilder().where().eq("report_id", report_id).query();
            if(!reports.isEmpty()) {
                report = reports.get(0);
            }
        } catch (Exception ex) {
            Log.e("ReportController(show)", "Error: " + ex.getMessage());
        }
        return report;
    }

    //Funcion que permite mostrar todos los reportes
    public List<Report> show(){
        List<Report> reports = new ArrayList<>();
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> reportDao = helper.getReportRuntimeDao();
            reports = reportDao.queryBuilder().orderBy("report_id", false).query();
        } catch (Exception ex) {
            Log.e("ReportController(show-list)", "Error: " + ex.getMessage());
        }
        return reports;
    }

    //Funcion que permite eliminar un reporte de la base de datos
    public boolean delete(Report report){
        boolean res = true;
        try {
            helper = OpenHelperManager.getHelper(context,DatabaseHelper.class);
            RuntimeExceptionDao<Report, Integer> reportDao = helper.getReportRuntimeDao();
            reportDao.delete(report);
        } catch (Exception ex) {
            res = false;
            Log.e("ReportController(delete)", "Error: " + ex.getMessage());
        }
        return res;
    }

}
