package tk.atna.instagramclient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Implementation of ArrayAdapter with items of type User.
 * 
 */
public class UserArrayAdapter extends BaseArrayAdapter<User> {
	
	// string resource
	private final static int USER_ID = R.string.id;
	private String userId;
	
	/**
	 * Constructor
	 * 
	 * @param context the current context
	 * @param itemResource layout id of a single adapter item
	 * @param drawableCache local memory cache to pull pictures from
	 */
	public UserArrayAdapter(Context context, int itemResource, SparseArray<Drawable> drawableCache) {
		super(context, itemResource, drawableCache);
		
		// pull string resource for view with user id
		this.userId = context.getResources().getString(USER_ID);
	}
	
	@Override
	protected View fillItemViews(View container, final int position, ViewGroup parent) {
		
		TextView tvNick = (TextView) container.findViewById(R.id.tvNick);
		tvNick.setText(getItem(position).getNick());
		
		TextView tvId = (TextView) container.findViewById(R.id.tvId);
		tvId.setText(userId + getItem(position).getId());
		
		ImageView ivPicture = (ImageView) container.findViewById(R.id.ivPicture);
		setDrawable(ivPicture, drawableCache.get(getItem(position).getId()));
		
		// button to remove item from list
		View btnRemove = container.findViewById(R.id.btnRemove);
		if(btnRemove != null) {
			btnRemove.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// remove from list
					remove(getItem(position));
				}
			});
		}
		
		return container;
	}
	
}
