package tk.atna.instagramclient.ui;

import tk.atna.instagramclient.Bundlable;
import tk.atna.instagramclient.R;
import tk.atna.instagramclient.User;
import tk.atna.instagramclient.UserArrayAdapter;
import tk.atna.instagramclient.http.HttpHelper.ServerAnswerCallback;
import tk.atna.instagramclient.http.JsonHelper;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class UserListFragment extends BaseFragment<User> {
	
	public final static String TAG = UserListFragment.class.getName();
	
	// layout resources for fragment views
	private int headerLayout = R.layout.list_header_layout;
	private int userListLayout = R.layout.user_list_layout;
	private int itemLayout = R.layout.list_item_layout;
	
	// list view resource
	private int lvResource = R.id.lvData;
	private ListView lvData;
	
	/**
	 * Adapter contains items for list view
	 */
	private UserArrayAdapter adapter;
	
	/**
	 * Identifier of currently executing http request
	 */
	private int searchRequest;
	
	/**
	 * Local memory cache for user profile pictures
	 */
	private final SparseArray<Drawable> drawableCache = new SparseArray<Drawable>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(userListLayout, container, false);
		
		
		adapter = new UserArrayAdapter(inflater.getContext(), itemLayout, drawableCache);
		adapter.setNotifyOnChange(true);
		
		lvData = (ListView) v.findViewById(lvResource);
		lvData.addHeaderView(createHeaderView(inflater, lvData), null, false);
		lvData.setAdapter(adapter);
		lvData.setFocusable(false);
		lvData.setOnItemClickListener(this);
		// turns off separating line between items
		lvData.setDividerHeight(0);
		// turns off background and shadow when scrolling listview
		lvData.setCacheColorHint(0);
		lvData.setVerticalFadingEdgeEnabled(false);
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		pullListFromDiskCache();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		dropListToDiskCache();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		makeFragmentAction(CLICK_ITEM_USER, Bundlable.toBundle(adapter.getItem((int) id), User.class));
	}
	
	/**
	 * Pulls list items from disk cache
	 */
	private void pullListFromDiskCache() {
		// pulls from disk cache
		if(getActivity() != null) {
			String appName = getResources().getString(R.string.app_name);
			SharedPreferences dCache = getActivity().getSharedPreferences(appName, Activity.MODE_PRIVATE);
			String users = dCache.getString(this.getClass().getSimpleName(), null);
			if(users != null) {
				// add parsed to adapter
				arrayToAdapter(JsonHelper.jsonToUserArray(users));
			}
		}
	}
	
	@Override
	protected void arrayToAdapter(SparseArray<User> users) {
		if(users != null) {
			int size = users.size();
			for(int i = 0; i < size; i++) {
				User user = users.valueAt(i);
				adapter.insert(user, 0);
//				adapter.add(user);
				// pull picture
				pullPicture(user);
			}
		}
	}
	
	/**
	 * Drops list items to disk cache
	 */
	private void dropListToDiskCache() {
		// pulls from disk cache
		if(getActivity() != null) {
			String built = JsonHelper.userArrayToJson(adapterToUserArray());
			if(built != null) {
				// drops to disk cache
				String appName = getResources().getString(R.string.app_name);
				SharedPreferences dCache = getActivity().getSharedPreferences(appName, Activity.MODE_PRIVATE);
				SharedPreferences.Editor editor = dCache.edit();
				editor.putString(this.getClass().getSimpleName(), built);
				// async dropping without failure notification
				editor.apply(); 
			}
		}
	}
	
	/**
	 * Drops all adapter items into sparse array
	 * 
	 * @return sparse array with users
	 */
	private SparseArray<User> adapterToUserArray() {
		int size = adapter.getCount();
		SparseArray<User> users = new SparseArray<>(size);
		
		for(int i = 0; i < size; i++) {
			User user = adapter.getItem(i);
			users.put(user.getTimestamp(), user);
		}
		return users;
	}

	/**
	 * Instantiates header view for list view
	 * 
	 * @param inflater layout inflater object to create header views from resource layout
	 * @param container parent view of created header
	 * @return header view
	 */
	private View createHeaderView(LayoutInflater inflater, ViewGroup container) {
		
		View headerView = inflater.inflate(headerLayout, container, false);
		
		final EditText etUser = (EditText) headerView.findViewById(R.id.etUser);
		etUser.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// enter pressed
				if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					if(etUser.getText().length() > 1)
						searchUser(etUser.getText().toString().trim());
				}
				return false;
			}
		});
		
		ImageView btnGo = (ImageView) headerView.findViewById(R.id.btnGo);
		btnGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(etUser.getText().length() > 1)
					searchUser(etUser.getText().toString().trim());
			}
		});
		
		return headerView;
	}
	
	/**
	 * Prepares and manages request of searching user
	 * 
	 * @param nick username of user to search
	 */
	private void searchUser(final String nick) {
		// such user is already in list
		if(alreadyInAdapter(nick))
			return;
		
		// another search request is already running
		if(searchRequest > 0)
			// cancel it
			httpHelper.cancelRequest(searchRequest);
		
		// show progress
		showProgress(true);
		
		// make async search
		searchRequest = httpHelper.searchUser(nick, new ServerAnswerCallback() {
			
			@Override
			public void onServerAnswer(Bundle answer) {
				String json = answer.getString(ServerAnswerCallback.JSON);
				if(json != null) {
					User user = JsonHelper.searchUser(json, nick);
					if(user != null) {
						// get found user picture
						pullPicture(user);
						// insert found user into list beginning
						adapter.insert(user, 0);
//						adapter.add(found);
					} else
						Toast.makeText(getActivity(), "Oops, user '" + nick + "' not found", Toast.LENGTH_SHORT).show();
				}
				// flush running request
				searchRequest = 0;
				// hide progress
				showProgress(false);
			}
			
			@Override
			public void onCancel() {
				// flush running request
				searchRequest = 0;
				// hide progress
				showProgress(false);
			}
		});
	}
	
	/**
	 * Investigates whether such user already in adapter or not
	 * 
	 * @param nick username to seek
	 * @return true if such username found, false otherwise
	 */
	private boolean alreadyInAdapter(String nick) {
		int size = adapter.getCount();
		for(int i = 0; i < size; i++) {
			if(adapter.getItem(i).getNick().equals(nick))
				return true;
		}
		return false;
	}
	
	@Override
	protected void pullPicture(User user) {
		if(user != null && user.getId() > 0) {
			// no image in cache
			if(drawableCache.get(user.getId()) == null) {
				// load from server
				if(user.getPictureUrl() != null)
					loadPicture(user.getId(), user.getPictureUrl());
			}
		}
	}
	
	@Override
	protected void loadPicture(final int userId, String url) {
		// make async loading
		httpHelper.loadPicture(url, new ServerAnswerCallback() {
			
			@Override
			public void onServerAnswer(Bundle answer) {
				if(getActivity() != null) {
					Bitmap bitmap = answer.getParcelable(ServerAnswerCallback.PICTURE);
					Drawable pic = new BitmapDrawable(getResources(), bitmap);
					drawableCache.put(userId, pic);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onCancel() {
				// nothing to do here
			}
		});
	}
	
	
}
