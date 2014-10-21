package tk.atna.instagramclient.http;

/**
 * Class that contains static methods for building request urls according to instagram api
 */
public class RequestUrlBuilder {
	
	// instagram api url pieces
	
	public final static String SERVER_URL = "https://api.instagram.com/v1";
	public final static String SERVER_CLIENT_ID = "&client_id=";
	
	public final static String SERVER_USERS = "/users";
	
	public final static String SERVER_USER_SEARCH = "/search/?";
	public final static String SERVER_USER_SEARCH_Q = "q=";
	
	public final static String SERVER_USER_MEDIA = "/media/recent/?";
	public final static String SERVER_USER_MEDIA_COUNT = "count=";
	
	/**
	 * Builds remote url to search user 
	 * 
	 * @param key client_id of instagram api
	 * @param nick username of user to search
	 * @return string with built url or null
	 */
	public static String getSearchUrl(String key, String nick) {
		if(key == null || nick == null)
			return null;
		
		StringBuilder sb = new StringBuilder(SERVER_URL);
		sb.append(SERVER_USERS);
		sb.append(SERVER_USER_SEARCH);
		sb.append(SERVER_USER_SEARCH_Q);
		sb.append(nick);
		sb.append(SERVER_CLIENT_ID);
		sb.append(key);
		
		return sb.toString();
	}
	
	/**
	 * Builds remote url to get user media 
	 * 
	 * @param key client_id of instagram api
	 * @param userId user identifier  whose media to get
	 * @param count number of medias to get
	 * @return string with built url or null
	 */
	public static String getMediaUrl(String key, int userId, int count) {
		if(key == null || userId < 3)
			return null;
		
		StringBuilder sb = new StringBuilder(SERVER_URL);
		sb.append(SERVER_USERS);
		sb.append("/");
		sb.append(String.valueOf(userId));
		sb.append(SERVER_USER_MEDIA);
		
		// add count value
		if(count > 0) {
			sb.append(SERVER_USER_MEDIA_COUNT);
			sb.append(count);
		}
		
		sb.append(SERVER_CLIENT_ID);
		sb.append(key);
		
		return sb.toString();
	}
	
}
