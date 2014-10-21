package tk.atna.instagramclient;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representation of single instagram user's media item 
 */
public class Media implements Parcelable {
	
	private String id;
	private String pictureUrl;
	private String title;
	private String type;
	private int created;
	private int likes;
	private int comments;
	
	/**
	 * Constructor
	 * 
	 * @param id media item id
	 */
	public Media(String id) {
		this.id = id;
	}
	
	/**
	 * Returns media item id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns remote url to media item graphical representation 
	 * in needed resolution
	 */
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public Media setPictureUrl(String url) {
		this.pictureUrl = url;
		return this;
	}
	
	/**
	 * Returns media item title
	 */
	public String getTitle() {
		return title;
	}
	
	public Media setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * Returns type of media item ('image' or 'video')
	 */
	public String getType() {
		return type;
	}
	
	public Media setType(String type) {
		this.type = type;
		return this;
	}
	
	/**
	 * Returns media item creation time
	 */
	public int getCreated() {
		return created;
	}
	
	public Media setCreated(int created) {
		this.created = created;
		return this;
	}
	
	/**
	 * Returns media item likes count
	 */
	public int getLikes() {
		return likes;
	}
	
	public Media setLikes(int likes) {
		this.likes = likes;
		return this;
	}
	
	/**
	 * Returns media item comments count
	 */
	public int getComments() {
		return comments;
	}
	
	public Media setComments(int comments) {
		this.comments = comments;
		return this;
	}
	
	public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
		
		public Media createFromParcel(Parcel in) {
			return new Media(in);
		}
		
