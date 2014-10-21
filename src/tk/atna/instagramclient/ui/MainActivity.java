package tk.atna.instagramclient.ui;

import tk.atna.instagramclient.R;
import tk.atna.instagramclient.ui.BaseFragment.FragmentActionCallback;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity implements FragmentActionCallback {
	
	/**
	 * Resource id of xml layout container for fragments
	 */
	private int container = R.id.flContainer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// show entry fragment with instagram users management
		UserListFragment fragment = new UserListFragment();
		if(fragment != null) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(container, fragment, UserListFragment.TAG);
			ft.commit();
		}
	}
	
	@Override
	public void onFragmentAction(int action, Bundle data) {
		switch (action) {
		
		case UserListFragment.CLICK_ITEM_USER:
			
			// load media fragment with chosen user pictures
			UserMediaFragment fragment = UserMediaFragment.init(data);
			if(fragment != null) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(container, fragment, UserMediaFragment.TAG);
				ft.addToBackStack(UserMediaFragment.TAG);
				ft.commit();
			}
			break;
			
		case UserMediaFragment.CLICK_ITEM_PICTURE:
			
			// show dialog with detailed information about picture
			MediaDetailsDialog dialog = MediaDetailsDialog.init(data);
			dialog.show(getFragmentManager(), MediaDetailsDialog.TAG);
			
			break;
		}
	}
	
	
}
