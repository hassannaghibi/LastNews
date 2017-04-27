package ir.ncompany.lastnews.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.view.LayoutInflater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Global extends Application {
    public static Context context;
    public static LayoutInflater inflater;
    public static final String LOG_TAG = "VTJ";
    public static final String REST_API_ADDRESS = "https://newsapi.org/";
    public static final String IMG_ADDRESS = "";
    public static RealmConfiguration configRealm;
    public static String android_id = "";
    public static Retrofit retrofit;
    public static Gson gson;
    static File CacheFile;
    public static SharedPreferences Preferences;
    public static SharedPreferences.Editor editor;
    public static Boolean here,here2;

    @Override
    public void onCreate() {
        super.onCreate();

//        ir.ncompany.constrantlayout.helper.FontsOverride.setDefaultFont(this, "MONOSPACE", "font/BYEKAN.ttf");

        context = getApplicationContext();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Realm.init(context);

        gson = new GsonBuilder()
                .setLenient()
                .create();

        configRealm = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name("BBC.realm")
                .build();
        android_id = Settings.Secure.getString(Global.context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        CacheFile = getCacheDir();
        getRetrofit();
        //set here flag
        here = false;
        here2 = false;
        SharedPreferences();

    }

    public static Retrofit getRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Global.REST_API_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    private void SharedPreferences() {
        Preferences = getApplicationContext().getSharedPreferences("Preferences", 0); // 0 - for private mode
        editor = Preferences.edit();

    }
}
