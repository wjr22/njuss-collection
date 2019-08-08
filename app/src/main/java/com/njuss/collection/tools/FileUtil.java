package com.njuss.collection.tools;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.autonavi.amap.mapcore.tools.GLFileUtil.getCacheDir;

/**
 * 文件读写工具类
 * 包括excel读写，照片读写，音视频读写
 * @author wangj
 * 参照github完成
 *
 * */
public class FileUtil {
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 导出到csv
     * @param c
     * @param
     */
    public static void ExportToCSV(Cursor c, String file) {

        int rowCount = 0;
        int colCount = 0;
        BufferedWriter bfw;
        FileWriter fw;
        File save= new File(file);
        try {

            rowCount = c.getCount();
            colCount = c.getColumnCount();
            //fos = new FileOutputStream(saveFile);
            fw = new FileWriter(save);
            bfw = new BufferedWriter(fw);
            if (rowCount > 0) {
                c.moveToFirst();
                // 写入表头
                for (int i = 0; i < colCount; i++) {
                    if (i != colCount - 1)
                        bfw.write(c.getColumnName(i) + ',');
                    else
                        bfw.write(c.getColumnName(i));
                }
                // 写好表头后换行
                bfw.newLine();
                // 写入数据
                for (int i = 0; i < rowCount; i++) {
                    c.moveToPosition(i);
                    // Toast.makeText(mContext, "正在导出第"+(i+1)+"条",
                    // Toast.LENGTH_SHORT).show();
                    Log.v("导出数据", "正在导出第" + (i + 1) + "条");
                    for (int j = 0; j < colCount; j++) {
                        if(c.getString(j) != null) {
                            if (j != colCount - 1)
                                bfw.write(c.getString(j) + ',');
                            else
                                bfw.write(c.getString(j));
                        }
                    }
                    // 写好每条记录后换行
                    bfw.newLine();
                }
            }
            // 将缓存数据写入文件
            bfw.flush();
            // 释放缓存
            bfw.close();
            // Toast.makeText(mContext, "导出完毕！", Toast.LENGTH_SHORT).show();
            Log.v("导出数据", "导出完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            c.close();
        }
    }
    public static File getFileFromUri(Uri uri, Context context) {
        if (uri == null) {
            return null;
        }
        switch (uri.getScheme()) {
            case "content":
                return getFileFromContentUri(uri, context);
            case "file":
                return new File(uri.getPath());
            default:
                return null;
        }
    }
    /**
     * Gets the corresponding path to a file from the given content:// URI
     *
     * @param contentUri The content:// URI to find the file path from
     * @param context    Context
     * @return the file path as a string
     */

    private static File getFileFromContentUri(Uri contentUri, Context context) {
        if (contentUri == null) {
            return null;
        }
        File file = null;
        String filePath;
        String fileName;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(contentUri, filePathColumn, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            fileName = cursor.getString(cursor.getColumnIndex(filePathColumn[1]));
            cursor.close();
            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);
            }
            if (!file.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
                filePath = getPathFromInputStreamUri(context, contentUri, fileName);
            }
            if (!TextUtils.isEmpty(filePath)) {
                file = new File(filePath);
            }
        }
        return file;
    }
    /**
     * 用流拷贝文件一份到自己APP目录下
     *
     * @param context
     * @param uri
     * @param fileName
     * @return
     */
    public static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
                //L.e(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {

                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
            throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            //自己定义拷贝文件路径
            targetFile = new File(getCacheDir(context), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }

    /**
     * 读入excel文件，解析后返回
     *
     * @return 列表，
     * @throws IOException
     */
    public static List<String[]> readExcel(String fileName, InputStream in) throws IOException {
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(fileName, in);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<String[]> list = new ArrayList<String[]>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了第一行的所有行
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    //循环当前行
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
            workbook.close();
        }
        return list;
    }

    /**
     * 从输入流获取工作表
     *
     * @param fileName 文件名称
     * @param is       文件输入流
     * @return Workbook
     */
    private static Workbook getWorkBook(String fileName, InputStream is) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(xls)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(xlsx)) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
        }
        return workbook;
    }

    private static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    public String savedFileIntoSD(File file, String targetName, String targetPath) {

        return targetName;
    }


    public static StorageManager getStorageManager(Context cxt) {
        StorageManager sm = (StorageManager)
                cxt.getSystemService(Context.STORAGE_SERVICE);
        return sm;
    }

    /**
     * Returns list of all mountable volumes.
     * list elements are RefStorageVolume, which can be seen as
     * mirror of android.os.storage.StorageVolume
     * return null on error.
     *
     * @param cxt
     * @return
     */
    public static RefStorageVolume[] getVolumeList(Context cxt) {
        if (!isSupportApi()) {
            return null;
        }
        StorageManager sm = getStorageManager(cxt);
        if (sm == null) {
            return null;
        }

        try {
            Class<?>[] argTypes = new Class[0];
            Method method_getVolumeList =
                    StorageManager.class.getMethod("getVolumeList", argTypes);
            Object[] args = new Object[0];
            Object array = method_getVolumeList.invoke(sm, args);
            int arrLength = Array.getLength(array);
            RefStorageVolume[] volumes = new
                    RefStorageVolume[arrLength];
            for (int i = 0; i < arrLength; i++) {
                volumes[i] = new RefStorageVolume(Array.get(array, i));
            }
            return volumes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns list of paths for all mountable volumes.
     * return null on error.
     */
    public static String[] getVolumePaths(Context cxt) {
        if (!isSupportApi()) {
            return null;
        }
        StorageManager sm = getStorageManager(cxt);
        if (sm == null) {
            return null;
        }

        try {
            Class<?>[] argTypes = new Class[0];
            Method method_getVolumeList =
                    StorageManager.class.getMethod("getVolumePaths", argTypes);
            Object[] args = new Object[0];
            Object array = method_getVolumeList.invoke(sm, args);
            int arrLength = Array.getLength(array);
            String[] paths = new
                    String[arrLength];
            for (int i = 0; i < arrLength; i++) {
                paths[i] = (String) Array.get(array, i);
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the state of a volume via its mountpoint.
     * return null on error.
     */
    public static String getVolumeState(Context cxt, String mountPoint) {
        if (!isSupportApi()) {
            return null;
        }
        StorageManager sm = getStorageManager(cxt);
        if (sm == null) {
            return null;
        }

        try {
            Class<?>[] argTypes = new Class[1];
            argTypes[0] = String.class;
            Method method_getVolumeList =
                    StorageManager.class.getMethod("getVolumeState", argTypes);
            Object[] args = new Object[1];
            args[0] = mountPoint;
            Object obj = method_getVolumeList.invoke(sm, args);
            String state = (String) obj;
            return state;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get primary volume of the device.
     *
     * @param cxt
     * @return RefStorageVolume can be seen as mirror of
     * android.os.storage.StorageVolume
     */
    public static RefStorageVolume getPrimaryVolume(Context cxt) {
        RefStorageVolume[] volumes = getVolumeList(cxt);
        if (volumes == null) {
            return null;
        }
        for (RefStorageVolume volume : volumes) {
            try {
                if (volume.isPrimary()) {
                    return volume;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * see if SDK version of current device is greater
     * than 14 (IceCreamSandwich, 4.0).
     */
    private static boolean isSupportApi() {
        int osVersion = android.os.Build.VERSION.SDK_INT;
        boolean avail = osVersion >= 14;
        return avail;
    }

    /**
     * this class can be seen as mirror of android.os.storage.StorageVolume :
     * Description of a storage volume and its capabilities, including the
     * filesystem path where it may be mounted.
     */
    public static class RefStorageVolume {

        private static final int INIT_FLAG_STORAGE_ID = 0x01 << 0;
        private static final int INIT_FLAG_DESCRIPTION_ID = 0x01 << 1;
        private static final int INIT_FLAG_PATH = 0x01 << 2;
        private static final int INIT_FLAG_PRIMARY = 0x01 << 3;
        private static final int INIT_FLAG_REMOVABLE = 0x01 << 4;
        private static final int INIT_FLAG_EMULATED = 0x01 << 5;
        private static final int INIT_FLAG_ALLOW_MASS_STORAGE = 0x01 << 6;
        private static final int INIT_FLAG_MTP_RESERVE_SPACE = 0x01 << 7;
        private static final int INIT_FLAG_MAX_FILE_SIZE = 0x01 << 8;
        private int mInitFlags = 0x00;

        private int mStorageId;
        private int mDescriptionId;
        private File mPath;
        private boolean mPrimary;
        private boolean mRemovable;
        private boolean mEmulated;
        private boolean mAllowMassStorage;
        private int mMtpReserveSpace;
        /**
         * Maximum file size for the storage, or zero for no limit
         */
        private long mMaxFileSize;

        private Class<?> class_StorageVolume =
                Class.forName("android.os.storage.StorageVolume");
        private Object instance;

        private RefStorageVolume(Object obj) throws ClassNotFoundException {
            if (!class_StorageVolume.isInstance(obj)) {
                throw new IllegalArgumentException(
                        "obj not instance of StorageVolume");
            }
            instance = obj;
        }

        public void initAllFields() throws Exception {
            getPathFile();
            getDescriptionId();
            getStorageId();
            isPrimary();
            isRemovable();
            isEmulated();
            allowMassStorage();
            getMaxFileSize();
            getMtpReserveSpace();
        }

        /**
         * Returns the mount path for the volume.
         *
         * @return the mount path
         */
        public String getPath() throws Exception {
            File pathFile = getPathFile();
            if (pathFile != null) {
                return pathFile.toString();
            } else {
                return null;
            }
        }

        public File getPathFile() throws Exception {
            if ((mInitFlags & INIT_FLAG_PATH) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "getPathFile", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mPath = (File) obj;
                mInitFlags &= INIT_FLAG_PATH;
            }
            return mPath;
        }

        /**
         * Returns a user visible description of the volume.
         *
         * @return the volume description
         */
        public String getDescription(Context context) throws Exception {
            int resId = getDescriptionId();
            if (resId != 0) {
                return context.getResources().getString(resId);
            } else {
                return null;
            }
        }

        public int getDescriptionId() throws Exception {
            if ((mInitFlags & INIT_FLAG_DESCRIPTION_ID) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "getDescriptionId", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mDescriptionId = (Integer) obj;
                mInitFlags &= INIT_FLAG_DESCRIPTION_ID;
            }
            return mDescriptionId;
        }

        public boolean isPrimary() throws Exception {
            if ((mInitFlags & INIT_FLAG_PRIMARY) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "isPrimary", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mPrimary = (Boolean) obj;
                mInitFlags &= INIT_FLAG_PRIMARY;
            }
            return mPrimary;
        }

        /**
         * Returns true if the volume is removable.
         *
         * @return is removable
         */
        public boolean isRemovable() throws Exception {
            if ((mInitFlags & INIT_FLAG_REMOVABLE) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "isRemovable", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mRemovable = (Boolean) obj;
                mInitFlags &= INIT_FLAG_REMOVABLE;
            }
            return mRemovable;
        }

        /**
         * Returns true if the volume is emulated.
         *
         * @return is removable
         */
        public boolean isEmulated() throws Exception {
            if ((mInitFlags & INIT_FLAG_EMULATED) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "isEmulated", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mEmulated = (Boolean) obj;
                mInitFlags &= INIT_FLAG_EMULATED;
            }
            return mEmulated;
        }

        /**
         * Returns the MTP storage ID for the volume.
         * this is also used for the storage_id column in the media provider.
         *
         * @return MTP storage ID
         */
        public int getStorageId() throws Exception {
            if ((mInitFlags & INIT_FLAG_STORAGE_ID) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "getStorageId", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mStorageId = (Integer) obj;
                mInitFlags &= INIT_FLAG_STORAGE_ID;
            }
            return mStorageId;
        }

        /**
         * Returns true if this volume can be shared via USB mass storage.
         *
         * @return whether mass storage is allowed
         */
        public boolean allowMassStorage() throws Exception {
            if ((mInitFlags & INIT_FLAG_ALLOW_MASS_STORAGE) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "allowMassStorage", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mAllowMassStorage = (Boolean) obj;
                mInitFlags &= INIT_FLAG_ALLOW_MASS_STORAGE;
            }
            return mAllowMassStorage;
        }

        /**
         * Returns maximum file size for the volume, or zero if it is unbounded.
         *
         * @return maximum file size
         */
        public long getMaxFileSize() throws Exception {
            if ((mInitFlags & INIT_FLAG_MAX_FILE_SIZE) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "getMaxFileSize", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mMaxFileSize = (Long) obj;
                mInitFlags &= INIT_FLAG_MAX_FILE_SIZE;
            }
            return mMaxFileSize;
        }

        /**
         * Number of megabytes of space to leave unallocated by MTP.
         * MTP will subtract this value from the free space it reports back
         * to the host via GetStorageInfo, and will not allow new files to
         * be added via MTP if there is less than this amount left free in the
         * storage.
         * If MTP has dedicated storage this value should be zero, but if MTP is
         * sharing storage with the rest of the system, set this to a positive
         * value
         * to ensure that MTP activity does not result in the storage being
         * too close to full.
         *
         * @return MTP reserve space
         */
        public int getMtpReserveSpace() throws Exception {
            if ((mInitFlags & INIT_FLAG_MTP_RESERVE_SPACE) == 0) {
                Class<?>[] argTypes = new Class[0];
                Method method = class_StorageVolume.getDeclaredMethod(
                        "getMtpReserveSpace", argTypes);
                Object[] args = new Object[0];
                Object obj = method.invoke(instance, args);
                mMtpReserveSpace = (Integer) obj;
                mInitFlags &= INIT_FLAG_MTP_RESERVE_SPACE;
            }
            return mMtpReserveSpace;
        }

        @Override
        public String toString() {
            try {
                final StringBuilder builder = new StringBuilder("RefStorageVolume [");
                builder.append("mStorageId=").append(getStorageId());
                builder.append(" mPath=").append(getPath());
                builder.append(" mDescriptionId=").append(getDescriptionId());
                builder.append(" mPrimary=").append(isPrimary());
                builder.append(" mRemovable=").append(isRemovable());
                builder.append(" mEmulated=").append(isEmulated());
                builder.append(" mMtpReserveSpace=").append(getMtpReserveSpace());
                builder.append(" mAllowMassStorage=").append(allowMassStorage());
                builder.append(" mMaxFileSize=").append(getMaxFileSize());
                builder.append("]");
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}


