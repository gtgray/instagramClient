package tk.atna.instagramclient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * Implementation of ArrayAdapter with drawables memory cache.
 * 
 */
public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {
	
	/**
	 * Placeholder for picture views with no loaded picture
	 */
	protected final static int BLANK_DRAWABLE = R.drawable.ic_blank_pic;
	
	/**
	 * Resource identifier of xml layout of a single adapter item view
	 */
	private int itemResource;
	
	/**
	 * Local memory cache for pictures of adapter items
	 */
	protected SparseArray<Drawable> drawableCache;
	
	/**
	 * Constructor
	 * 
	 * @param context current context
	 * @param itemResource layout id of a single adapter item
	 * @param drawableCache local memory cache to pull pictures from
	 */
	public BaseArrayAdapter(Context context, int itemResource, SparseArray<Drawable> drawableCache) {
		super(context, itemResource);
		this.itemResource = itemResource;
		this.drawableCache = drawableCache;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) 
			convertView = LayoutInflater.from(getContext()).inflate(itemResource, parent, false);
		
		return fillItemViews(convertView, position, parent);
	}
	
	/**
	 * Fill views of adapter item each time it redraws on getView
	 * 
	 * @param container parent view of current adapter item
	 * @param position position of current adapter item 
	 * @param parent parent view of the container
	 * @return filled container
	 */
	protected abstract View fillItemViews(View container, int position, ViewGroup parent);
	
	/**
	 * Sets a drawable as the content of the view, if no drawable sets placeholder 
	 * 
	 * @param view image view to set drawable
	 * @param drawable picture to set to view
	 */
	protected void setDrawable(ImageView view, Drawable drawable) {
		if(drawable == null) 
			view.setImageResource(BLANK_DRAWABLE);
		else
			view.setImageDrawable(drawable);
	}
	
	
}
