package com.systek.guide.util;

import android.content.Context;
import android.text.TextUtils;


import com.systek.guide.base.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Qiang on 2016/7/13.
 */
public class FileUtil {

    private static final String TAG = "FileUtil";


    /**
     * 查看url对应文件是否存在
     * @param url
     * @param museumId
     * @return
     */
    public static boolean checkFileExists(String url,String museumId){
        if(TextUtils.isEmpty(museumId)){return false;}
        String name=changeUrl2Name(url);
        String path= Constants.LOCAL_PATH + museumId + "/" + name;
        File file=new File(path);
        return file.exists();
    }


    /**
     * 查看url对应文件是否存在
     * @param path
     * @return
     */
    public static boolean checkFileExists(String path){
        if(TextUtils.isEmpty(path)){return false;}
        File file = new File(path);
        return file.exists();
    }

    /**
     * 将url转换为文件名字
     * @param url
     * @return 文件名
     */
    public static String changeUrl2Name(String url){
        return url.replaceAll("/","_");
    }



    /**
     *
     * 读取assets文件
     *
     * @param context
     *            context对象
     * @param name
     *            读取文件名
     * @return String 读取信息
     * @since 1.0.0
     */
    public static String readAssetsFile(Context context, String name ) {

        if( context == null || TextUtils.isEmpty( name ) ) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        InputStream in;
        try {
            in = context.getResources().getAssets().open( name );
            BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
            String tempStr;
            while( ( tempStr = br.readLine() ) != null ) {// 一行一行的读取
                sb.append( tempStr ).append( "\n" );
            }
            return sb.toString();
        } catch( IOException e ) {
            LogUtil.e( TAG, e.getMessage(), e );
        }
        return null;
    }


    /**
     * 复制单个文件
     *
     * @param oldPath
     *            String 原文件路径
     * @param newPath
     *            String 复制后路径
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newfile = new File(newPath);
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
            LogUtil.i("","文件复制完成！！！");
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }



}
