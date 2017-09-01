package com.example.yishuinanfeng.jigsawapplication.util;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by Hendricks on 2017/6/12.
 * 解析SVG文件的工具类
 */

public class SvgParseUtil {
    public static ArrayList<Path> createPathFromSvgMask(String filePath) {
        ArrayList<Path> pathList = new ArrayList<>();
        try {

            FileInputStream inStream = new FileInputStream(filePath);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inStream, "UTF-8");// 设置数据源编码
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理

                        break;
                    case XmlPullParser.START_TAG:// 文档开始事件,可以进行数据初始化处理
                        Log.d("XmlPullParser", parser.getName());
                        //椭圆
                        if (parser.getName().equals("ellipse")) {
                            float cx = Float.valueOf(parser.getAttributeValue(null, "cx"));
                            float cy = Float.valueOf(parser.getAttributeValue(null, "cy"));
                            float rx = Float.valueOf(parser.getAttributeValue(null, "rx"));
                            float ry = Float.valueOf(parser.getAttributeValue(null, "ry"));

                            Path path = new Path();
                            path.addOval(new RectF(cx - rx, cy - ry, cx + rx, cy + ry), Path.Direction.CCW);
                            pathList.add(path);
                            //圆形
                        } else if (parser.getName().equals("circle")) {
                            float cx = Float.valueOf(parser.getAttributeValue(null, "cx"));
                            float cy = Float.valueOf(parser.getAttributeValue(null, "cy"));
                            float r = Float.valueOf(parser.getAttributeValue(null, "r"));

                            Path path = new Path();
                            path.addCircle(cx, cy, r, Path.Direction.CCW);
                            pathList.add(path);
                            //多边形
                        } else if (parser.getName().equals("polygon")) {
                            String points = parser.getAttributeValue(null, "points");
                            String[] pointSplit = points.split(",| ");
                            Path path = new Path();
                            for (int i = 0; i < pointSplit.length; i++) {
                                float x;
                                float y;
                                if (i % 2 == 0) {
                                    x = Float.valueOf(pointSplit[i]);
                                    y = Float.valueOf(pointSplit[i + 1]);
                                    if (i == 0) {
                                        path.moveTo(x, y);
                                    } else {
                                        path.lineTo(x, y);
                                    }
                                }
                            }
                            pathList.add(path);
                        } else if (parser.getName().equals("path")) {
                            Path path = PathParser.createPathFromPathData(parser.getAttributeValue(null, "d"));
                            pathList.add(path);
                        }
                        break;
                    case XmlPullParser.END_TAG:// 结束元素事件

                        break;
                }
                eventType = parser.next();
            }
            inStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }


    /**
     * 读取镂空部分的路径
     */
    public static ArrayList<ArrayList<Path>> getHollowPaths(List<String> svgPathList) {
        ArrayList<ArrayList<Path>> pathsList = new ArrayList<>();
        if (svgPathList.size() > 0) {
            for (String filePath : svgPathList) {
                ArrayList<Path> pathList = SvgParseUtil.createPathFromSvgMask(FileUtil.getSDPath() + "/" + filePath);
                pathsList.add(pathList);
            }
        }
        return pathsList;
    }
}




