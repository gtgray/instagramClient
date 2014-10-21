package tk.atna.instagramclient;

/**
 * Represents data to get next set of instagram user's media
 */
public class Pagination {
	
	private String nextUrl;
//	private String nextMaxId;
	
	/**
	 * Constructor
	 * 
	 * @param nextUrl remote url of next user's media set 
	 */
	public Pagination(String nextUrl/*, String nextMaxId*/) {
		this.nextUrl = nextUrl;
//		this.nextMaxId = nextMaxId;
	}
	
	/**
	 * Returns remote url of next user's media set 
	 */
	public String getNextUrl() {
		return nextUrl;
	}
	
//	public String getNextMaxId() {
//		return nextMaxId;
//	}
	
}
