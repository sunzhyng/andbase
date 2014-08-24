package com.sunzy.andbase;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Looper;
import android.widget.Toast;

public class AppException extends Exception implements UncaughtExceptionHandler {

	private static final long serialVersionUID = 983330074861607808L;
	
	/** 定义异常类型 */
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;
	public final static byte TYPE_JSON = 0x08;

	private byte type;
	private int code;

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	private AppException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public int getType() {
		return this.type;
	}

	public void makeToast(Context ctx) {
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error, this.getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(ctx, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_JSON:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public static AppException http(int code) {
		return new AppException(TYPE_HTTP_CODE, code, null);
	}

	public static AppException http(Exception e) {
		return new AppException(TYPE_HTTP_ERROR, 0, e);
	}

	public static AppException socket(Exception e) {
		return new AppException(TYPE_SOCKET, 0, e);
	}

	public static AppException io(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof IOException) {
			return new AppException(TYPE_IO, 0, e);
		}
		return run(e);
	}

	public static AppException xml(Exception e) {
		return new AppException(TYPE_XML, 0, e);
	}

	public static AppException json(Exception e) {
		return new AppException(TYPE_JSON, 0, e);
	}

	public static AppException network(Exception e) {
		if (e instanceof UnknownHostException || e instanceof ConnectException) {
			return new AppException(TYPE_NETWORK, 0, e);
		} else if (e instanceof HttpException) {
			return http(e);
		} else if (e instanceof SocketException) {
			return socket(e);
		}
		return http(e);
	}

	public static AppException run(Exception e) {
		return new AppException(TYPE_RUN, 0, e);
	}

	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		final Context context = AppManager.getAppManager().currentActivity();
		if (context == null) {
			return false;
		}
		
		getCrashReport(context, ex); // crash report string
		// 显示异常信息&发送报告
		new Thread() {
			public void run() {
				Looper.prepare();
				// TODO sent crash report
				Looper.loop();
			}

		}.start();
		return true;
	}

	private String getCrashReport(Context context, Throwable ex) {
		PackageInfo pinfo = ((AppEx) context.getApplicationContext()).getPackageInfo();
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}
}
