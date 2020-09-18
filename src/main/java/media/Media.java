package media;

public class Media {
	private int id;
	private String post_id, title, dir;

	public Media() {
		super();
	}

	public Media(int id, String post_id, String title, String dir) {
		super();
		this.id = id;
		this.post_id = post_id;
		this.title = title;
		this.dir = dir;
	}

	public Media(int id, String title, String dir) {
		this.id = id;
		this.title = title;
		this.dir = dir;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPost_id() {
		return post_id;
	}

	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	@Override
	public String toString() {
		return "Media [id=" + id + ", post_id=" + post_id + ", title=" + title + ", dir=" + dir + "]";
	}

}
