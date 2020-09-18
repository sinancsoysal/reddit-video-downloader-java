package main;

import database.DatabaseManager;
import instagram.Uploader;
import json.JsonManager;
import process.Downloader;

public class App {
    public static void main(String[] args) throws Exception {
//        System.out.println(System.getProperty("user.dir"));
//        Merger.merge("./Downloads/wpdi/i9fsjk/");ya
//        Downloader.downloadV("https://v.redd.it/5flh4rol6mg51/DASH_480.mp4?source=fallback", "./Downloads/uvid/i8r67g");

//        Runtime r=Runtime.getRuntime();
//
//        System.out.println("No of Processor: "+
//                r.availableProcessors());
//        System.out.println("Total memory: "+r.totalMemory());
//        System.out.println("Free memory: "+r.freeMemory());
//        System.out.println("Memory occupied: "+
//                (r.totalMemory()-r.freeMemory()));

//        ProcessManager.run("ffmpeg -i video.mp4 -i audio.wav -c:v copy -c:a aac output.mp4");
//    	DatabaseManager dbConn = new DatabaseManager();
//        removeOld(dbConn);
//    	addTables(dbConn);
        start();	
//        createInstagramTable();
//    	System.out.println(JsonManager.readJsonFromFile("/home/eniax/eclipse-workspace/redditVideoHandler/downloads/uvid/ibxxrn").getJSONArray("streams").getJSONObject(0).get("display_aspect_ratio"));
    	
//    	Downloader.scaleIfNotAppropriateAndReturnNameOfTheFile("/home/eniax/eclipse-workspace/redditVideoHandler/downloads/pcut/idluiy/");
    }

	
	  private static void createInstagramTable(DatabaseManager dbConn) throws
	  Exception { dbConn.createInstagramTable(); dbConn.addInstagramAccount("uvid",
	  "unexpectedvideos.instagram@gmail.com",
	  "b!CQ'y?]vR%*}wUUFP!97d)@k9}ZEEZc".getBytes());
	  dbConn.addInstagramAccount("pcut", "perfectlycutscreams.instagram@gmail.com",
	  "b!CQ'y?]vR%*}wUUFP!97d)@k9}ZEEZc".getBytes());
	  dbConn.addInstagramAccount("wpdi",
	  "watchpeopledieinside.instagram@gmail.com",
	  "b!CQ'y?]vR%*}wUUFP!97d)@k9}ZEEZc".getBytes()); } private static void
	  addTables(DatabaseManager dbConn){ dbConn.addTable("uvid");
	  dbConn.addTable("pcut"); dbConn.addTable("wpdi"); } private static void
	  removeOld(DatabaseManager dbConn){ dbConn.removeOld("uvid");
	  dbConn.removeOld("pcut"); dbConn.removeOld("wpdi"); }
	 
    private static void start(){
//        new MyThread("uvids", "uvid", "https://www.reddit.com/r/Unexpected/");
//        new MyThread("pcuts", "pcut", "https://www.reddit.com/r/perfectlycutscreams/");
        new MyThread("wpdis", "wpdi", "https://www.reddit.com/r/WatchPeopleDieInside/");
    }

//    private static void parse(String url){
//       dbConn.addUrls(JsonParser.parseUrls(url), "uvid");
//    }
//    private static void down() throws IOException {
//        Downloader.downloadAll("uvid");
//    }
//    private static void createdb() {
//       dbConn.addTable("pcut");DataBaseUtils.addTable("wpdi");
//    }
}
class MyThread implements Runnable{
    private final String shortlink;
    private final String url;
    Thread t;
    MyThread(String threadName, String shortlink, String url){
        this.shortlink=shortlink;
        this.url=url;
        t = new Thread(this, threadName);
        t.start();
    }
    @Override
    public void run() {
    	run(new DatabaseManager());
    }
    public void run(DatabaseManager dbConn){
        try {
            parse(this.url, this.shortlink, dbConn);
//        	process.Downloader.downloadAll(shortlink, dbConn);
//            new Uploader(this.shortlink, dbConn);
//        } catch (InterruptedException e){
//            System.out.println(this.threadName + " interrupted!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private static void parse(String url, String shortlink, DatabaseManager dbConn){
       dbConn.addUrls(shortlink, JsonManager.parseUrls(url));
    }

}
