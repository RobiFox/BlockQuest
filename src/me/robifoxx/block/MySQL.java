package me.robifoxx.block;

import java.sql.*;

public class MySQL {

	private String Host;
	private String Database;
	private String User;
	private String Password;

	private Connection con;

	public MySQL(String host, String database, String user, String password) {

		Host = host;
		Database = database;
		User = user;
		Password = password;

		connect();

	}

	public void connect() {

		try {

			con = DriverManager.getConnection("jdbc:mysql://" + Host + ":3306/" + Database + "?autoReconnect=true", User, Password);

		} catch(SQLException e) {

			System.out.println("[MagicPVP MySQL] Connection failed: " + e.getMessage());

		}

	}

	public void close() {

		try {

			if(con != null) {

				con.close();

			}

		} catch(SQLException e) {

			System.out.println("[MagicPVP MySQL] Connection couldnt be closed: " + e.getMessage());

		}

	}

	public void update(String qry) {

		try {

			Statement st = con.createStatement();
			st.executeUpdate(qry);
			st.close();

		} catch(SQLException e) {

			connect();
			System.err.println(e);

		}

	}

	public boolean hasConnection() {

		if(con != null) {

			return true;

		} else {

			return false;

		}

	}

	public ResultSet query(String qry) {

		ResultSet rs = null;

		try {

			Statement st = con.createStatement();
			rs = st.executeQuery(qry);

		} catch(SQLException e) {

			connect();
			System.err.println(e);

		}

		return rs;

	}

}
