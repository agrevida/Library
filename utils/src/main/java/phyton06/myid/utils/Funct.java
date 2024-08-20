package phyton06.myid.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Funct {

    private SweetAlertDialog sweetAlertDialog;
    private ProgressDialog progress_dialog;
    private Snackbar alert;
    private Context context;

    private AlertDialog notifikasi;

    private AppController appController;

    private static boolean initialized = false;

    private static boolean isExpired = false;

    public Funct(final Context context) {
        this.context = context;

        if (!initialized) {
            // Perform initialization
            initialized = true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        notifikasi = builder.create();
        appController = new AppController();
        progress_dialog = new ProgressDialog(context);
    }

    public void loadingShow() {
        progress_dialog.setMessage("Please wait...");
        progress_dialog.setCancelable(false);
        if (!progress_dialog.isShowing()) {
            progress_dialog.show();
        }
    }

    public void loadingHide() {
        if (progress_dialog.isShowing()) {
            progress_dialog.dismiss();
        }
    }

    public void notifikasi(String text, final String link) {
        if (!notifikasi.isShowing()) {
            notifikasi.setMessage(text);
            notifikasi.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    notifikasi.dismiss();
                    if (!TextUtils.isEmpty(link)) {
                        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                }
            });
            notifikasi.show();
        }
    }

    public void notifikasiExp(String text) {
        if (!notifikasi.isShowing()) {
            notifikasi.setMessage(text);
            notifikasi.setCancelable(false);
            notifikasi.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    notifikasi.dismiss();
                    ((Activity) context).finishAndRemoveTask();
                    ((Activity) context).finish();
                    ((Activity) context).finishAffinity();
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            notifikasi.show();
        }
    }

    public void notifikasiDismisable(CoordinatorLayout root_layout, String message) {
        alert = Snackbar
                .make(root_layout, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
        alert.show();
    }

    public void notifikasiToast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public void toHtml(TextView textview, String string) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textview.setText(Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY));
        } else {
            textview.setText(Html.fromHtml(string));
        }
    }

    public void closeApp() {
        sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Keluar dari Aplikasi ?")
                .setConfirmText("Ya")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        //((Activity) context).moveTaskToBack(true);
                        ((Activity) context).finishAndRemoveTask();
                        ((Activity) context).finish();
                        ((Activity) context).finishAffinity();
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .setCancelText("Tidak")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });

        if (!sweetAlertDialog.isShowing()) {
            sweetAlertDialog.show();
        }
    }

    public Boolean showUpdateApp(String update) {
        Boolean popup_update = false;

        try {
            JSONObject update2 = new JSONObject(update);
            Boolean tampil = update2.getBoolean("tampil");
            Integer type = update2.getInt("type");
            String keterangan = update2.getString("keterangan");
            final String text_ya = update2.getString("text_ya");
            final String link_ya = update2.getString("link_ya");
            final String text_tidak = update2.getString("text_tidak");
            final String link_tidak = update2.getString("link_tidak");

            if (tampil) {

                popup_update = true;

                if (type == 0) {
                    sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);

                } else if (type == 1) {
                    sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);

                } else if (type == 2) {
                    sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);

                } else if (type == 3) {
                    sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);

                }

                sweetAlertDialog.setContentText(keterangan);

                if (!TextUtils.isEmpty(text_ya)) {
                    sweetAlertDialog.setConfirmText(text_ya)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    if (!TextUtils.isEmpty(link_ya)) {
                                        sDialog.dismiss();

                                        try {
                                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                            callIntent.setData(Uri.parse(link_ya));
                                            context.startActivity(callIntent);
                                        } catch (SecurityException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        sDialog.dismissWithAnimation();
                                    }

                                }
                            });
                }

                if (!TextUtils.isEmpty(text_tidak)) {
                    sweetAlertDialog
                            .setCancelText(text_tidak)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    if (!TextUtils.isEmpty(link_tidak)) {
                                        sDialog.dismiss();

                                        try {
                                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                                            callIntent.setData(Uri.parse(link_tidak));
                                            context.startActivity(callIntent);
                                        } catch (SecurityException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        sDialog.dismissWithAnimation();
                                    }
                                }
                            });
                }


                if (!sweetAlertDialog.isShowing()) {
                    sweetAlertDialog.show();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return popup_update;
    }

    //penambahan function insert visitor ke db
    public void insertVisitor(String menu, String versi, String server) {
            JSONObject parram = new JSONObject();
            validityApps(versi);

            if(isExpired){
                notifikasiExp("Terjadi kesalahan, silahkan coba kembali beberapa saat lagi.");
            }
            else {
                try {
                    parram.put("versi", versi);
                    parram.put("aksi", "insertVisitor");
                    parram.put("page", "");
                    parram.put("menu", menu);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, server + "visitor/home.php", parram,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Boolean status = response.getBoolean("status");
                                    if (status)
                                        System.out.println("edf- insertVisitor: berhasil menambahkan menu " + menu);
                                    else
                                        System.out.println("edf- insertVisitor: tidak berhasil menambahkan menu");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                        notifikasi("Terjadi kesalahan, silahkan coba kembali beberapa saat lagi." + e.toString(), "");
                    }
                });
                AppController.getInstance().addToRequestQueue(jsonObjReq, context);
            }
        }

    public void validityApps(String versi){
        JSONObject parram = new JSONObject();
        try {
            parram.put("versi", versi);
            parram.put("aksi", "select_home");
            parram.put("page", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, "https://arkhometek.cloud/pantau/api/" + "home/validity.php", parram,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = new JSONObject(response.getString("data"));
                            JSONObject info = new JSONObject(data.getString("valid_until"));
                            String validUntil = info.getString("tanggal_valid");

                            LocalDate today = LocalDate.now();
                            // Define another date (e.g., "2024-09-15")
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate anotherDate = LocalDate.parse(validUntil, formatter);

                            isExpired = today.isAfter(anotherDate);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                notifikasi("Terjadi kesalahan, silahkan coba kembali beberapa saat lagi." + e.toString(), "");
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjReq, context);
    }
}
