package com.xy.springboot.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/**
 * @author sc-xy
 * @time 2024/7/3
 */


public class FolderToZipUtil {

    public static void zip(String sourceFileName, Boolean useZip, HttpServletResponse response){
        ZipOutputStream out = null;
        FileOutputStream fos = null;
        String cacheZip = sourceFileName + ".zip";
        try {
            fos = new FileOutputStream(cacheZip);
            File sourceFile = new File(sourceFileName);
            File cacheZipFile = new File(cacheZip);
            response.setCharacterEncoding("utf-8");
            if (useZip) {
                response.setHeader("content-type", "application/zip");
                response.setHeader("Content-disposition", "attachment;filename=" + cacheZipFile.getName());
                out = new ZipOutputStream(response.getOutputStream());
            } else {
                response.setHeader("content-type", "plain/text");
                out = new ZipOutputStream(fos);
            }

            compress(out, sourceFile, sourceFile.getName());
            out.flush();
            out.finish();
            if (!useZip) {
//                String prefix = "http://localhost:5202/r/"+cacheZip.substring("D:/upload/".length());
                String prefix = cacheZip.substring(9); // "D:/upload".length()
                response.getOutputStream().write(prefix.getBytes(StandardCharsets.UTF_8));
            }
            response.flushBuffer();

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IOCloseUtil.close(fos, out);
        }
    }

    /**
     * 文件压缩
     * @param out
     * @param sourceFile
     * @param base
     */
    public static void compress(ZipOutputStream out, File sourceFile, String base){
        FileInputStream fis = null;
        try {
            //如果路径为目录（文件夹）
            if (sourceFile.isDirectory()) {
                //取出文件夹中的文件（或子文件夹）
                File[] flist = sourceFile.listFiles();
                if (flist.length == 0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                    out.putNextEntry(new ZipEntry(base + "/"));
                    out.closeEntry();
                } else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                    for (File file : flist) {
                        if (file.getName().endsWith(".zip")) { continue; }
                        compress(out, file, base + "/" + file.getName());
                    }
                }
            } else {//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
                out.putNextEntry(new ZipEntry(base));
                fis = new FileInputStream(sourceFile);

                int len;
                byte[] bytes = new byte[1024];
                //将源文件写入到zip文件中
                while ((len = fis.read(bytes)) > 0) {
                    out.write(bytes, 0, len);
                }
                out.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IOCloseUtil.close(fis);
        }
    }
    public static void main(String[] args) {
        String sourceFileName = "py/Codes";
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream("test.zip"));
            compress(out, new File(sourceFileName), sourceFileName);
            out.finish();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}