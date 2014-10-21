package tk.atna.instagramclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents instagram user
 */
public class User implements Parcelable {
	
	private int id;
	private String nick;
	private String pictureUrl;
	private int timestamp;
	
	/**
	 * Constructor
	 * 
	 * @param id user id
	 */
	public User(int id) {
		this.id = id;
	}
	
	/**
	 * Returns user id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns user username
	 */
	public String getNick() {
		return nick;
	}
	
	public User setNick(String nick) {
		this.nick = nick;
		return this;
	}
	
	/**
	 * Returns remote url of user's profile picture
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public User setPictureUrl(String url) {
		this.pictureUrl = url;
		return this;
	}
	
	/**
	 * Returns time stamp when user object was created
	 */
	public int getTimestamp() {
		return timestamp;
	}
	
	public User setTimestamp(int timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		
		public User createFromParcel(Parcel in) {
			return new User(in);
		}
		
		public User[] newArray(int size) {
			return new User[size];
		}
	};
	
	/**
	 * Private constructor. 
	 * Rebuilds user object from parcel previously stored with writeToParcel()
	 * 
	 * @param parcel object to rebuild user from
	 */
	private User(Parcel parcel) {
		this.id = parcel.readInt();
		this.nick = parcel.readString();
		this.pictureUrl = parcel.readString();
		this.timestamp = parcel.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(nick);
		dest.writeString(pictureUrl);
		dest.writeInt(timestamp);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	
}
