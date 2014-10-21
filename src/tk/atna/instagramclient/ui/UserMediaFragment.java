package tk.atna.instagramclient.ui;

import tk.atna.instagramclient.Bundlable;
import tk.atna.instagramclient.Media;
import tk.atna.instagramclient.MediaArrayAdapter;
import tk.atna.instagramclient.Pagination;
import tk.atna.instagramclient.R;
import tk.atna.instagramclient.User;
import tk.atna.instagramclient.http.HttpHelper.ServerAnswerCallback;
import tk.atna.instagramclient.http.JsonHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMediaFragment extends BaseFragment<Media> {
	
	public final static String TAG = UserMediaFragment.class.getName();
	
	// layout resources for fragment views
	private int userMediaLayout = R.layout.user_media_layout;
	private int itemLayout = R.layout.media_item_layout;
	
	// grid view resource
	private int gvResource = R.id.gvData;
	private GridView gvData;
	
	private final static int CAPTION_TAIL = R.string.media_caption_tail;
	private TextView tvCaption;
	
	/**
	 * User whose media fragment manages
	 */
	private User user;
	
	// default medias count per loading request
	private final static int MEDIAS_PER_LOAD = 10; 
	
	/**
	 * 
	 */
	private Pagination pagination;
	
	/**
	 * Adapter contains items for grid view
	 */
	private MediaArrayAdapter adapter;
	
	/**
	 * Identifier of currently executing http request
	 */
	private int mediaRequest;
	
	/**
	 * Local memory cache for pictures from user media set
	 */
	private final SparseArray<Drawable> drawableCache = new SparseArray<Drawable>();
	
	
	/**
	 * Creates fragment and sends initialization data to it
	 * 
	 * @param data bundle object with initialization data
	 * @return initialized fragment or null
	 */
	public static UserMediaFragment init(Bundle data) {
		UserMediaFragment fragment = new UserMediaFragment();
		fragment.setArguments(data);
		return fragment;
	}
	
	/**
	 * Private constructor
	 */
	private UserMediaFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(userMediaLayout, container, false);
		
		// initialize adapter
		adapter = new MediaArrayAdapter(inflater.getContext(), itemLayout, drawableCache) {

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				// detect the end of grid
				if(position == (getCount() - 1))
					// make additional loading
					getUserMedia();
				
				return super.getView(position, convertView, parent);
			}
		};
		adapter.setNotifyOnChange(true);
		
		gvData = (GridView) v.findViewById(gvResource);
		gvData.setAdapter(adapter);
		gvData.setFocusable(false);
		gvData.setOnItemClickListener(this);
		// turns off background and shadow when scrolling listview
		gvData.setCacheColorHint(0);
		gvData.setVerticalFadingEdgeEnabled(false);
		
		tvCaption = (TextView) v.findViewById(R.id.tvCaption);
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// limit number of medias per load
		httpHelper.setMediasPerLoad(MEDIAS_PER_LOAD);
		
		// requests user media
		getUserMedia();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		makeFragmentAction(CLICK_ITEM_PICTURE, Bundlable.toBundle(adapter.getItem(position), Media.class));
	}
	
	@Override
	protected void processArguments(Bundle args) {
		User user = Bundlable.fromBundle(args, User.class);
		if(user != null) {
			// remember user
			this.user = user;
			// update caption
			tvCaption.setText(user.getNick() + getResources().getString(CAPTION_TAIL));
		}
	}
	
	/**
	 * Prepares and manages request of getting user media set
	 */
	private void getUserMedia() {
		// define id of user to load media for
		final int userId = user != null ? user.getId() : 0;
		
		// check what page to load first or not
		String nextPage = (pagination != null && pagination.getNextUrl() != null) 
								? pagination.getNextUrl() : null;
		
		// another media request is already running
		if(mediaRequest > 0)
			// cancel it
			httpHelper.cancelRequest(mediaRequest);
		
		// show progress
		showProgress(true);
		
		// make async media request
		mediaRequest = httpHelper.getUserMedia(userId, nextPage, new ServerAnswerCallback() {
			
			@Override
			public void onServerAnswer(Bundle answer) {
				String raw = answer.getString(ServerAnswerCallback.JSON);
				if(raw != null) {
					// parse pagination
					pagination = JsonHelper.getPagination(raw);
					// parse user media set
					SparseArray<Media> medias = JsonHelper.parseUserMedia(raw);
					if(medias != null && medias.size() > 0) {
						arrayToAdapter(medias);
					} else
						Toast.makeText(getActivity(), "Oops, user '" + user.getNick() + "' doesn't have photos to show", Toast.LENGTH_SHORT).show();
				}
				// flush running request
				mediaRequest = 0;
				// hide progress
				showProgress(false);
			}
			
			@Override
			public void onCancel() {
				// flush running request
				mediaRequest = 0;
				// hide progress
				showProgress(false);
			}
		});
	}
	
	@Override
	protected void arrayToAdapter(SparseArray<Media> medias) {
		if(medias != null) {
			int size = medias.size();
			for(int i = size - 1; i > -1; i--) {
				Media media = medias.valueAt(i);
				adapter.add(media);
				// pull preview picture
				pullPicture(media);
			}
		}
	}
	
	@Override
	protected void pullPicture(Media media) {
		int created;
		if(media != null && (created = media.getCreated()) > 0) {
			// no image in cache
			if(drawableCache.get(created) == null) {
				// load from server
				if(media.getPictureUrl() != null)
					loadPicture(created, media.getPictureUrl());
			}
		}
	}
	
	@Override
	protected void loadPicture(final int picId, String url) {
		// make async loading
		httpHelper.loadPicture(url, new ServerAnswerCallback() {
			
			@Override
			public void onServerAnswer(Bundle answer) {
				if(getActivity() != null) {
					Bitmap bitmap = answer.getParcelable(ServerAnswerCallback.PICTURE);
					Drawable pic = new BitmapDrawable(getResources(), bitmap);
					drawableCache.put(picId, pic);
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
