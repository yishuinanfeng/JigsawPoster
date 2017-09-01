package com.example.yishuinanfeng.jigsawapplication.util;

import android.graphics.Path;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hendricks on 2017/8/31.
 * svg文件工具类
 */

public class FileUtil {
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir == null ? null : sdDir.toString();
    }


}
