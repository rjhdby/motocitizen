package motocitizen.utils;

import android.Manifest;
import android.os.Environment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import motocitizen.activity.MainScreenActivity;


/**
 * Created by Prodigy. Класс для логирования в файл
 */

public class UtilitiesProdigy {
 
    static final String DIR_SD = "MyLogs";
    static final String FILENAME_SD = "prodigy.txt";

    public static void writeFileSD(String text) {
/*
        Dexter.withActivity(MainScreenActivity.this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new EmptyPermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        super.onPermissionRationaleShouldBeShown(permission, token);
                        token.continuePermissionRequest();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        super.onPermissionDenied(response);

                    }
                }).check();
*/

            // получаем путь к SD
            File sdPath = Environment.getExternalStorageDirectory();
            // добавляем свой каталог к пути
            sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD + "/");
            // создаем каталог
            sdPath.mkdirs();
            // формируем объект File, который содержит путь к файлу
            File sdFile = new File(sdPath, FILENAME_SD);
            try {
                // открываем поток для записи
                BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));
                // Ещё один вариант ?! ->>> BufferedWriter bwdd = new BufferedWriter(new OutputStreamWriter(openFileOutput(sdFile, MODE_PRIVATE)));
                // пишем данные
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(Calendar.getInstance().getTime());
                bw.write("\r\n" + timeStamp + ": " + text);
                // закрываем поток
                bw.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

