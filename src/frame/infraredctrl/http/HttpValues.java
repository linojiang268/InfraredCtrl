package frame.infraredctrl.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.apache.james.mime4j.util.CharsetUtil;

/**
 * Http参数
 * 
 * @author ouArea
 * 
 */
public class HttpValues {
	public int socketTimeOut = 60 * 1000;;
	public int connectTimeOut = 60 * 1000;;
	public String url;

	public MultipartEntity entity;
	// 暂用于测试
	public HashMap<String, Object> entityMap;
	public String retValue;

	public StringBuffer entityBuffer;

	public HttpValues(String url) {
		super();
		this.url = url;
	}

	/**
	 * 添加参数
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String key, Object value) {
		if (null == entity) {
			entity = new MultipartEntity();
			entityBuffer = new StringBuffer();
		}
		if (value instanceof File) {
			entity.addPart(key, new FileBody((File) value));
			entityBuffer.append(key).append(":").append("lenth:").append(null != value).append("  ");
		} else {
			try {
				entity.addPart(key, new StringBody(String.valueOf(value), CharsetUtil.getCharset(HTTP.UTF_8)));
				entityBuffer.append(key).append(":").append(value).append("  ");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加参数(暂用于测试，无法接受数组参数)
	 * 
	 * @param key
	 * @param value
	 */
	public void addMap(String key, Object value) {
		if (null == entity) {
			entityMap = new HashMap<String, Object>();
			entityBuffer = new StringBuffer();
		}
		if (value instanceof File) {
			entityMap.put(key, value);
			entityBuffer.append(key).append(":").append("lenth:").append(null != value).append("  ");
		} else {
			entityMap.put(key, String.valueOf(value));
			entityBuffer.append(key).append(":").append(value).append("  ");
		}
	}

	/**
	 * 清空参数
	 */
	public void clear() {
		entity = null;
		entityBuffer = null;
	}

}
