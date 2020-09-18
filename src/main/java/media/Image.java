package media;

public class Image extends Media{
	String iUrl;
	/**
	 * @param	id		an int that refers to the index of the item in the table
	 * @param	post_id parsed id from the post
	 * @param	title 	parsed title from the post
	 * @param	dir		directory of the downloaded file 
	 * @param	iUrl	an absolute URL giving the base location of the image
	 */
	public Image(int id, String post_id, String title, String dir, String iUrl) {
		super(id, post_id, title, dir);
		this.iUrl = iUrl;
	}
	public Image(int id, String title, String dir) {
		super(id, title, dir);
	}

	public String getiUrl() {
		return iUrl;
	}

	public void setiUrl(String iUrl) {
		this.iUrl = iUrl;
	}

	@Override
	public String toString() {
		return "Image [iUrl=" + iUrl + ", getId()=" + getId() + ", getPost_id()=" + getPost_id() + ", getTitle()="
				+ getTitle() + ", getDir()=" + getDir() + "]\n";
	}
	
}
