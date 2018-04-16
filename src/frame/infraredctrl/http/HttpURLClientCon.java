package frame.infraredctrl.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HTTP;
import org.apache.mina.util.Base64;

import android.util.Log;

/**
 * 
 * @author ouArea
 * @category manager for HttpURLConnection
 * 
 */
public class HttpURLClientCon implements Runnable {
	private HttpValues httpValues;
	private HttpCallBack httpCallBack;

	public HttpURLClientCon(HttpValues httpValues, HttpCallBack httpCallBack) {
		super();
		this.httpValues = httpValues;
		this.httpCallBack = httpCallBack;
	}

	public HttpValues getHttpValues() {
		return httpValues;
	}

	public void setHttpValues(HttpValues httpValues) {
		this.httpValues = httpValues;
	}

	public HttpCallBack getHttpCallBack() {
		return httpCallBack;
	}

	public void setHttpCallBack(HttpCallBack httpCallBack) {
		this.httpCallBack = httpCallBack;
	}

	// --------------------------------------------------------------------------
	private final static String PREFIX = "--";
	private final static String LINE_END = "\r\n";
	private String boundary;
	private HttpURLConnection instance = null;

	private HttpURLConnection getHttpConnection(URL url) throws MalformedURLException, IOException {
		if (instance == null) {
			instance = (HttpURLConnection) url.openConnection();
			instance.setReadTimeout(httpValues.socketTimeOut);
			instance.setConnectTimeout(httpValues.connectTimeOut);
			instance.setDoInput(true);
			instance.setDoOutput(true);
			instance.setUseCaches(false);
			instance.setRequestMethod("POST");
			// gzip
			instance.setRequestProperty("Accept-Encoding", "gzip");
			instance.setRequestProperty("Connection", "Keep-Alive");
			instance.setRequestProperty("Charset", HTTP.UTF_8);
			instance.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		}
		return instance;
	}

	private HttpURLConnection getHttpConnection(URL url, String user, String pass) throws MalformedURLException, IOException {
		getHttpConnection(url);
		instance.setRequestProperty("Proxy-Authorization", "Basic" + Base64.encodeBase64("admin:admin".getBytes()));
		return instance;
	}

	private void putParamString(StringBuffer sb, String name, String value) {
		if (null != value && !"".equals(value)) {
			sb.append(PREFIX);
			sb.append(boundary);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"");
			sb.append(name);
			sb.append("\"; ");
			sb.append(LINE_END);
			sb.append(LINE_END);
			sb.append(value);
			sb.append(LINE_END);
		}
	}

	private void putParamFile(StringBuffer sb, String name, File value) throws IOException {
		if (value != null) {
			sb.append(PREFIX);
			sb.append(boundary);
			sb.append(LINE_END);
			sb.append("Content-Disposition: form-data; name=\"");
			sb.append(name);
			sb.append("\"; ");
			sb.append("filename=\"");
			sb.append(value.getName());
			sb.append("\"");
			sb.append(LINE_END);
			sb.append("Content-Type: application/octet-stream; charset=");
			sb.append(HTTP.UTF_8);
			sb.append(LINE_END);
			sb.append(LINE_END);
			InputStream is = new FileInputStream(value);
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = is.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, len));
			}
			is.close();
			sb.append(LINE_END);
		}
	}

	public boolean destroy() {
		try {
			if (null != instance) {
				instance.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		instance = null;
		return true;
	}

	public void doPost() {
		try {
			this.boundary = UUID.randomUUID().toString();
			StringBuffer sb = new StringBuffer();
			if (httpValues.entityMap != null && !httpValues.entityMap.isEmpty()) {
				for (Map.Entry<String, Object> entry : httpValues.entityMap.entrySet()) {
					if (entry.getValue() instanceof String) {
						this.putParamString(sb, entry.getKey(), (String) entry.getValue());
					} else if (entry.getValue() instanceof File) {
						this.putParamFile(sb, entry.getKey(), (File) entry.getValue());
					}
				}
			}
			sb.append(PREFIX);
			sb.append(boundary);
			sb.append(PREFIX);
			sb.append(LINE_END);
			DataOutputStream dos = null;
			// if (null == user) {
			// dos = new DataOutputStream(this.getHttpConnection(new
			// URL(httpValues.url)).getOutputStream());
			// } else {
			// dos = new DataOutputStream(this.getHttpConnection(new
			// URL(httpValues.url), user, pass).getOutputStream());
			// }
			dos = new DataOutputStream(this.getHttpConnection(new URL(httpValues.url), "admin", "admin").getOutputStream());
			dos.write(sb.toString().getBytes());
			dos.flush();
			dos.close();
			if (instance.getResponseCode() == HttpURLConnection.HTTP_OK || instance.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
				InputStream input = instance.getInputStream();
				// gzip------------
				String encoding = instance.getContentEncoding();
				if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
					input = new GZIPInputStream(input);
				}
				String content = readByteFully(input, HTTP.UTF_8);
				Log.i("HttpClientCon_Read", httpValues.url + "\n" + content);
				if (null != httpCallBack) {
					httpValues.retValue = content;
					httpCallBack.sendCallBack(0, httpValues);
				}
				// int s = content.indexOf("{");
				// if (-1 == s) {
				// this.setRetValue("");
				// } else {
				// this.setRetValue(content.substring(s));
				// }
			} else {
				Log.e("HttpClientCon_Read", "服务器无响应");
				if (null != httpCallBack) {
					httpValues.retValue = "服务器无响应";
					httpCallBack.sendCallBack(-1, httpValues);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "不支持编码异常");
			if (null != httpCallBack) {
				httpValues.retValue = "不支持编码异常";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} catch (ConnectTimeoutException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "连接网络出错，请检查网络配置");
			if (null != httpCallBack) {
				httpValues.retValue = "连接网络出错，请检查网络配置";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "网络连接超时");
			if (null != httpCallBack) {
				httpValues.retValue = "网络连接超时";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "网络协议异常");
			if (null != httpCallBack) {
				httpValues.retValue = "网络协议异常";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "程序冲突或URL错误");
			if (null != httpCallBack) {
				httpValues.retValue = "程序冲突或URL错误";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "Stream or FileSystem异常");
			if (null != httpCallBack) {
				httpValues.retValue = "Stream or FileSystem异常";
				httpCallBack.sendCallBack(-1, httpValues);
			}
		} finally {
			this.destroy();
		}
	}

	// --------------
	protected String readByteFully(InputStream is, String format) throws IOException {
		byte[] bytes = null;
		if (is != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int l = 0;
			byte[] b = new byte[1024];
			while ((l = is.read(b)) != -1) {
				baos.write(b, 0, l);
			}
			bytes = baos.toByteArray();
			baos.close();
			baos = null;
		}
		return new String(bytes, format);
	}

	@Override
	public void run() {
		this.doPost();
	}

}
