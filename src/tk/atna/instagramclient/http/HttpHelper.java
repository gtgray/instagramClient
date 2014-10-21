package tk.atna.instagramclient.http;

import tk.atna.instagramclient.R;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Helper that transmits server requests between ui-layer and async http gate
 *
 */
public class HttpHelper {
	
	// client id to be able to talk to instagram api 
	private final static int KEY = R.string.client_id;
	private String key;
	
	/**
	 * number of medias per request
	 */
	private int mediasPerLoad;
	
	/**
	 * http requests executor
	 */
	private HttpGate httpGate;
	
	/**
	 * Constructor
	 * 
	 * @param context current context
	 * @param handler handler to post answers to
	 */
	public HttpHelper(Context context, Handler handler) {
		this.key = context.getResources().getString(KEY);
		this.httpGate = new HttpGate(handler);
	}
	
	/**
	 * Sets the size of media set to load per request
	 * 
	 * @param count number of medias per request
	 */
	public void setMediasPerLoad(int count) {
		this.mediasPerLoad = count;
	}
	
	/**
	 * Invokes request to load users associated with nick
	 * 
	 * @param nick username of user to search
	 * @param callback listener to get answer
	 * @return request identifier
	 */
	public int searchUser(String nick, ServerAnswerCallback callback) {
		String url = RequestUrlBuilder.getSearchUrl(key, nick);
		Log.d("myLogs", "HttpHelper.searchUser: url " + url);
		
		httpGate.executeTalking(HttpGate.TYPE_JSON, url, callback);
		return url.hashCode();
	}
	
	/**
	 * Invokes request to load user media
	 * 
	 * @param userId user identifier whose media to load
	 * @param nextPage remote url to next user's media set
	 * @param callback listener to get answer
	 * @return request identifier
	 */
	public int getUserMedia(int userId, String nextPage, ServerAnswerCallback callback) {
		String url = (nextPage != null) ? nextPage 
						: RequestUrlBuilder.getMediaUrl(key, userId, mediasPerLoad);
		Log.d("myLogs", "HttpHelper.getUserMedia: url " + url);
		
		httpGate.executeTalking(HttpGate.TYPE_JSON, url, callback);
		return url.hashCode();
	}
	
	/**
	 * Invokes request to load picture
	 * 
	 * @param url remote url of picture to load
	 * @param callback listener to get answer
	 * @return request identifier
	 */
	public int loadPicture(String url, ServerAnswerCallback callback) {
		Log.d("myLogs", "HttpHelper.loadPicture: url " + url);
		
		httpGate.executeTalking(HttpGate.TYPE_PICTURE, url, callback);
		return url.hashCode();
	}
	
	/**
	 * Cancels executing request
	 * 
	 * @param hash request identifier
	 */
	public void cancelRequest(int hash) {
		httpGate.cancelTalking(hash);
	}
	
	/**
	 * Callback interface to deliver answer from server. 
	 * Inherited from HttpGate.ServerAnswerCallback
	 */
	public interface ServerAnswerCallback extends HttpGate.ServerAnswerCallback {
		// just inherits
	}
	
}
