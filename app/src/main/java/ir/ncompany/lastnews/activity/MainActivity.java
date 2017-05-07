package ir.ncompany.lastnews;

import android.app.Dialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import ir.ncompany.lastnews.activity.Global;
import ir.ncompany.lastnews.adapter.AdapterBBCNews;
import ir.ncompany.lastnews.api.RetroBaseApi;
import ir.ncompany.lastnews.api.RetroGetBBCNews;
import ir.ncompany.lastnews.api.RetroGetBBCNewsData;
import ir.ncompany.lastnews.helper.HelperCache;
import ir.ncompany.lastnews.model.ModelNewsSport;
import ir.ncompany.lastnews.realm.RealmNews;
import ir.ncompany.lastnews.realm.RealmSport;
import ir.ncompany.lastnews.utiles.Utiles;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private HelperCache helperCache = new HelperCache();
    private long date;
    private ArrayList<ModelNewsSport> modelNewsSports = new ArrayList<>();
    private ArrayList<RetroGetBBCNewsData> retroGetBBCNewsDatas = new ArrayList<>();
    private ArrayList<RealmNews> realmNewses = new ArrayList<>();
    private ArrayList<RealmSport> realmSports = new ArrayList<>();
    private Dialog dialog_loading;
    private String status;
    private Realm mRealm;
    private boolean isSport = true;
    @InjectView(R.id.constrant_rc)
    RecyclerView constrant_rc;
    @InjectView(R.id.container_main)
    View rootview;
    @InjectView(R.id.constrant_rel_nodata)
    RelativeLayout constrant_rel_nodata;
    @InjectView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
    }

    private void init() {

        ButterKnife.inject(this);
        showDialogLoading();
        sendOrGetDataFromServer("getNews");

        //set click fab button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSport){
                    isSport = false;
                    modelNewsSports.clear();
                    retroGetBBCNewsDatas.clear();
                    sendOrGetDataFromServer("getSport");
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_news_white));
                }else{
                    isSport = true;
                    modelNewsSports.clear();
                    retroGetBBCNewsDatas.clear();
                    sendOrGetDataFromServer("getNews");
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_sport_white));
                }
            }
        });

        constrant_rc.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });
    }

    private void sendOrGetDataFromServer(String status) {
        if (status.equals("getNews")) {
            if (!Utiles.isNetworkConnected()) {
                modelNewsSports.clear();
                modelNewsSports = helperCache.getNews();
                if (modelNewsSports.size() > 0) {
                    noData(false);
                    setRecyclerSuite(modelNewsSports);
                } else {
                    noData(true);
                    showNoNet();
                }
            } else {
                getNews();
            }
        } else if (status.equals("getSport")) {
            if (!Utiles.isNetworkConnected()) {
                modelNewsSports.clear();
                modelNewsSports = helperCache.getSport();
                if (modelNewsSports.size() > 0) {
                    noData(false);
                    setRecyclerSuite(modelNewsSports);
                } else {
                    noData(true);
                    showNoNet();
                }
            } else {
                getSport();
            }
        }
    }

    private void getNews() {
        dialog_loading.show();
        RetroBaseApi retroBaseApi = Global.retrofit.create(RetroBaseApi.class);
        final Call<RetroGetBBCNews> retroGetBBCNewsCall = retroBaseApi.bbcNews();
        final Callback<RetroGetBBCNews> retroGetBBCNewsCallback = new Callback<RetroGetBBCNews>() {
            @Override
            public void onResponse(Call<RetroGetBBCNews> call, Response<RetroGetBBCNews> response) {
                if (response.isSuccessful()) {
                    RetroGetBBCNews apiResponse = response.body();
                    if (apiResponse != null) {

                        status = apiResponse.getStatus();
                        if (status.equals("ok")) {
                            modelNewsSports.clear();
                            retroGetBBCNewsDatas = apiResponse.getRetroGetBBCNewsDatas();

                            mRealm = Realm.getInstance(Global.configRealm);
                            mRealm.beginTransaction();

                            for (int i = 0; i < retroGetBBCNewsDatas.size(); i++) {
                                RealmNews realmNews = new RealmNews();

                                realmNews.setTitle(retroGetBBCNewsDatas.get(i).getTitle());
                                realmNews.setDescription(retroGetBBCNewsDatas.get(i).getDescription());
                                realmNews.setUrlToImage(retroGetBBCNewsDatas.get(i).getUrlToImage());
                                realmNews.setUrl(retroGetBBCNewsDatas.get(i).getUrl());
                                realmNews.setPublishedAt(retroGetBBCNewsDatas.get(i).getPublishedAt());
                                realmNews.setAuthor(retroGetBBCNewsDatas.get(i).getAuthor());

                                realmNewses.add(realmNews);
                            }
                            mRealm.delete(RealmNews.class);
                            mRealm.copyToRealmOrUpdate(realmNewses);
                            mRealm.commitTransaction();

                            /*set recycler*/
                            modelNewsSports = helperCache.getNews();
                            setRecyclerSuite(modelNewsSports);

                            /*check data*/
                            if (modelNewsSports.size() > 0) {
                                noData(false);
                            } else {
                                noData(true);
                            }
                            dialog_loading.dismiss();

                        } else {
                            dialog_loading.dismiss();
                            Snackbar.make(rootview, "problem in api try again", Snackbar.LENGTH_LONG)
                                    .setAction("problem", null).show();

                        }
                    } else {
                        Log.e(Global.LOG_TAG, "ProblemAtEndPoint");
                    }
                } else {
                    Utiles.Log("failedToGetAPI ||| ");
                }
            }

            @Override
            public void onFailure(Call<RetroGetBBCNews> call, Throwable t) {
                Utiles.Log("onFailure ||| " + t);
                dialog_loading.dismiss();
                Snackbar.make(rootview, "problem from server :( ", Snackbar.LENGTH_LONG)
                        .setAction("problem", null).show();
            }
        };

        retroGetBBCNewsCall.enqueue(retroGetBBCNewsCallback);

    }

    private void getSport() {
        dialog_loading.show();
        RetroBaseApi retroBaseApi = Global.retrofit.create(RetroBaseApi.class);
        final Call<RetroGetBBCNews> retroGetBBCNewsCall = retroBaseApi.bbcSport();
        final Callback<RetroGetBBCNews> retroGetBBCNewsCallback = new Callback<RetroGetBBCNews>() {
            @Override
            public void onResponse(Call<RetroGetBBCNews> call, Response<RetroGetBBCNews> response) {
                if (response.isSuccessful()) {
                    RetroGetBBCNews apiResponse = response.body();
                    if (apiResponse != null) {

                        status = apiResponse.getStatus();
                        if (status.equals("ok")) {
                            modelNewsSports.clear();
                            retroGetBBCNewsDatas = apiResponse.getRetroGetBBCNewsDatas();

                            mRealm = Realm.getInstance(Global.configRealm);
                            mRealm.beginTransaction();

                            for (int i = 0; i < retroGetBBCNewsDatas.size(); i++) {
                                RealmSport realmSport = new RealmSport();

                                realmSport.setTitle(retroGetBBCNewsDatas.get(i).getTitle());
                                realmSport.setDescription(retroGetBBCNewsDatas.get(i).getDescription());
                                realmSport.setUrlToImage(retroGetBBCNewsDatas.get(i).getUrlToImage());
                                realmSport.setUrl(retroGetBBCNewsDatas.get(i).getUrl());
                                realmSport.setPublishedAt(retroGetBBCNewsDatas.get(i).getPublishedAt());
                                realmSport.setAuthor(retroGetBBCNewsDatas.get(i).getAuthor());

                                realmSports.add(realmSport);
                            }
                            mRealm.delete(RealmSport.class);
                            mRealm.copyToRealmOrUpdate(realmSports);
                            mRealm.commitTransaction();

                            /*set recycler*/
                            modelNewsSports = helperCache.getSport();
                            setRecyclerSuite(modelNewsSports);

                            /*check data*/
                            if (modelNewsSports.size() > 0) {
                                noData(false);
                            } else {
                                noData(true);
                            }
                            dialog_loading.dismiss();

                        } else {
                            dialog_loading.dismiss();
                            Snackbar.make(rootview, "problem in api try again", Snackbar.LENGTH_LONG)
                                    .setAction("problem", null).show();

                        }
                    } else {
                        Log.e(Global.LOG_TAG, "ProblemAtEndPoint");
                    }
                } else {
                    Utiles.Log("failedToGetAPI ||| ");
                }
            }

            @Override
            public void onFailure(Call<RetroGetBBCNews> call, Throwable t) {
                Utiles.Log("onFailure ||| " + t);
                dialog_loading.dismiss();
                Snackbar.make(rootview, "problem from server :( ", Snackbar.LENGTH_LONG)
                        .setAction("problem", null).show();
            }
        };

        retroGetBBCNewsCall.enqueue(retroGetBBCNewsCallback);

    }

    private void setRecyclerSuite(ArrayList<ModelNewsSport> mobileNumber) {

        if (mobileNumber.size() > 0) {
            AdapterBBCNews adapterBBCNews = new AdapterBBCNews(mobileNumber);
            constrant_rc.setHasFixedSize(false);
            LinearLayoutManager llm = new LinearLayoutManager(Global.context);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            constrant_rc.setLayoutManager(llm);
            constrant_rc.setAdapter(adapterBBCNews);
        } else {
            Utiles.Log("no suite");
        }

    }

    private void showDialogLoading() {

        dialog_loading = new Dialog(MainActivity.this);
        dialog_loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_loading.setCancelable(false);
        dialog_loading.setContentView(R.layout.dialog_loading);
        dialog_loading.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog_loading.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id

    }

    private void showNoNet() {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_no_net);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id

        Button settingBtn = (Button) dialog.findViewById(R.id.dialog_btn_setting);
        Button tryBtn = (Button) dialog.findViewById(R.id.dialog_btn_refresh);

        tryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(
                        android.provider.Settings.ACTION_WIFI_SETTINGS));
            }

        });

        settingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendOrGetDataFromServer("getNews");
            }
        });
        ImageView exitBtn = (ImageView) dialog.findViewById(R.id.dialog_img_close);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void noData(boolean status) {
        if (status) {
            constrant_rel_nodata.setVisibility(View.VISIBLE);
            constrant_rc.setVisibility(View.GONE);
        } else {
            constrant_rel_nodata.setVisibility(View.GONE);
            constrant_rc.setVisibility(View.VISIBLE);
        }
    }

}
