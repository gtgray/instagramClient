package tk.atna.instagramclient.http;

/**
 * Set of constants that provides mapping to convert data received 
 * from instagram server to internal app representation 
 *
 */
public interface EncodingMap {
	
	public final static String META = "meta";
	public final static String CODE = "code";
	public final static String ERROR_TYPE = "error_type";
	public final static String ERROR_MESSAGE = "error_message";
	
	public final static String DATA = "data";
	public final static String ID = "id";
	public final static String USERNAME = "username";
	public final static String PROFILE_PICTURE = "profile_picture";
	public final static String TIMESTAMP = "timestamp";
	
	public final static String TYPE = "type";
	public final static String IMAGE = "image";
	public final static String IMAGES = "images";
//	public final static String THUMBNAIL = "thumbnail"; // 150x150
	public final static String LOW_RESOLUTION = "low_resolution"; // 306x306
	public final static String CREATED_TIME = "created_time";
	public final static String COMMENTS = "comments";
	public final static String LIKES = "likes";
	public final static String COUNT = "count";
	public final static String CAPTION = "caption";
	public final static String TEXT = "text";
	public final static String URL = "url";
	
	public final static String PAGINATION = "pagination";
	public final static String NEXT_URL = "next_url";
//	public final static String NEXT_MAX_ID = "next_max_id";
	
	
}
