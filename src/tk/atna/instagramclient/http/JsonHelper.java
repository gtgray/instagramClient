package tk.atna.instagramclient.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import tk.atna.instagramclient.Media;
import tk.atna.instagramclient.Pagination;
import tk.atna.instagramclient.User;
import android.util.SparseArray;

/**
 * Helper that contains static methods for parsing json data from instagram server.<br>
 * Such raw json data consists of three parts:<br> - meta (service part about requests),
 * <br> - data (base part with content),<br> - pagination (part about paginating of content).
 */
public class JsonHelper {
	
	/**
	 * Searches instagram user by username 
	 * in a raw json data received from instagram server
	 * 
	 * @param raw string containing raw json data
	 * @param nick username to search for
	 * @return found User object or null
	 */
	public static User searchUser(String raw, String nick) {
		try {
			return searchUserInDataJson(getDataFromRawJson(raw), nick);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses raw json data received from instagram server 
	 * in search of pagination data
	 * 
	 * @param raw string containing raw json data
	 * @return Pagination object or null
	 */
	public static Pagination getPagination(String raw) {
		try {
			if(raw == null) 
				throw new JSONException("raw to parse from is null");
			
			JSONObject jRaw = new JSONObject(raw);
			JSONObject jPagination = jRaw.optJSONObject(EncodingMap.PAGINATION);
			Pagination pagination = null;
			
			if(jPagination != null)
				pagination = new Pagination(jPagination.optString(EncodingMap.NEXT_URL));/*, 
											jPagination.optString(EncodingMap.NEXT_MAX_ID));*/
			return pagination;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses raw json data received from instagram server 
	 * in search of content data with media items
	 * 
	 * @param raw string containing raw json data
	 * @return SparseArray of media items or null
	 */
	public static SparseArray<Media> parseUserMedia(String raw) {
		try {
			JSONArray jaData = getDataFromRawJson(raw);
			SparseArray<Media> medias = new SparseArray<>((jaData != null) ? jaData.length() : 0);
			
			int length = jaData.length();
			for(int i = 0; i < length; i++) {
				Media media = jsonToMedia(jaData.optJSONObject(i));
				medias.put(media.getCreated(), media);
			}
			return medias;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses stringed json array 
	 * in search of data with users
	 * 
	 * @param strArray string containing json array 
	 * @return SparseArray of user objects or null
	 */
	public static SparseArray<User> jsonToUserArray(String strArray) {
		try {
			if(strArray == null) 
				throw new JSONException("strArray to parse from is null");
			
			JSONArray jaUsers = new JSONArray(strArray);
			SparseArray<User> users = new SparseArray<>((jaUsers != null) ? jaUsers.length() : 0);
			
			int length = jaUsers.length();
			for(int i = 0; i < length; i++) {
				User user = jsonToUser(jaUsers.optJSONObject(i));
				users.put(user.getTimestamp(), user);
			}
			return users;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parses raw json data received from instagram server 
	 * in search of content data
	 * 
	 * @param raw string containing raw json data
	 * @return JSONArray with content data or null
	 */
	private static JSONArray getDataFromRawJson(String raw) throws JSONException {
		if(raw == null) 
			throw new JSONException("raw to parse from is null");
		
		JSONObject jRaw = new JSONObject(raw);
		JSONArray jaData = jRaw.optJSONArray(EncodingMap.DATA);
		
		return jaData;
	}
	
	/**
	 * Searches instagram user by username 
	 * in json array with content data
	 * 
	 * @param data json array with content data
	 * @param nick username to search for
	 * @return found User object or null
	 * @throws JSONException
	 */
	private static User searchUserInDataJson(JSONArray data, String nick) throws JSONException {
		if(data == null) 
			throw new JSONException("data to parse from is null");
		if(nick == null) 
			throw new JSONException("nick to search is null");
		
		int length = data.length();
		for(int i = 0; i < length; i++) {
			// username at next item is equal to searching nick
			if(nick.equals((data.optJSONObject(i)).optString(EncodingMap.USERNAME)))
				return jsonToUser(data.optJSONObject(i));
		}
		// nobody found
		return null;
	}
	
	/**
	 * Converts json object to user object
	 * 
	 * @param json object with content data
	 * @return User object or null
	 * @throws JSONException
	 */
	private static User jsonToUser(JSONObject json) throws JSONException {
		if(json == null) 
			throw new JSONException("json to parse from is null");
		
		User user = new User(json.optInt(EncodingMap.ID))
					.setNick(json.optString(EncodingMap.USERNAME))
					.setPictureUrl(json.optString(EncodingMap.PROFILE_PICTURE));
		// internal parameter is not null
		// if json was read from disk cache
		int ts = json.optInt(EncodingMap.TIMESTAMP);
		user.setTimestamp((int) (ts > 0 ? ts : (System.currentTimeMillis() / 1000)));
		
		return user;
	}
	
	/**
	 * Converts json object to media object
	 * 
	 * @param json object with content data
	 * @return Media object or null
	 * @throws JSONException
	 */
	private static Media jsonToMedia(JSONObject json) throws JSONException {
		if(json == null) 
			throw new JSONException("json to parse from is null");
		
		Media media = new Media(json.optString(EncodingMap.ID))
					  .setPictureUrl(jsonToUrl(json))
					  .setTitle(jsonToTitle(json))
					  .setType(json.optString(EncodingMap.TYPE))
					  .setCreated(json.optInt(EncodingMap.CREATED_TIME))
					  .setLikes(jsonToLikes(json))
					  .setComments(jsonToComments(json));
		return media;
	}
	
	/**
	 * Searches remote url to media item
	 * in json object with content data
	 * 
	 * @param json object with content data
	 * @return remote url to media item graphical representation 
	 * 			in needed resolution or null
	 * @throws JSONException
	 */
	private static String jsonToUrl(JSONObject json) throws JSONException {
		String url = null;
		
		JSONObject jImages = json.optJSONObject(EncodingMap.IMAGES);
		if(jImages != null) {
//			JSONObject jThumbnail = jImages.optJSONObject(EncodingMap.THUMBNAIL);
			JSONObject jThumbnail = jImages.optJSONObject(EncodingMap.LOW_RESOLUTION);
			if(jThumbnail != null)
				url = jThumbnail.optString(EncodingMap.URL);
		}
		return url;
	}
	
	/**
	 * Searches media item title
	 * in json object with content data
	 * 
	 * @param json object with content data
	 * @return title of media item or null
	 * @throws JSONException
	 */
	private static String jsonToTitle(JSONObject json) throws JSONException {
		String title = null;
		
		JSONObject jCaption = json.optJSONObject(EncodingMap.CAPTION);
		if(jCaption != null)
			title = jCaption.optString(EncodingMap.TEXT);
			
		return title;
	}
	
	/**
	 * Searches media item likes count
	 * in json object with content data
	 * 
	 * @param json object with content data
	 * @return likes count of media item or null
	 * @throws JSONException
	 */
	private static int jsonToLikes(JSONObject json) throws JSONException {
		int likes = 0;
		
		JSONObject jLikes = json.optJSONObject(EncodingMap.LIKES);
		if(jLikes != null)
			likes = jLikes.optInt(EncodingMap.COUNT);
			
		return likes;
	}

	/**
	 * Searches media item commemts count
	 * in json object with content data
	 * 
	 * @param json object with content data
	 * @return commemts count of media item or null
	 * @throws JSONException
	 */
	private static int jsonToComments(JSONObject json) throws JSONException {
		int commemts = 0;
		
		JSONObject jComments = json.optJSONObject(EncodingMap.COMMENTS);
		if(jComments != null)
			commemts = jComments.optInt(EncodingMap.COUNT);
			
		return commemts;
	}
	
	/**
	 * Converts sparse array of user objects to stringed json
	 * 
	 * @param users sparse array to convert
	 * @return string with json or null
	 */
	public static String userArrayToJson(SparseArray<User> users) {
		try {
			if(users == null) 
				throw new JSONException("users array to parse from is null");
			
			JSONArray jaUsers = new JSONArray();
			
			int size = users.size();
			for(int i = 0; i < size; i++) {
				jaUsers.put(userToJson(users.valueAt(i)));
			}
			return jaUsers.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Converts user object to json object
	 * 
	 * @param user object to convert
	 * @return json object or null
	 * @throws JSONException
	 */
	private static JSONObject userToJson(User user) throws JSONException {
		if(user == null) 
			throw new JSONException("user to parse from is null");
		
		JSONObject jUser = new JSONObject();
		jUser.put(EncodingMap.ID, user.getId());
		jUser.put(EncodingMap.USERNAME, user.getNick());
		jUser.put(EncodingMap.PROFILE_PICTURE, user.getPictureUrl());
		// internal parameter
		jUser.put(EncodingMap.TIMESTAMP, user.getTimestamp());
		
		return jUser;
	}
}
