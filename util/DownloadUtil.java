package com.zhzw.util;



import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
/**
 * 下载工具类
 */
public class DownloadUtil
{
    /**
     * 下载
     * @param fileName  文件名
     * @param request   请求对象
     * @param response  响应对象
     * @throws Exception
     */
    public void download(final String fileName, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("UTF-8");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        final String ctxPath = String.valueOf(request.getSession().getServletContext().getRealPath("/")) + "\\" + "upload\\";
        final String downLoadPath = String.valueOf(ctxPath) + fileName;
        try {
            final long fileLength = new File(downLoadPath).length();
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream((OutputStream)response.getOutputStream());
            final byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        if (bis != null) {
            bis.close();
        }
        if (bos != null) {
            bos.close();
        }
    }
}

