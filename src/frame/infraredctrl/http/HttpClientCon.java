package frame.infraredctrl.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * HttpClient连接
 * 
 * @author ouArea
 * 
 */
class HttpClientCon implements Runnable {
	private HttpValues httpValues;
	private HttpCallBack httpCallBack;

	public HttpClientCon(HttpValues httpValues, HttpCallBack httpCallBack) {
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
	private HttpClient instance = null;

	/**
	 * @category HttpClient for Gzip,JsonString
	 * @return HttpClient
	 */
	private HttpClient getHttpClient() {
		if (instance == null) {
			instance = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(instance.getParams(), httpValues.connectTimeOut);
			HttpConnectionParams.setSoTimeout(instance.getParams(), httpValues.socketTimeOut);
			((AbstractHttpClient) instance).addRequestInterceptor(new HttpRequestInterceptor() {
				public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept-Encoding", "gzip");
					}
				}
			});

			((AbstractHttpClient) instance).addResponseInterceptor(new HttpResponseInterceptor() {
				@Override
				public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
					HttpEntity entity = response.getEntity();
					Header ceheader = entity.getContentEncoding();
					if (ceheader != null) {
						HeaderElement[] codecs = ceheader.getElements();
						for (int i = 0; i < codecs.length; i++) {
							if (codecs[i].getName().equalsIgnoreCase("gzip")) {
								response.setEntity(new GzipDecompressingEntity(response.getEntity()));
								return;
							}
						}
					}
				}

			});

		}
		// instance.getParams().setParameter("Proxy-Authorization", "Basic" +
		// Base64.encodeBase64("admin:admin".getBytes()));
		return instance;
	}

	public boolean destroy() {
		try {
			if (null != instance) {
				instance.getConnectionManager().shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		instance = null;
		return true;
	}

	public void doPost() {
		try {
			Log.i("HttpClientCon_Send", httpValues.url);
			if (null != httpValues.entityBuffer) {
				Log.i("HttpClientCon_Send", httpValues.entityBuffer.toString());
			}
			HttpPost post = new HttpPost(new URI(httpValues.url));
			post.setEntity(httpValues.entity);
			HttpResponse res = getHttpClient().execute(post);
			if (HttpURLConnection.HTTP_OK == res.getStatusLine().getStatusCode() || HttpURLConnection.HTTP_PARTIAL == res.getStatusLine().getStatusCode()) {
				HttpEntity httpEntity = res.getEntity();
				InputStream isIn = httpEntity.getContent();
				String content = readByteFully(isIn, HTTP.UTF_8);
				Log.i("HttpClientCon_Read", httpValues.url + "\n" + content);
				if (null != httpCallBack) {
					httpValues.retValue = content;
					httpCallBack.sendCallBack(0, httpValues);
				}
				// int i = content.indexOf("{");
				// if (-1 != i) {
				// httpValues.retValue = content.substring(i, content.length());
				// if (null != httpCallBack) {
				// httpCallBack.sendCallBack(0, httpValues);
				// }
				// } else {
				// Log.e("无法解析String", content);
				// httpValues.retValue = "网络参数返回错误";
				// if (null != httpCallBack) {
				// httpCallBack.sendCallBack(-1, httpValues);
				// }
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
		} catch (URISyntaxException e) {
			e.printStackTrace();
			Log.e("HttpClientCon_Read", "解析URI出错");
			if (null != httpCallBack) {
				httpValues.retValue = "解析URI出错";
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

/**
 * 
 * @author ouArea
 * 
 */
class GzipDecompressingEntity extends HttpEntityWrapper {
	public GzipDecompressingEntity(final HttpEntity entity) {
		super(entity);
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		InputStream wrappedin = wrappedEntity.getContent();
		return new GZIPInputStream(wrappedin);
	}

	@Override
	public long getContentLength() {
		return -1;
	}

}
