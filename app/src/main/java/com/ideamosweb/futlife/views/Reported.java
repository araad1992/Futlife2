package com.ideamosweb.futlife.views;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ideamosweb.futlife.Adapters.RowRecyclerReportAdapter;
import com.ideamosweb.futlife.Controllers.ReportController;
import com.ideamosweb.futlife.Controllers.UserController;
import com.ideamosweb.futlife.Models.Report;
import com.ideamosweb.futlife.R;
import com.ideamosweb.futlife.Service.Api;
import com.ideamosweb.futlife.EventBus.MessageBusReport;
import com.ideamosweb.futlife.EventBus.StationBus;
import com.ideamosweb.futlife.Utils.MaterialDialog;
import com.ideamosweb.futlife.Utils.ToastMessages;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class Reported extends AppCompatActivity {

    private Context context;
    private UserController userController;
    private ReportController reportController;
    private MaterialDialog dialog;
    private ToastMessages toast;
    private Report report;
    private String actual_view;

    //Elementos de la vista
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.lbl_title_activity)TextView lbl_title_activity;
    @Bind(R.id.layout_reported)RelativeLayout layout_reported;
    @Bind(R.id.layout_report_list)RelativeLayout layout_report_list;
    @Bind(R.id.layout_report_info)RelativeLayout layout_report_info;

    @Bind(R.id.lbl_title_proof_report)TextView lbl_title_proof_report;
    @Bind(R.id.lbl_data_not_found)TextView lbl_data_not_found;
    @Bind(R.id.lbl_title_report_info)TextView lbl_title_report_info;
    @Bind(R.id.lbl_description_report)TextView lbl_description_report;
    @Bind(R.id.lbl_attach)TextView lbl_attach;
    @Bind(R.id.txt_description)EditText txt_description;
    @Bind(R.id.img_image_proof)ImageView img_image_proof;
    @Bind(R.id.recycler_reports)RecyclerView recycler;

    @Bind(R.id.lbl_description_report_info)TextView lbl_description_report_info;
    @Bind(R.id.txt_description_report_info)TextView txt_description_report_info;
    @Bind(R.id.lbl_url_youtube_report)TextView lbl_url_youtube_report;
    @Bind(R.id.txt_url_youtube_report)TextView txt_url_youtube_report;
    @Bind(R.id.lbl_image_report)TextView lbl_image_report;
    @Bind(R.id.img_image_report)ImageView img_image_report;
    @Bind(R.id.lbl_image_report_not_found)TextView lbl_image_report_not_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported);
        ButterKnife.bind(this);
        Nammu.init(this);
        setupActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        StationBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        StationBus.getBus().unregister(this);
    }

    @Subscribe
    public void recievedMessage(MessageBusReport messageBusReport){
        boolean active = messageBusReport.isActive();
        int report_id = messageBusReport.getReport_id();
        if(active) {
            report = reportController.show(report_id);
            if(report.getState().equalsIgnoreCase("Pendiente")) {
                actual_view = "reported";
                openPendingState(layout_reported, layout_report_list);
            } else {
                actual_view = "information";
                setupElementsInformation();
                openPendingState(layout_report_info, layout_report_list);
            }
        }
    }

    public void openPendingState(final RelativeLayout layout_in, final RelativeLayout layout_out){
        YoYo.with(Techniques.FadeOut).duration(300).playOn(layout_out);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                layout_out.setVisibility(View.GONE);
                YoYo.with(Techniques.FadeIn).duration(700).playOn(layout_in);
                layout_in.setVisibility(View.VISIBLE);
            }
        }, 400);
    }

    public void setupActivity(){
        context = this;
        userController = new UserController(context);
        reportController = new ReportController(context);
        dialog = new MaterialDialog(context);
        toast = new ToastMessages(context);
        report = new Report();
        actual_view = "";
        setupToolbar();
        setupLayouts();
        setupElements();
        setupLoadCamera();
    }

    public void setupToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setContentInsetStartWithNavigation(0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toBack();
                }
            });
        }
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        lbl_title_activity.setTypeface(bebas_bold);
    }

    public void setupLayouts(){
        List<Report> reports = reportController.show();
        if(reports.isEmpty()) {
            layout_report_list.setVisibility(View.VISIBLE);
            lbl_data_not_found.setVisibility(View.VISIBLE);
        } else if(reports.size() == 1) {
            report = reportController.show().get(0);
            layout_reported.setVisibility(View.VISIBLE);
        } else if(reports.size() > 1) {
            actual_view = "list";
            layout_report_list.setVisibility(View.VISIBLE);
            setupRecycler(reports);
        }
    }

    public void setupElements(){
        Typeface bebas_bold = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_proof_report.setTypeface(bebas_bold);
        lbl_description_report.setTypeface(bebas_regular);
        lbl_data_not_found.setTypeface(bebas_regular);
        txt_description.setTypeface(bebas_regular);
        lbl_attach.setTypeface(bebas_regular);
    }

    public void setupElementsInformation(){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        lbl_title_report_info.setTypeface(bebas_bold);
        lbl_description_report_info.setTypeface(bebas_bold);
        lbl_url_youtube_report.setTypeface(bebas_bold);
        lbl_image_report.setTypeface(bebas_bold);
        txt_description_report_info.setText(report.getDescription());
        txt_description_report_info.setTypeface(bebas_regular);

        if(report.getUrl_youtube() != null || report.getUrl_youtube().isEmpty()) {
            txt_url_youtube_report.setText("Ver video anexado");
            txt_url_youtube_report.setTypeface(bebas_regular);
            txt_url_youtube_report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(report.getUrl_youtube())));
                }
            });
        } else {
            txt_url_youtube_report.setTypeface(bebas_regular);
            txt_url_youtube_report.setText("No hay video anexado al reporte.");
        }

        if(report.getUrl_image() != null) {
            Glide.with(context)
                    .load(report.getUrl_image())
                    .fitCenter()
                    .centerCrop()
                    .placeholder(R.drawable.image_error)
                    .error(R.drawable.image_error)
                    .into(img_image_report);
        } else {
            img_image_report.setVisibility(View.GONE);
            lbl_image_report_not_found.setVisibility(View.VISIBLE);
            lbl_image_report_not_found.setTypeface(bebas_regular);
            lbl_image_report_not_found.setText("No hay imagen anexada al reporte");
        }
    }

    public void setupRecycler(List<Report> reports){
        recycler.removeAllViewsInLayout();
        RowRecyclerReportAdapter adapter = new RowRecyclerReportAdapter(context, reports);
        recycler.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);
        recycler.setVisibility(View.VISIBLE);
    }

    public void setupLoadCamera(){
        EasyImage.configuration(context)
                .setImagesFolderName("Camara Futlife")
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);
    }

    @OnClick(R.id.fab_camera)
    public void openCamera(View view) {
        int permission_camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permission_write_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_read_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission_camera == PackageManager.PERMISSION_GRANTED &&
                permission_write_storage == PackageManager.PERMISSION_GRANTED &&
                permission_read_storage == PackageManager.PERMISSION_GRANTED) {
            EasyImage.openChooserWithGallery(Reported.this, "", 0);
        } else {
            String[] permissions = new String[3];
            permissions[0] = Manifest.permission.CAMERA;
            permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
            permissions[2] = Manifest.permission.READ_EXTERNAL_STORAGE;
            Nammu.askForPermission(this, permissions, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    EasyImage.openChooserWithGallery(Reported.this, "", 0);
                }
                @Override
                public void permissionRefused() {
                    toast.toastWarning("Debe aceptar los permisos para acceder a esta función");
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                toast.toastWarning("Carga de imagen cancelada.");
            }
            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(context);
                    photoFile.deleteOnExit();
                }
            }
            @Override
            public void onImagePicked(File imagesFiles, EasyImage.ImageSource source, int type) {
                loadImageProof(imagesFiles);
            }
        });
    }

    public void loadImageProof(File temp){
        if(temp != null){
            Picasso.with(context)
                    .load(temp)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.avatar_default)
                    .into(img_image_proof);
            YoYo.with(Techniques.FadeIn)
                    .duration(700)
                    .playOn(img_image_proof);
            img_image_proof.setVisibility(View.VISIBLE);
            uploadImageProof(temp);
        }
    }

    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.MINUTES);
        client.setWriteTimeout(5, TimeUnit.MINUTES);
        client.setReadTimeout(5, TimeUnit.MINUTES);
        return client;
    }

    public void uploadImageProof(File file){
        dialog.dialogProgress("Subiendo imagen de prueba...");
        String url = getString(R.string.url_api);
        String token = "Bearer " + userController.show().getToken();
        TypedFile image = new TypedFile("image/*", file);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .setClient(new OkClient(getClient()))
                .build();
        Api api = restAdapter.create(Api.class);
        api.uploadImage(token, report.getReport_id(), image, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                dialog.cancelProgress();
                if (success) {
                    String url_image = jsonObject.get("url_image").getAsString();
                    String message = jsonObject.get("message").getAsString();
                    report.setUrl_image(url_image);
                    toast.toastSuccess(message);
                }
            }
            @Override
            public void failure(RetrofitError error) {
                try {
                    dialog.cancelProgress();
                    errorsRequest(error);
                } catch (Exception ex) {
                    Log.e("Reported(uploadImageProof)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

    public void errorsRequest(RetrofitError retrofitError){
        if(retrofitError.getKind().equals(RetrofitError.Kind.NETWORK)){
            dialog.dialogErrors("Error de conexión", retrofitError.getMessage());
        } else {
            int status = retrofitError.getResponse().getStatus();
            if(status == 400) {
                String error = retrofitError.getBody().toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonErrors = (JsonObject)parser.parse(error);
                String message = jsonErrors.get("message").getAsString();
                dialog.dialogWarnings("¡Alerta!", message);
            } else {
                String message = retrofitError.getMessage();
                dialog.dialogErrors("Error " + status, message);
            }
        }
    }

//region Funciones para lanzar el dialog de youtube

    @OnClick(R.id.fab_youtube)
    public void openDialogYoutube(){
        dialogYouTube();
    }

    public void dialogYouTube(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_youtube, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        setElements(view, alertDialog);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void setElements(View view, final AlertDialog alertDialog){
        Typeface bebas_bold = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_bold.ttf");
        Typeface bebas_regular = Typeface.createFromAsset(context.getAssets(), "fonts/bebas_neue_regular.ttf");
        //Labels
        TextView lbl_title_dialog = (TextView)view.findViewById(R.id.lbl_title_dialog);
        lbl_title_dialog.setTypeface(bebas_bold);
        TextView lbl_url_youtube = (TextView)view.findViewById(R.id.lbl_url_youtube);
        //Edittext
        final EditText txt_video_code = (EditText)view.findViewById(R.id.txt_video_code);
        txt_video_code.setTypeface(bebas_regular);
        //Events
        setUrlYoutube(txt_video_code, lbl_url_youtube);
        //Botones del dialog
        Button but_cancel = (Button)view.findViewById(R.id.but_cancel_dialog);
        Button but_done = (Button)view.findViewById(R.id.but_ok_dialog);
        but_cancel.setTypeface(bebas_regular);
        but_done.setTypeface(bebas_regular);
        //Functions
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        but_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String video_code = txt_video_code.getText().toString().trim();
                if(video_code.isEmpty()) {
                    toast.toastWarning("Debes agregar un codigo de video antes de aceptar");
                } else {
                    report.setUrl_youtube(getString(R.string.dialog_url_full_video) + video_code);
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void setUrlYoutube(final EditText txt_code_video, final TextView lbl_url_youtube){
        txt_code_video.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = txt_code_video.getText().toString().trim();
                if(value.isEmpty()) {
                    lbl_url_youtube.setText(getString(R.string.dialog_url_video_youtube));
                } else {
                    lbl_url_youtube.setText(getString(R.string.dialog_url_video) + value);
                }
            }
        });
    }

//endregion

//region

    @OnClick(R.id.fab_done)
    public void sendInfoReport(){
        String description = txt_description.getText().toString().trim();
        if(description.isEmpty()) {
            toast.toastWarning("Debes agregar una descripción");
        } else {
            report.setDescription(description);
            sendReport();
        }
    }

    public void sendReport(){
        String token = "Bearer " + userController.show().getToken();
        int user_id = userController.show().getUser_id();
        dialog.dialogProgress("Enviando pruebas...");
        String url = getString(R.string.url_api);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .build();
        Api api = restAdapter.create(Api.class);
        api.sendProof(token, report.getReport_id(), user_id, report.getDescription(),
                report.getUrl_youtube(), report.getUrl_image(), report.getState(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                boolean success = jsonObject.get("success").getAsBoolean();
                if(success) {
                    String message = jsonObject.get("message").getAsString();
                    String state = jsonObject.get("state").getAsString();
                    String shipping_date = jsonObject.get("shipping_date").getAsString();
                    report.setState(state);
                    report.setShipping_date(shipping_date);
                    reportController.update(report);
                    toast.toastSuccess(message);
                    setupElementsInformation();
                    actual_view = "information";
                    openPendingState(layout_report_info, layout_reported);
                }
                dialog.cancelProgress();
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.cancelProgress();
                errorsRequest(error);
                try {
                    Log.d("Reported(sendReport)", "Errors body: " + error.getMessage());
                } catch (Exception ex) {
                    Log.e("Reported(sendReport)", "Error ret: " + error + "; Error ex: " + ex.getMessage());
                }
            }
        });
    }

//endregion

    public void toBack(){
        List<Report> reports = reportController.show();
        int count = reports.size();
        if(actual_view.equalsIgnoreCase("reported")) {
            if(count > 1) {
                actual_view = "list";
                setupRecycler(reports);
                openPendingState(layout_report_list, layout_reported);
            } else {
                finish();
            }
        } else if(actual_view.equalsIgnoreCase("information")) {
            if(count > 1) {
                actual_view = "list";
                setupRecycler(reports);
                openPendingState(layout_report_list, layout_report_info);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        toBack();
    }

}
