package media;

public class Video extends Media {
	String vUrl, aUrl;

	/**
	 * @param id      an int that refers to the index of the item in the table
	 * @param post_id parsed id from the post
	 * @param title   parsed title from the post
	 * @param dir     directory of the downloaded file
	 * @param vUrl    an absolute URL giving the base location of the video
	 * @param aUrl    an absolute URL giving the base location of the audio
	 */
	public Video(int id, String post_id, String title, String dir, String vUrl, String aUrl) {
		super(id, post_id, title, dir);
		this.vUrl = vUrl;
		this.aUrl = aUrl;
	}

	public Video(int id, String title, String dir) {
		super(id, title, dir);
	}

	public String getvUrl() {
		return vUrl;
	}

	public void setvUrl(String vUrl) {
		this.vUrl = vUrl;
	}

	public String getaUrl() {
		return aUrl;
	}

	public void setaUrl(String aUrl) {
		this.aUrl = aUrl;
	}

	@Override
	public String toString() {
		return "Video [vUrl=" + vUrl + ", aUrl=" + aUrl + ", getId()=" + getId() + ", getPost_id()=" + getPost_id()
				+ ", getTitle()=" + getTitle() + ", getDir()=" + getDir() + "]\n";
	}




}
