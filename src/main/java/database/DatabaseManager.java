package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import media.*;

public class DatabaseManager {
	private final Connection conn;

	public DatabaseManager() {
		this.conn = connectDB();
	}

	// Database Connection
	private Connection connectDB() {
		String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
		String USER = "postgres";
		String PASSWORD = "1029qpwo";
		try {
			return DriverManager.getConnection(DB_URL, USER, PASSWORD);
		} catch (SQLException e) {
			sqlExceptionLogger(e);
			return null;
		}
	}

	/*
	 * Database Users Table user_id ----+---- name ----+---- username ----+----
	 * password ----+---- level ----+---- date_added SERIAL | TEXT | VARCHAR(50) |
	 * VARCHAR(50) | SMALLINT | DATE PR. KEY
	 */
	public void addUser(String name, String username, char[] password, int level) {
		String query = String.join("", "INSERT INTO users (name, username, password, level)", "VALUES (?,?,?,?)");

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			Authentication auth = new Authentication();
			pst.setString(1, name);
			pst.setString(2, auth.hash(username.toCharArray()));
			pst.setString(3, auth.hash(password));
			pst.setInt(4, level);
			pst.executeUpdate();

			System.out.println(String.join("", "[INFO] User successfully added: ", username));
		} catch (SQLException e) {
			sqlExceptionLogger(e);
			System.out.println(String.join("", "[ERROR] User couldn't be added: ", username));
		}
	}

	public void updateLevel(int id, int level) {
		String query = String.join("", "UPDATE users ", "SET level = ? ", "WHERE user_id = ?");

		try (this.conn; PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setInt(1, level);
			pst.setInt(2, id);
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public void removeUser(int id) {
		String query = "DELETE FROM users WHERE user_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setInt(1, id);
			pst.executeUpdate();

		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	/**
	 * @param name the name of the table
	 * @return exit value of the operation
	 */
	public int addTable(String name) {
		String query = String.join("", "CREATE TABLE IF NOT EXISTS ", name, " (", "id SERIAL PRIMARY KEY,",
				"post_id VARCHAR(6),", "title TEXT,", "type CHAR,", "url TEXT,", "a_url TEXT,",
				"p_status BOOLEAN DEFAULT FALSE,", "d_status BOOLEAN DEFAULT FALSE,", "r_status BOOLEAN DEFAULT FALSE,",
				"u_status BOOLEAN DEFAULT FALSE,", "dir TEXT,", "date_added DATE NOT NULL DEFAULT CURRENT_DATE)");

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.executeUpdate();
			return 0;
		} catch (SQLException e) {
			sqlExceptionLogger(e);
			return -1;
		}
	}

	private int checkExistenceOfPostId(String postId, String db) {
		String query = "SELECT COUNT('post_id') FROM " + db + " WHERE 'post_id' = '" + postId + "'";
		int count = 0;
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return count;
	}

	private void addUrl(String db, String type, String post_id, String title, String url, String a_url) {
		if (checkExistenceOfPostId(post_id, db) == 0) {
			String query = String.join("", "INSERT INTO ", db, " (post_id, type, title, url, a_url) ",
					"VALUES(?,?,?,?,?)");

			try (PreparedStatement pst = conn.prepareStatement(query)) {
				pst.setString(1, post_id);
				pst.setString(2, type);
				pst.setString(3, title);
				pst.setString(4, url);
				pst.setString(5, a_url);
				pst.executeUpdate();
			} catch (SQLException e) {
				sqlExceptionLogger(e);
			}
		}
	}

	/**
	 * @param db    refers to the table name
	 * @param media consists of all the acceptable media
	 */
	public void addUrls(String db, List<Media> media) {
		media.parallelStream().forEach(content -> {
			if (content.getClass().getSimpleName().equals("Video")) {
				addUrl(db, "v", content.getPost_id(), content.getTitle(), ((Video) content).getvUrl(),
						((Video) content).getaUrl());
			} else {
				addUrl(db, "i", content.getPost_id(), content.getTitle(), ((Image) content).getiUrl(), null);
			}
		});
	}

	public void updateU_status(int id, boolean status, String db) {
		String query = "UPDATE " + db + " SET u_status = ? WHERE id = ?";
		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setBoolean(1, status);
			pst.setInt(2, id);
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public void updateD_status(int id, boolean status, String db) {
		String query = "UPDATE " + db + " SET d_status = ? WHERE id = ?";
		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setBoolean(1, status);
			pst.setInt(2, id);
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public void updateDir(int id, String dir, String db) {
		String query = "UPDATE " + db + " SET dir = ? WHERE id = ?";
		try (PreparedStatement pst = conn.prepareStatement(query)) {

			pst.setString(1, dir);
			pst.setInt(2, id);
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public void removeOld(String db) {
		String query = "DELETE FROM " + db;
		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public List<Media> getUploadables(String db) {
		String query = "SELECT id, type, title, dir FROM " + db + " WHERE d_status=TRUE AND u_status=FALSE";
		List<Media> list = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				if (rs.getString("type").equals("v")) {
					list.add(new Video(rs.getInt("id"), rs.getString("title"), rs.getString("dir")));
				} else {
					list.add(new Image(rs.getInt("id"), rs.getString("title"), rs.getString("dir")));
				}
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return list;
	}

	public List<Media> getDownloadables(String db) {
		String query = "SELECT id, post_id, type, title, url, a_url FROM " + db
				+ " WHERE p_status=TRUE AND r_status=TRUE";
		List<Media> list = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				if (rs.getString("type").equals("v")) {
					list.add(new Video(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), null,
							rs.getString("url"), rs.getString("a_url")));
				} else {
					list.add(new Image(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), null,
							rs.getString("url")));
				}
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return list;
	}
	public List<Media> byPass_getDownloadables(String db) {
		String query = "SELECT id, post_id, type, title, url, a_url FROM " + db
				+ " WHERE u_status=FALSE";
		List<Media> list = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				if (rs.getString("type").equals("v")) {
					list.add(new Video(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), null,
							rs.getString("url"), rs.getString("a_url")));
				} else if(rs.getString("type").equals("i")){
					list.add(new Image(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), null,
							rs.getString("url")));
				}
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return list;
	}
	public List<Media> byPass_getUploadables(String db) {
		String query = "SELECT id, post_id, type, title, url, dir FROM " + db
				+ " WHERE u_status=FALSE";
		List<Media> list = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				if (rs.getString("type").equals("v")) {
					list.add(new Video(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), rs.getString("dir"),
							rs.getString("url"), null));
				} else {
					list.add(new Image(rs.getInt("id"), rs.getString("post_id"), rs.getString("title"), rs.getString("dir"),
							rs.getString("url")));
				}
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return list;
	}

	public void createInstagramTable() {
		String query = String.join("", "CREATE TABLE IF NOT EXISTS instagram (", "id SERIAL PRIMARY KEY,",
				"shortlink TEXT,", "mail TEXT,", "password BYTEA,", "passwordkey BYTEA,",
				"date_added DATE NOT NULL DEFAULT CURRENT_DATE)");

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public void addInstagramAccount(String shortlink, String mail, byte[] password) throws Exception {
		String query = String.join("", "INSERT INTO instagram (shortlink, mail, password, passwordkey) ",
				"VALUES(?,?,?,?)");

		try (PreparedStatement pst = conn.prepareStatement(query)) {
			Authentication auth = new Authentication();
			pst.setString(1, shortlink);
			pst.setString(2, mail);
			pst.setBytes(3, auth.encrypt(password));
			pst.setBytes(4, auth.getPublicKey());
			pst.executeUpdate();
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
	}

	public String getInstagramPassword(String shortlink) throws Exception {
		String query = "SELECT passwordkey, password FROM instagram WHERE shortlink = '" + shortlink + "'";
		String pw = null;
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			Authentication auth = new Authentication();
			while (rs.next()) {
				pw = new String(auth.decrypt(rs.getBytes(1), rs.getBytes(2)));
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return pw;
	}

	public String getInstagramMail(String shortlink) {
		String query = "SELECT mail FROM instagram WHERE shortlink = '" + shortlink + "'";
		String mail = null;
		try (PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
			while (rs.next()) {
				mail = rs.getString(1);
			}
		} catch (SQLException e) {
			sqlExceptionLogger(e);
		}
		return mail;
	}

	// LOGGER
	private void sqlExceptionLogger(SQLException e) {
		Logger lgr = Logger.getLogger(DatabaseManager.class.getName());
		lgr.log(Level.SEVERE, e.getMessage(), e);
	}
}
