package com.njuss.collection.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GPSUtil {
    public GPSUtil() {
    }

    public static String decimalToDMS(double var0) {
        double var6 = var0 % 1.0D;
        int var4;
        String var2 = String.valueOf(var4 = (int)var0);
        var6 = (var0 = var6 * 60.0D) % 1.0D;
        if ((var4 = (int)var0) < 0) {
            var4 = -var4;
        }

        String var3 = String.valueOf(var4);
        if ((int)(var0 = var6 * 60.0D) < 0) {
            var0 = -var0;
        }

        String var8 = String.format("%.2f", var0);
        return var2 + "°" + var3 + "'" + var8 + "\"";
    }

    public static double[] GDtrans(double var0, double var2) {
        Boolean var4 = Boolean.TRUE;

        try {
            SimpleDateFormat var5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date var6 = new Date();
            Date var7 = var5.parse("2020-01-01 15:20:00");
            var4 = var6.before(var7);
        } catch (ParseException var19) {
            var19.printStackTrace();
        }

        if (!var4) {
            double[] var22;
            (var22 = new double[2])[0] = var0;
            var22[1] = var2;
            return var22;
        } else {
            double var21 = transformlat(var0 - 105.0D, var2 - 35.0D);
            double var23 = transformlng(var0 - 105.0D, var2 - 35.0D);
            double var9;
            double var11 = Math.sin(var9 = var2 / 180.0D * 3.141592653589793D);
            double var13 = Math.sqrt(var11 = 1.0D - var11 * 0.006693421622965943D * var11);
            var21 = var21 * 180.0D / (6335552.717000426D / (var11 * var13) * 3.141592653589793D);
            var23 = var23 * 180.0D / (6378245.0D / var13 * Math.cos(var9) * 3.141592653589793D);
            double var15 = var2 + var21;
            double var17 = var0 + var23;
            double[] var20;
            (var20 = new double[2])[0] = var0 * 2.0D - var17;
            var20[1] = var2 * 2.0D - var15;
            return var20;
        }
    }

    public static double transformlat(double var0, double var2) {
        return -100.0D + var0 * 2.0D + var2 * 3.0D + var2 * 0.2D * var2 + var0 * 0.1D * var2 + 0.2D * Math.sqrt(Math.abs(var0)) + (20.0D * Math.sin(var0 * 6.0D * 3.141592653589793D) + 20.0D * Math.sin(var0 * 2.0D * 3.141592653589793D)) * 2.0D / 3.0D + (20.0D * Math.sin(var2 * 3.141592653589793D) + 40.0D * Math.sin(var2 / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D + (160.0D * Math.sin(var2 / 12.0D * 3.141592653589793D) + 320.0D * Math.sin(var2 * 3.141592653589793D / 30.0D)) * 2.0D / 3.0D;
    }

    public static double transformlng(double var0, double var2) {
        return var0 + 300.0D + var2 * 2.0D + var0 * 0.1D * var0 + var0 * 0.1D * var2 + 0.1D * Math.sqrt(Math.abs(var0)) + (20.0D * Math.sin(var0 * 6.0D * 3.141592653589793D) + 20.0D * Math.sin(var0 * 2.0D * 3.141592653589793D)) * 2.0D / 3.0D + (20.0D * Math.sin(var0 * 3.141592653589793D) + 40.0D * Math.sin(var0 / 3.0D * 3.141592653589793D)) * 2.0D / 3.0D + (150.0D * Math.sin(var0 / 12.0D * 3.141592653589793D) + 300.0D * Math.sin(var0 / 30.0D * 3.141592653589793D)) * 2.0D / 3.0D;
    }
}

