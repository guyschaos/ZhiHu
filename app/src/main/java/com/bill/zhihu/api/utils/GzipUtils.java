package com.bill.zhihu.api.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * Created by Bill-pc on 2015/6/27.
 */
public class GzipUtils {

    public static String decodeString(byte[] byteArray) {
        GZIPInputStream gis = null;
        ByteArrayOutputStream bao = null;
        StringBuffer sb = new StringBuffer();
        try {
            gis = new GZIPInputStream(new ByteArrayInputStream(byteArray));
            InputStreamReader isr = new InputStreamReader(gis);
            BufferedReader br = new BufferedReader(isr);
            String b = "";
            while ((b = br.readLine()) != null) {
                sb.append(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gis != null && bao != null) {
                try {
                    gis.close();
                    bao.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return sb.toString();
    }
}
