/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ZipUtils.java
 *
 * 14-5-15 下午7:22
 *
 *******************************************************************/
package com.handee.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * com.handee.utils.ZipUtils.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-5-15 下午7:22
 */
public class ZipUtils {

    /**
     * 压缩文件或目录，包括子目录压缩
     *
     * @param baseDir 待压缩目录或文件
     * @param zipFile 压缩后的文件名
     */
    public static void zip(String baseDir, String zipFile) {
        File source = new File(baseDir);
        File out = new File(zipFile);
        ZipArchiveOutputStream stream = null;
        try {
            if (!out.exists()) {
                out.createNewFile();
            }
            stream = new ZipArchiveOutputStream(new FileOutputStream(out));
            if (source.exists()) {
                if (source.isFile()) {
                    zip(baseDir, source, stream);
                } else {
                    List<File> fileList = getSubFiles(source);
                    for (int i = 0; i < fileList.size(); i++) {
                        zip(baseDir, fileList.get(i), stream);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * @param baseDir 基本目录
     * @param srcFile 本次压缩的文件
     * @param stream  Zip输出流
     * @throws IOException
     * @description 压缩文件
     */
    private static void zip(String baseDir, File srcFile, ZipArchiveOutputStream stream) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(srcFile));
            ZipArchiveEntry entry = new ZipArchiveEntry(getAbsFileName(baseDir, srcFile));
            entry.setSize(srcFile.length());
            stream.putArchiveEntry(entry);
            IOUtils.copy(is, stream);
            stream.closeArchiveEntry();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 取得指定目录下的所有文件列表，包括子目录下的文件.
     *
     * @param baseDir File 指定的目录
     * @return 包含java.io.File的List
     */
    private static List<File> getSubFiles(File baseDir) {
        List<File> ret = new ArrayList<>();
        File[] tmp = baseDir.listFiles();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].isFile())
                ret.add(tmp[i]);
            if (tmp[i].isDirectory())
                ret.addAll(getSubFiles(tmp[i]));
        }
        return ret;
    }

    /**
     * 给定根目录，返回另一个文件名的相对路径，用于zip文件中的路径.
     *
     * @param baseDir      java.lang.String 根目录
     * @param realFileName java.io.File 实际的文件名
     * @return 相对文件名
     */

    private static String getAbsFileName(String baseDir, File realFileName) {
        File real = realFileName;
        File base = new File(baseDir);
        String ret = real.getName();
        if (real.equals(base))// baseDir 为文件时，直接返回
        {
            return ret;
        } else {
            while (true) {
                real = real.getParentFile();
                if (real == null)
                    break;
                if (real.equals(base))
                    break;
                else
                    ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }

    /**
     * 将Zip文件解压到指定目录
     *
     * @param in  zip文件
     * @param out 目录
     * @throws IOException
     */
    public static void unzip(File in, File out) throws IOException {
        // 如果不存在，是文件夹，不是zip文件
        if (!in.exists() || in.isDirectory() || !in.getName().endsWith("zip")) {
            return;
        }
        // 如果目标目录不存在，则创建
        if (!out.exists() || !out.isDirectory()) {
            out.mkdirs();
        }
        ZipFile zipFile = new ZipFile(in);
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            ZipArchiveEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                File directory = new File(out, entry.getName());
                directory.mkdirs();
            } else {
                OutputStream os = null;
                InputStream inputStream = zipFile.getInputStream(entry);
                try {
                    os = new FileOutputStream(getRealFileName(out, entry.getName()));
                    IOUtils.copy(inputStream, os);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(os);
                }
            }
        }
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(File baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = baseDir;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
            if (!ret.exists())
                ret.mkdirs();
        }
        return new File(ret, dirs[dirs.length - 1]);
    }

    public static void main(String[] args) throws Exception {
        // 测试
//        ZipTool.zip("E:/mdf", "E:/mdf/22.zip");
//        ZipTool.unzip("E:/mdf", "E:/mdf/22.zip");
    }
}
