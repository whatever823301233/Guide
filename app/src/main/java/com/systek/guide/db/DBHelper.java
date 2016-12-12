package com.systek.guide.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.systek.guide.base.util.LogUtil;
import com.systek.guide.db.tableInfoImpl.AreaRoomInfo;
import com.systek.guide.db.tableInfoImpl.CityInfo;
import com.systek.guide.db.tableInfoImpl.ExhibitInfo;
import com.systek.guide.db.tableInfoImpl.LabelInfo;
import com.systek.guide.db.tableInfoImpl.MuseumInfo;
import com.systek.guide.db.tableInfoImpl.TableInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Qiang on 2016/7/13.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "qiang.db";
    private static final int DATABASE_VERSION = 1;

    private ArrayList<TableInfo> sTableInfo = new ArrayList<>();


    public DBHelper( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );

        sTableInfo.add(new CityInfo());
        sTableInfo.add(new MuseumInfo());
        sTableInfo.add(new ExhibitInfo());
        sTableInfo.add(new LabelInfo());
        sTableInfo.add(new AreaRoomInfo());
        //sTableInfo.add(new BeaconInfo());
        //sTableInfo.add(new DeviceRecorderInfo());
        //sTableInfo.add(new DeviceUseCountInfo());

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtil.d( TAG, "onCreate" );
        try{
            for( TableInfo info : sTableInfo) {
                for( String sql : info.getCreateSql() ) {
                    db.execSQL( sql );
                }
            }
        }catch (SQLException e){
            LogUtil.e(TAG,e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        LogUtil.d( TAG, "onUpgrade" );
        for( TableInfo info : sTableInfo) {
            info.upgrade( db, oldVersion, newVersion );
        }
    }




    private final static String DATABASE_PATH = Environment.getExternalStorageDirectory() + "/";
    private Context context;
    private int dbVersion;

    public DBHelper(Context context, String dbName, int initVersion) {
        super(context, dbName, (SQLiteDatabase.CursorFactory)null, initVersion);
        this.context = context;
        this.dbVersion = initVersion;
    }

    /*@Override
    public void onCreate(SQLiteDatabase db) {
        for(int i = 1; i <= this.dbVersion; ++i) {
            this.upgradeSqlData(db, i, false);
        }
    }*/

    private void upgradeSqlData(SQLiteDatabase db, int version, boolean isDowngrade) {
        String fileName;
        if(isDowngrade) {
            fileName = "_" + version + "_db.sql";
        } else {
            fileName = version + "_db.sql";
        }

        BufferedReader bufferedReader = null;
        db.beginTransaction();

        try {
            InputStream e = this.context.getAssets().open(fileName);
            InputStreamReader reader = new InputStreamReader(e, "UTF-8");
            bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            StringBuilder sb = new StringBuilder();

            while(true) {
                while(line != null) {
                    line = line.trim();
                    if(!"".equals(line) && line.charAt(0) != 47 && line.charAt(0) != 45) {
                        boolean middleIndex = true;
                        int middleIndex1;
                        if((middleIndex1 = line.lastIndexOf("--")) != -1) {
                            line = line.substring(0, middleIndex1);
                        }

                        sb.append(line);
                        String sql = sb.toString();
                        if(!"".equals(sql) && sql.charAt(sql.length() - 1) == 59) {
                            //logger.debug("load sql:{}", sql);
                            db.execSQL(sql.substring(0, sql.indexOf(59)));
                            sb = new StringBuilder();
                        }

                        line = bufferedReader.readLine();
                    } else {
                        line = bufferedReader.readLine();
                    }
                }

                db.setTransactionSuccessful();
                //logger.info("load file {} success.", fileName);
                Log.e("TAG","load file {} success." + fileName);
                break;
            }
        } catch (Exception var20) {
            //logger.error("load file {} failed.", fileName, var20);
            Log.e("TAG","load file {} success." + fileName);
        } finally {
            try {
                if(bufferedReader != null) {
                    bufferedReader.close();
                }

                db.endTransaction();
            } catch (IOException var19) {
                ;
            }

        }

    }

   /* @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion + 1; i <= newVersion; ++i) {
            this.upgradeSqlData(db, i, false);
        }
    }*/

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion - 1; i >= newVersion; --i) {
            this.upgradeSqlData(db, i, false);
        }

    }

//    @Override
//    public SQLiteDatabase getWritableDatabase() {
//        File file = new File(DATABASE_PATH + "dbtest");
//        if (!file.exists()) {
//            file.mkdirs();
//
//        }
//        File dbFile = new File(file,"mydb.db");
//        if (!dbFile.exists())
//            try {
//                dbFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        return SQLiteDatabase.openOrCreateDatabase(dbFile,null);
//    }
//
//    @Override
//    public SQLiteDatabase getReadableDatabase() {
//        return getWritableDatabase();
//    }






}
