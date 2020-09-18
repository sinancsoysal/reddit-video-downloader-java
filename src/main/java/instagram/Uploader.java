package instagram;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramUploadPhotoRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUploadVideoRequest;

import database.DatabaseManager;
import media.Media;

public class Uploader {
	private Instagram4j instagram;
    public Uploader(String shortLink, DatabaseManager dbConn) throws Exception {
        Credentials credentials = new Credentials(shortLink);
        this.instagram = new Instagram4j(credentials.getLogInDetails(dbConn).get("mail"), credentials.getLogInDetails(dbConn).get("password"));
        logIn();
        uploadAll(shortLink, dbConn);
    }

    @SuppressWarnings("static-access")
	private void logIn() throws IOException {
        instagram.builder().build();
        instagram.setup();
        instagram.login();
    }
    private void uploadV(String title, String dir) throws ClientProtocolException, NoSuchAlgorithmException, IOException {
        instagram.sendRequest(new InstagramUploadVideoRequest(
                new File(dir), 
                title.concat(Tag.getRandomTag())));
    }
    private void uploadI(String title, String dir) throws ClientProtocolException, NoSuchAlgorithmException, IOException{
    	instagram.sendRequest(new InstagramUploadPhotoRequest(
    	        new File(dir),
    	        title.concat(Tag.getRandomTag())));
	}
    public void uploadAll(String db, DatabaseManager dbConn){
    	List<Media> media = dbConn.byPass_getUploadables(db);
    	media.stream().forEach(content -> {
			switch(content.getClass().getSimpleName()) {
			case "Video":
				try {
					uploadV(content.getTitle(), content.getDir());
					dbConn.updateU_status(content.getId(), true, db);
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
				break;
				
			case "Image":
				try {
					uploadI(content.getTitle(), content.getDir());
					dbConn.updateU_status(content.getId(), true, db);
				} catch (NoSuchAlgorithmException | IOException e) {
					e.printStackTrace();
				}
				break;
				
			default:
				break;
			}
		});
    }
}
