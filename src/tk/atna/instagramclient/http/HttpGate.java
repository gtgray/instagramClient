package tk.atna.instagramclient.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;

/**
 * Async http gate to make server requests
 * 
 */
public class HttpGate {
	
//	private final static String HTTP_GET = "GET"; // HttpUrlConntection uses GET, by default
//	private final static String HTTP_CHARSET = "utf-8"; // String constructor uses utf-8, by default
	
	/**
	 * Type of answer is string with json
	 */
	public final static int TYPE_JSON = 0x0000aa01;
	/**
	 * Type of answer is Bitmap
	 */
	public final static int TYPE_PICTURE = 0x0000aa02;
	
	/**
	 * Handler to post answer to
	 */
	private Handler handler;
	
	/**
	 * Array of executing requests
	 */
	private SparseArray<Task> requests = new SparseArray<>();
	
	/**
	 * Constructor
	 * 
	 * @param handler handler to post answer to
	 */
	public HttpGate(Handler handler) {
		this.handler = handler;
	}
	
	/**
	 * Makes async request to server
	 * 
	 * @param type type of answer
	 * @param url remote url to load data from
	 * @param callback listener to deliver answer
	 */
	public void executeTalking(final int type, final String url, final ServerAnswerCallback callback) {
		// url is null 
		// or less than 10 symbols 
		// or already executing
		if(url == null || url.length() < 10 
				|| requests.get(url.hashCode()) != null) {
			callback.onCancel();
			return;
		}
		
		// start new thread to run server talking
		final Task worker = new Task();
		worker.execute(new Runnable() {
			
			@Override
			public void run() {
				final Bundle answer;
				DataInputStream response = null;
				try {
					HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
//					connection.setRequestMethod(HTTP_GET);
					connection.setDoInput(true);
					
					// wrong status code
					if(connection.getResponseCode() != 200) {
						String message = connection.getResponseCode() + ", " + connection.getResponseMessage();
						(new IOException(message)).printStackTrace();
						worker.cancel();
						answer = null;
						
					// status OK
					} else {
						response = new DataInputStream(connection.getInputStream());
						answer = answerToBundle(type, response);
					}
					
					// free resources
					connection.disconnect();
					
					if(response != null)
						response.close();
					
				} catch (IOException e) {
					e.printStackTrace();
					closeStream(response);
					return;
				}
				
				// post answer
				if(handler != null)
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							if(callback != null)
								if(!worker.canceled())
									callback.onServerAnswer(answer);
								else
									callback.onCancel();
						}
					});
				// forget executed request
				requests.remove(url.hashCode());
			}
		});
		// remember request by url
		requests.put(url.hashCode(), worker);
	}
	
	/**
	 * Cancels executing request
	 * 
	 * @param hash request identifier
	 */
	public void cancelTalking(int hash) {
		Task task = requests.get(hash);
		if(task != null)
			task.cancel();
	}
	
	/**
	 * Processes raw answer from server 
	 * by converting it from input stream to needed type according to <b>type</b>
	 * and wrapping into bundle object
	 * 
	 * @param type type of answer
	 * @param answer input stream containing server answer
	 * @return Bundle object with answer or null
	 */
	private static Bundle answerToBundle(int type, InputStream answer) {
		if(answer == null) 
			return null;
		
		Bundle data = new Bundle();
		
		switch (type) {
		
		case TYPE_JSON:
			String json = inputStreamToString(answer);
			data.putString(ServerAnswerCallback.JSON, json);
			break;
			
		case TYPE_PICTURE:
			Bitmap bitmap = inputStreamToBitmap(answer);
			data.putParcelable(ServerAnswerCallback.PICTURE, bitmap);
			break;
		}
		
		return data;
	}
	
	/**
	 * Reads bytes from input stream and converts them to string object
	 * 
	 * @param is input stream to read
	 * @return string or null
	 */
	private static String inputStreamToString(InputStream is) {
		// empty stream
		if(is == null) 
			return null;
		
		int bufSize = 1024 * 8;
		byte[] buffer = new byte[bufSize];
		String result = null;
		
		ByteArrayOutputStream stream = null;
		BufferedInputStream bis = null;
		try {
			stream = new ByteArrayOutputStream();
			bis = new BufferedInputStream(is);
			
			int bytes;
			while((bytes = bis.read(buffer)) != -1) {
				stream.write(buffer, 0, bytes);
			}
			result = new String(stream.toByteArray()/*, HTTP_CHARSET*/);
			
			bis.close();
			bis = null;
			
			stream.close();
			stream = null;
			
		} catch (IOException e) {
			e.printStackTrace();
			closeStream(bis);
			closeStream(stream);
			return null;
		}
		return result;
	}
	
	/**
	 * Decodes input stream into bitmap object
	 * 
	 * @param is input stream to decode
	 * @return bitmap or null
	 */
	private static Bitmap inputStreamToBitmap(InputStream is) {
		// define buffer size
		int bufSize = 1024 * 16;
		Bitmap bitmap = null;
		
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(is, bufSize);
			bitmap = BitmapFactory.decodeStream(bis);
			
			bis.close();
			bis = null;
			
		} catch (IOException e) {
			e.printStackTrace();
			closeStream(bis);
			return null;
		}
		return bitmap;
	}
	
	/**
	 * Closes stream 
	 * 
	 * @param stream stream to close
	 */
	private static void closeStream(Closeable stream) {
		if(stream != null)
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				stream = null;
			}
	}
	
	/**
	 * Callback interface to deliver answer from server
	 */
	public interface ServerAnswerCallback {
		
		public final static String JSON = "json";
		public final static String PICTURE = "picture";
		
		/**
		 * Called when answer from server received
		 * 
		 * @param answer Bundle object that wrappes server answer
		 */
		public abstract void onServerAnswer(Bundle answer);
		
		/**
		 * Called when no delivery is needed
		 */
		public abstract void onCancel();
	}
	
	/**
	 * Implements Thread class but with ability to fire cancel flag
	 */
	private class Task extends Thread {
		
		// cancel flag
		private boolean canceled = false;
		// action to run
		private Runnable task = null;
		
		/**
		 * Constructor
		 */
		public Task() {
			super();
		}
		
//		public Task(Runnable runnable) {
//			super(runnable);
//		}
		
		/**
		 * Starts executing of task
		 * 
		 * @param task action to be executed
		 */
		public void execute(Runnable task) {
			this.task = task;
			this.start();
		}
		
		/**
		 * Fires cancel flag
		 */
		public void cancel() {
			this.canceled = true;
		}
		
		/**
		 * Returns true if task cancelled, false otherwise
		 */
		public boolean canceled() {
			return canceled;
		}
		
		@Override
		public void run() {
			// runs only one runnable
	        if (task != null) {
	            task.run();
	            return;
	        }
//	        super.run();
	    }
		
	}
	
}