		public Media[] newArray(int size) {
			return new Media[size];
		}
	};
	
	/**
	 * Private constructor. 
	 * Rebuilds media object from parcel previously stored with writeToParcel()
	 * 
	 * @param parcel object to rebuild media from
	 */
	private Media(Parcel parcel) {
		this.id = parcel.readString();
		this.pictureUrl = parcel.readString();
		this.title = parcel.readString();
		this.type = parcel.readString();
		this.created = parcel.readInt();
		this.likes = parcel.readInt();
		this.comments = parcel.readInt();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(pictureUrl);
		dest.writeString(title);
		dest.writeString(type);
		dest.writeInt(created);
		dest.writeInt(likes);
		dest.writeInt(comments);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	
// instagram server answer example (data object only)	
/*
	
{
"attribution":null,
"videos":{
		"low_resolution":
			{
			"url":"http:\/\/*\/10467350_1459224184318683_256384235_a.mp4",
			"width":480,
			"height":480
			},
		"standard_resolution":
			{
			"url":"http:\/\/*\/10461075_769484616405774_1730541975_n.mp4",
			"width":640,
			"height":640
			},
		"low_bandwidth":
			{
			"url":"http:\/\/*\/10470302_320037104820535_1651911465_s.mp4",
			"width":480,
			"height":480
			}
		},
"tags":["jimmychoo"],
"location":null,
"comments":{
			"count":0,
			"data":[]
			},
"filter":"Normal",
"created_time":"1402424525",
"link":"http:\/\/instagram.com\/p\/pEsvWKTcFC\/",
"likes":{
		"count":6,
		"data":[{
				"username":"espositok11",
				"profile_picture":"http:\/\/*\/10471858_292112054302251_2139412004_a.jpg",
				"id":"233843968",
				"full_name":"Kevin Esposito \u00a9"
				},
				{
				"username":"bouletjava",
				"profile_picture":"http:\/\/*\/1208152_986819358002062_2083360675_a.jpg",
				"id":"1141727641",
				"full_name":"MArt\u00edn BArrios"
				},
				{
				"username":"edsonpanduro",
				"profile_picture":"http:\/\/*\/profile_39680485_75sq_1393347689.jpg",
				"id":"39680485",
				"full_name":"Eds\ud83c\udd7en \ud83c\udd7f\ufe0fanduro"
				},
				{
				"username":"santiago_iglesias",
				"profile_picture":"http:\/\/*\/profile_180189575_75sq_1397963032.jpg",
				"id":"180189575",
				"full_name":"santiago_iglesias"
				}]
		},
"images":{
		"low_resolution":{
						"url":"http:\/\/*\/10369294_568065879977136_1403554489_a.jpg",
						"width":306,
						"height":306
						},
		"thumbnail":{
					"url":"http:\/\/*\/10369294_568065879977136_1403554489_s.jpg",
					"width":150,
					"height":150
					},
		"standard_resolution":{
							"url":"http:\/\/*\/10369294_568065879977136_1403554489_n.jpg",
							"width":640,
							"height":640
							}
		},
"users_in_photo":[],
"caption":{
		"created_time":"1402424525",
		"text":"Un secreto... #jimmychoo",
		"from":{
				"username":"soyyodanna",
				"profile_picture":"http:\/\/*\/profile_1229975_75sq_1376979124.jpg",
				"id":"1229975",
				"full_name":"Danna DLRM"
				},
		"id":"739913007719367082"
		},
"type":"video",
"id":"739913006452687170_1229975",
"user":{
		"username":"soyyodanna",
		"website":"",
		"profile_picture":"http:\/\/*\/profile_1229975_75sq_1376979124.jpg",
		"full_name":"Danna DLRM",
		"bio":"",
		"id":"1229975"
		}
},	
	
{
"attribution":null,
"tags":["caninolove","jimmychoo"],
"location":null,
"comments":{
			"count":0,
			"data":[]
			},
"filter":"Rise",
"created_time":"1404998028",
"link":"http:\/\/instagram.com\/p\/qRZTrUTcDl\/",
"likes":{
		"count":5,
		"data":[{
				"username":"bouletjava",
				"profile_picture":"http:\/\/*\/1208152_986819358002062_2083360675_a.jpg",
				"id":"1141727641",
				"full_name":"MArt\u00edn BArrios"
				},
				{
				"username":"rosepalafox",
				"profile_picture":"http:\/\/*\/10299637_1476365032600177_1281199343_a.jpg",
				"id":"401103213",
				"full_name":"Rosa Mar\u00eda Palafox Scott"
				},
				{
				"username":"venomweb00",
				"profile_picture":"http:\/\/*\/profile_243440598_75sq_1351370548.jpg",
				"id":"243440598",
				"full_name":"Gerardo delaRMtz"
				},
				{
				"username":"pilarcachonr",
				"profile_picture":"http:\/\/*\/10413138_734293559968553_2053116777_a.jpg",
				"id":"468533263",
				"full_name":"Pilar Cachon"
				}]
		},
"images":{
		"low_resolution":{
						"url":"http:\/\/*\/924430_322190327939649_2130349543_a.jpg",
						"width":306,
						"height":306
						},
		"thumbnail":{
					"url":"http:\/\/*\/924430_322190327939649_2130349543_s.jpg",
					"width":150,
					"height":150
					},
		"standard_resolution":{
								"url":"http:\/\/*\/924430_322190327939649_2130349543_n.jpg",
								"width":640,
								"height":640
								}
		},
"users_in_photo":[],
"caption":{
			"created_time":"1404998028",
			"text":"Esto es amor....... #caninolove #jimmychoo",
			"from":{
					"username":"soyyodanna",
					"profile_picture":"http:\/\/*\/profile_1229975_75sq_1376979124.jpg",
					"id":"1229975",
					"full_name":"Danna DLRM"
					},
			"id":"761501115699020661"
			},
"type":"image",
"id":"761501115346698469_1229975",
"user":{
		"username":"soyyodanna",
		"website":"",
		"profile_picture":"http:\/\/*\/profile_1229975_75sq_1376979124.jpg",
		"full_name":"Danna DLRM",
		"bio":"",
		"id":"1229975"
		}
}	
	
*/	
	
	
}
