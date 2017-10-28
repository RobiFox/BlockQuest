package me.robifoxx.block.mysql;

import me.robifoxx.block.Main;
import me.robifoxx.block.Utils;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLPlayer {

	static String database = "BlockQuest";
	static String column = "UUID";

	public static boolean playerExists(String p) {

		try {

			ResultSet rs = Main.mysql.query("SELECT * FROM " + database + " WHERE " + column +"= '" + p + "'");

			if(rs.next()) {

				return rs.getString("NAME") != null;

			}

		} catch(SQLException e) {

			e.printStackTrace();

		}

		return false;

	}

	public static void createPlayer(Player p, int x, int y, int z, String world) {

		Main.mysql.update("INSERT INTO " + database + " (UUID, X, Y, Z, WORLD) VALUES ('" + Utils.getIdentifier(p) + "', '" + x + "', '" + y + "', '" + z + "', '" + world + "')");

	}

	public static int getInteger(String p, String path) {

		Integer i = 0;

		if(playerExists(p)) {

			try {

				ResultSet rs;

				rs = Main.mysql.query("SELECT * FROM " + database + " WHERE " + column + "='" + p + "'");

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

				rs = Main.mysql.query("SELECT * FROM " + database + " ORDER BY ID DESC LIMIT 1");

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

			Main.mysql.update("UPDATE " + database + " SET " + path + "= '" + value + "' WHERE " + column + "='" + p + "';");

		}

	}

	public static void addInteger(String p, String path, Integer value) {

		if(playerExists(p)) {

			Main.mysql.update("UPDATE " + database + " SET " + path + "= '" + (getInteger(p, path) + value) + "' WHERE " + column + "='" + p + "';");

		}

	}

	public static String getString(String p, String path) {

		String i = "";

		if(playerExists(p)) {

			try {

				ResultSet rs;

				rs = Main.mysql.query("SELECT * FROM " + database + " WHERE " + column + "='" + p + "'");

				if(!rs.next()
						|| rs.getString(path) == null);

				i = rs.getString(path);

			} catch(SQLException e) {

				e.printStackTrace();

			}

		}

		return i;

	}

	public static void setString(String p, String path, String value) {

		if (playerExists(p)) {

			Main.mysql.update("UPDATE " + database + " SET " + path + "= '" + value + "' WHERE " + column + "='" + p + "';");

		}

	}

}
