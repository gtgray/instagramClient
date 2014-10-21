package tk.atna.instagramclient.ui;

import tk.atna.instagramclient.R;
import tk.atna.instagramclient.http.HttpHelper;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

public abstract class BaseFragment<T> extends Fragment implements OnItemClickListener {
	
	/**
	 * Fragment action to invoke media data of chosen user
	 */
	public final static int CLICK_ITEM_USER = 0x00000c01;
	/**
	 * Fragment action to invoke detailed information about chosen picture
	 */
	public final static int CLICK_ITEM_PICTURE = 0x00000c02;
	
	// progress bar resources
	private int pbResource = R.id.pbRoll;
	private ProgressBar progress;
	
	/**
	 * http transport
	 */
	protected HttpHelper httpHelper;
	
	private FragmentActionCallback callback;
	
	/**
	 * Invoke action callback
	 * 
	 * @param action needed fragment command
	 * @param data additional data to send
	 */
	public void makeFragmentAction(int action, Bundle data) {
		if(callback != null)
			callback.onFragmentAction(action, data);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// process arguments for initialized fragment
		if(getArguments() != null) 
			processArguments(getArguments());
		
		// initialize actions callback and
		// prove that hoster activity implements it
		try {
			callback = (FragmentActionCallback) getActivity();
			
		} catch (ClassCastException e) {
			e.printStackTrace();
			
			Log.d("myLogs", "BaseFragment.onActivityCreated: activity must implement " + 
							FragmentActionCallback.class.getSimpleName());
		}
		
		// initialize http transport
		httpHelper = new HttpHelper(getActivity(), new Handler());
		
		// try to find progress bar view
		progress = (ProgressBar) getView().findViewById(pbResource);
		if(progress == null)
			Log.d("myLogs", "BaseFragment.onActivityCreated: fragment layout doesn't contain progress bar with id "
							+ pbResource + "(R.id.pbRoll)");
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// override to use
	}
	
	/**
	 * Handles arguments supplied to fragment on initialization. 
	 * Called in onActivityCreated() method.
	 * 
	 * @param args non null bundle object with init arguments
	 */
	protected void processArguments(Bundle args) {
		// override to use
	}
	
	/**
	 * Changes the visibility of progress bar view.
	 * Toggle to true to show progress bar view 
	 * or to false to hide it
	 * 
	 * @param show switch for toggle
	 */
	protected void showProgress(boolean show) {
		int visibility = show ? View.VISIBLE : View.GONE;
		if(progress != null)
			progress.setVisibility(visibility);
	}
	
	/**
	 * Adds items from array to adapter
	 * 
	 * @param array sparse array of items ready to be added to adapter
	 */
	protected abstract void arrayToAdapter(SparseArray<T> array);
	
	/**
	 * Pulls picture from memory cache, on failure loads it from server
	 * 
	 * @param obj object to get picture id and url
	 */
	protected abstract void pullPicture(T obj);
	
	/**
	 * Manages request of loading picture from url
	 * 
	 * @param id identifier of the picture to remember in cache
	 * @param url remote url to load from
	 */
	protected abstract void loadPicture(final int id, String url);
	
	/**
	 * Callback interface to deliver fragment actions to activity
	 *
	 */
	public interface FragmentActionCallback {
		
		/**
		 * Called on fragment action event
		 * 
		 * @param action needed command
		 * @param data additional data to send
		 */
		public void onFragmentAction(int action, Bundle data);
	}
	
}
