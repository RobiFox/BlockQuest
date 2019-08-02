package me.robifoxx.block.mysql;

import me.robifoxx.block.Main;
import me.robifoxx.block.Utils;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLPlayer {

	static String database = Main.getPlugin(Main.class).getDescription().getName();
	static String column = "UUID";

	public static boolean playerExists(String p) {
		try {
			ResultSet rs = Main.getPlugin(Main.class).mysql.query("SELECT * FROM " + database + " WHERE " + column +"= '" + p + "'");
			if(rs.next()) {
				return rs.getString(column) != null;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void createPlayer(Player p, String x, String y, String z, String world) {
		if(!playerExists(Utils.getIdentifier(p)))
		Main.getPlugin(Main.class).mysql.update("INSERT INTO " + database + " (UUID, X, Y, Z, WORLD) VALUES ('" + Utils.getIdentifier(p) + "', '" + x + "', '" + y + "', '" + z + "', '" + world + "')");

	}

	public static int getInteger(String p, String path) {

		Integer i = 0;

		if(playerExists(p)) {

			try {

				ResultSet rs;

				rs = Main.getPlugin(Main.class).mysql.query("SELECT * FROM " + database + " WHERE " + column + "='" + p + "'");

				if(!rs.next()
						|| Integer.valueOf(rs.getInt(path)) == null);

				i = rs.getInt(path);

			} catch(SQLException e) {

				e.printStackTrace();

			}

		}

		return i;

	}

	public static int getLatestId(String p) {

		Integer i = 0;
		String path = "id";

		if(playerExists(p)) {

			try {

				ResultSet rs;

				rs = Main.getPlugin(Main.class).mysql.query("SELECT * FROM " + database + " ORDER BY ID DESC LIMIT 1");

				if(!rs.next()
						|| Integer.valueOf(rs.getInt(path)) == null);

				i = rs.getInt(path);

			} catch(SQLException e) {

				e.printStackTrace();

			}

		}

		return i;

	}
	public static void setInteger(String p, String path, Integer value) {

		if(playerExists(p)) {

			Main.getPlugin(Main.class).mysql.update("UPDATE " + database + " SET " + path + "= '" + value + "' WHERE " + column + "='" + p + "';");

		}

	}

	public static void addInteger(String p, String path, Integer value) {

		if(playerExists(p)) {

			Main.getPlugin(Main.class).mysql.update("UPDATE " + database + " SET " + path + "= '" + (getInteger(p, path) + value) + "' WHERE " + column + "='" + p + "';");

		}

	}

	public static String getString(String p, String path) {

		String i = "";

		if(playerExists(p)) {

			try {

				ResultSet rs;

				rs = Main.getPlugin(Main.class).mysql.query("SELECT * FROM " + database + " WHERE " + column + "='" + p + "'");

				if(!rs.next()
						|| rs.getString(path) == null);

				i = rs.getString(path);

			} catch(SQLException e) {

				e.printStackTrace();

			}

		}

		return i;

	}

	public static List<String> getAll() {
		List<String> i = new ArrayList<>();
		try {
			ResultSet rs;
			rs = Main.getPlugin(Main.class).mysql.query("SELECT * FROM " + database);
			while(rs.next()) {
				i.add(rs.getString(1));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return i;
	}

	public static void setString(String p, String path, String value) {
		if (playerExists(p)) {
			Main.getPlugin(Main.class).mysql.update("UPDATE " + database + " SET " + path + "= '" + value + "' WHERE " + column + "='" + p + "';");
		}
	}
}
