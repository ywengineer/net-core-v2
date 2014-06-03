package com.handee.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * 
 * 上传文件到FTP服务器工具类.
 * 
 * <ul>
 * <li>在创建FTPUploader时会自动连接到FTP服务器，如果连接不成功会抛异常。</li>
 * <li>在上传过程中有可能会抛出异常。</li>
 * <li>在完成上传任务之后，会自动关闭连接。</li>
 * </ul>
 * 
 * <h3>用法：</h3>
 * <p>
 * FTPUploader uploader = new FTPUploader(String server, int port, String uname,
 * String pwd);
 * </p>
 * 
 * <p>
 * uploader.upload(String path, String filename, InputStream src);
 * </p>
 * 
 * @author yangwei ywengineer@gmail.com
 * 
 */
public class FTPUploader implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4144224452487195733L;

	private FTPClient ftp;

	private boolean binary = true;

	private boolean isLogin;

	private String encode = "UTF-8";

	private boolean debug = true;

    public static void main(String[] args) {
        try {
            FTPUploader uploader = new FTPUploader(false,"115.28.244.44", 21, "handee", "handy666");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * 创建FTP上传对象
	 * 
	 * @param useSsl
	 *            是否使用ssl安全链接
	 * @param server
	 *            FTP服务器
	 * @param port
	 *            端口
	 * @param uname
	 *            用户名
	 * @param pwd
	 *            密码
	 * @throws Exception
	 */
	public FTPUploader(boolean useSsl, String server, int port, String uname, String pwd) throws Exception {
		if (useSsl) {
			ftp = new FTPSClient(true);
		} else {
			ftp = new FTPClient();
		}
		if (debug) {
			ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		}

		try {
			int reply;
			ftp.connect(server, port);
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				close();
				throw new Exception("FTP server refused connection.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
			throw new Exception("Could not connect to server.");
		}

		try {
			isLogin = ftp.login(uname, pwd);

			if (!isLogin) {
				close();
			} else {
				if (binary) {
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
				}

				// ftp.setFileType(FTP.FILE_STRUCTURE);
				ftp.setControlEncoding(encode);
				ftp.enterLocalPassiveMode();
			}
		} catch (IOException e) {
			e.printStackTrace();
			close();
			throw new Exception("Could not connect to server.");
		}
	}

	/**
	 * 上传文件到指定目录。
	 * 
	 * @param path
	 *            目录
	 * @param filename
	 *            文件名
	 * @param src
	 *            文件流
	 * @throws Exception
	 *             上传异常
	 * @return 上传是否成功
	 */
	public boolean upload(String path, String filename, InputStream src) throws Exception {
		if (src == null) {
			throw new Exception("upload file not found");
		}
		if (!logined()) {
			throw new Exception("ftp is not login");
		}

		if (!StringUtils.isEmpty(path)) {
			ftp.changeWorkingDirectory(path);
		}

		FTPFile[] files = ftp.listFiles();

		boolean isExsit = false;

		for (FTPFile f : files) {
			if (f.getName().equals(filename)) {
				isExsit = true;
				break;
			}
		}

		try {
			if (isExsit) {
				ftp.deleteFile(filename);
			}

			return ftp.storeFile(filename, src);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("upload file error", e);
		} finally {
			close();
		}
	}

	public boolean logined() {
		return isLogin;
	}

	private void close() {
		try {
			if (ftp != null) {
				if (isLogin) {
					ftp.logout();
				}
				if (ftp.isConnected()) {
					ftp.disconnect();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
