package tk.atna.instagramclient.ui;

import tk.atna.instagramclient.Bundlable;
import tk.atna.instagramclient.Media;
import tk.atna.instagramclient.R;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MediaDetailsDialog extends DialogFragment {
	
	public final static String TAG = MediaDetailsDialog.class.getName();
	
	// container for dialog fragment
	private int detailsDialogLayout = R.layout.media_details_dialog_layout;
	
	// string resources
	private final static int ID = R.string.id;
	private final static int TYPE = R.string.type;
	private final static int CREATED = R.string.created;
	private final static int LIKED = R.string.liked;
	private final static int COMMENTED = R.string.commented;
	private final static int TITLE = R.string.title;
	
	/**
	 * Date format to represent created date
	 */
	private final static String CREATED_FORMAT = "d MMMM yyyy";
	
	/**
	 * Contains detailed data about chosen picture
	 */
	private Media media;
	
	/**
	 * @param data
	 * @return
	 */
	public static MediaDetailsDialog init(Bundle data) {
		MediaDetailsDialog dialog = new MediaDetailsDialog();
		dialog.setArguments(data);
		return dialog;
	}
	
	/**
	 * Private constructor
	 */
	private MediaDetailsDialog() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// sets style and theme to dialog window
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Dialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(detailsDialogLayout, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// unwrap arguments with detailed data about media
		if(getArguments() != null) {
			media = Bundlable.fromBundle(getArguments(), Media.class);
			if(media != null)
				// fill views with detailed data
				fillDialogViews();
		}
	}
	
	/**
	 * Fills dialog views with data
	 */
	private void fillDialogViews() {
		View v = getView();
		if(v != null) {
			Resources r = v.getResources();
			
			TextView tvId = (TextView) v.findViewById(R.id.tvId);
			tvId.setText(r.getString(ID) + media.getId());

			TextView tvType = (TextView) v.findViewById(R.id.tvType);
			tvType.setText(r.getString(TYPE) + media.getType());

			TextView tvCreated = (TextView) v.findViewById(R.id.tvCreated);
			tvCreated.setText(r.getString(CREATED) 
					+ (DateFormat.format(CREATED_FORMAT, ((long) media.getCreated() * 1000))).toString());
			
			TextView tvLikes = (TextView) v.findViewById(R.id.tvLikes);
			tvLikes.setText(r.getString(LIKED) + media.getLikes());

			TextView tvComments = (TextView) v.findViewById(R.id.tvComments);
			tvComments.setText(r.getString(COMMENTED) + media.getComments());

			TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
			tvTitle.setText(r.getString(TITLE) + media.getTitle());
		}
	}
	
}
