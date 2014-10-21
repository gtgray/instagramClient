package tk.atna.instagramclient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Implementation of ArrayAdapter with items of type Media.
 * 
 */
public class MediaArrayAdapter extends BaseArrayAdapter<Media> {
	
	// string resource
	private final static int LIKED = R.string.liked;
	private String liked;
	
	/**
	 * Constructor
	 * 
	 * @param context the current context
	 * @param itemResource layout id of a single adapter item
	 * @param drawableCache local memory cache to pull pictures from
	 */
	public MediaArrayAdapter(Context context, int itemResource, SparseArray<Drawable> drawableCache) {
		super(context, itemResource, drawableCache);
		
		// pull string resource for view with likes
		this.liked = context.getResources().getString(LIKED);
	}
	
	@Override
	protected View fillItemViews(View container, int position, ViewGroup parent) {
		
		TextView tvLikes = (TextView) container.findViewById(R.id.tvLikes);
		tvLikes.setText(liked + getItem(position).getLikes());
		
		ImageView tvPicture = (ImageView) container.findViewById(R.id.tvPicture);
		setDrawable(tvPicture, drawableCache.get(getItem(position).getCreated()));
		
		// set height of container same as it's weight (square mosaics)
		int side = ((GridView) parent).getColumnWidth();
		container.setLayoutParams(new LayoutParams(side, side));
		
		return container;
	}
	
}
