package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DBServlet
 */
@WebServlet("/DBServlet")
public class DBServlet extends HttpServlet {
	boolean errorThrown = false;
	static final String JDBCUrl = "jdbc:mysql://localhost:3306/finaldb";
	static final String JDBCUser = "root";
	static final String JDBCPass = "Jsmalls128!";
	static Connection conn = null;
	static String value = null;
	static int columnCount = 0;
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public DBServlet() {
		// TODO Auto-generated constructor stub

	}

	public Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(getURL(), getUser(), getPass());
			System.out.println("Connected!");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

	public String getURL() {
		return JDBCUrl;
	}

	public String getUser() {
		return JDBCUser;
	}

	public String getPass() {
		return JDBCPass;
	}

	public int getColumnCount() {
		return columnCount;
	}
	public String inputParser() {
		String[] words = value.split(" ");
		String output = "";
		if(words[0].equalsIgnoreCase("winrate")) {
			if(words[2].equalsIgnoreCase("all")) {
				output = "SELECT (SELECT count(*) from played where championId = (SELECT championId from championdata WHERE name = \""+words[1]+"\") AND victory =\"YES\")/\r\n" + 
						"(SELECT count(*) from played where championId = (SELECT championId from championdata WHERE name = \""+words[1]+"\")) as winratio, name, championId from championdata where name = \""+words[1]+"\";";
			}
			else {
			output = "WITH tempMatches as (Select * from played natural join summoner where league like '"+ words[2]+"%')" + 
					"Select" + 
					"(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = \""+ words[1]+"\") AND victory =\"YES\")/" + 
					"(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = \""+words[1]+"\")) as winratio, name, championId from championdata where name = \""+words[1]+"\"";
			}
		}
		else if(words[0].equalsIgnoreCase("patchwinrate")) {
			output = "WITH tempMatches as\r\n" + 
					"(SELECT gameVersion, played.gameId, championId, victory from matches join played on played.gameId = matches.gameId Where  matches.gameVersion like'"+words[2]+"%')\r\n" + 
					"Select \r\n" + 
					"(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = \""+words[1] +"\") AND victory =\"YES\")/\r\n" + 
					"(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = \""+words[1] +"\")) as winratio, name, championId from championdata where name = \""+words[1]+"\";";
		}
		else if(words[0].equalsIgnoreCase("visionscore")){
			output = "WITH vis as (SELECT summonerName, avg(visionScore) as avgVisionScore FROM matchstats group by summonerName)\r\n" + 
					"select avg(avgVisionScore) as avgVisionScore,league from vis natural join summoner group by league order by avgVisionScore DESC;";
		}
		else if(words[0].equalsIgnoreCase("counters")) {
			output = "with perchamp (wins, games, champvsID, champvsName, role) as (SELECT SUM(case when vic = 'YES' then 1 else 0 end) wins, COUNT(*) games, t.champid2, name, role \r\n" + 
					"    FROM\r\n" + 
					"		(SELECT tab2.championID as champid2, tab1.victory as vic, name, tab1.role as role\r\n" + 
					"		FROM (played as tab1\r\n" + 
					"        JOIN played as tab2 on tab1.gameID = tab2.gameID and tab1.role = tab2.role and tab1.championID != tab2.championID and tab1.victory != tab2.victory)\r\n" + 
					"        JOIN championdata on tab2.championid = championdata.championId\r\n" + 
					"        JOIN matches on tab1.gameId = matches.gameid and matches.gamemode = 'CLASSIC'\r\n" + 
					"        WHERE tab1.championID = (SELECT championId from championdata WHERE name = \""+words[1]+"\")) as t\r\n" + 
					"        GROUP BY t.champid2\r\n" + 
					"        ORDER BY wins ASC\r\n" + 
					"        )\r\n" + 
					"        \r\n" + 
					"SELECT wins/games as winRate, games as numGames, champVsID, champVsName, role from perchamp;";
		}
		else if(words[0].equalsIgnoreCase("compliments")) {
			output = "with perchamp (wins, games, champWithID, champWithName) as (SELECT SUM(case when vic = 'YES' then 1 else 0 end) wins, COUNT(*) games, t.champid2, name \r\n" + 
					"    FROM\r\n" + 
					"		(SELECT tab2.championID as champid2, tab1.victory as vic, name\r\n" + 
					"		FROM (played as tab1\r\n" + 
					"        JOIN played as tab2 on tab1.gameID = tab2.gameID and tab1.championID != tab2.championID)\r\n" + 
					"        JOIN championdata on tab2.championid = championdata.championId\r\n" + 
					"        JOIN matches on tab1.gameId = matches.gameid and matches.gamemode = 'CLASSIC'\r\n" + 
					"        WHERE tab1.championID = (SELECT championId from championdata WHERE name = \""+words[1]+"\")) as t\r\n" + 
					"        GROUP BY t.champid2\r\n" + 
					"        order by wins desc\r\n" + 
					"        )\r\n" + 
					"SELECT wins/games as winRate, games, champWithID, champWithName from perchamp;\r\n";
		}
		else if (words[0].equalsIgnoreCase("matchhistory")) {
			output = "SELECT summonername,gameId,victory,team,role,kills,deaths,assists, totalMinionsKilled as cs,goldEarned,visionScore FROM (summoner NATURAL JOIN played) natural join matchstats WHERE summonerName = \""+words[1]+"\" ;";
		}
		else if(words[0].equalsIgnoreCase("pickrate")) {
			output = "Select count(*) as count, name,(count(*))/(select count(*) from matches) as PickRatio from championdata cd natural join played p group by name order by count DESC;";
		}
		System.out.println(output);
		return output;
	}
	// Get button value from index.jsp
	public String getQueryValue() {
		// Insert parsing right here
		return inputParser();
	}

	public static String[] getChampionList() {
		String champions = "Aatrox,Ahri,Akali,Alistar,Amumu,Anivia,Annie,Ashe,Aurelion Sol,Azir,Bard,Blitzcrank,Brand,Braum,"
				+ "Caitlyn,Camille,Cassiopeia,Cho'Gath,Corki,Darius,Diana,Dr. Mundo,Draven,Ekko,Elise,Evelynn,"
				+ "Ezreal,Fiddlesticks,Fiora,Fizz,Galio,Gangplank,Garen,Gnar,Gragas,Graves,Hecarim,Heimerdinger,"
				+ "Illaoi,Irelia,Ivern,Janna,Jarvan IV,Jax,Jayce,Jhin,Jinx,Kalista,Karma,Karthus,Kassadin,Katarina,Kayle,"
				+ "Kayn,Kennen,Kha'Zix,Kindred,Kled,Kog'Maw,LeBlanc,Lee Sin,Leona,Lissandra,Lucian,Lulu,Lux,"
				+ "Malphite,Malzahar,Maokai,Master Yi,Miss Fortune,Mordekaiser,Morgana,Nami,Nasus,Nautilus,Neeko,Nidalee,"
				+ "Nocturne,Nunu,Olaf,Orianna,Ornn,Pantheon,Poppy,Pyke,Quinn,Rakan,Rammus,Rek'Sai,Renekton,Rengar,"
				+ "Riven,Rumble,Ryze,Sejuani,Shaco,Shen,Shyvana,Singed,Sion,Sivir,Skarner,Sona,Soraka,Swain,Sylas,"
				+ "Syndra,Tahm Kench,Tailyah,Talon,Taric,Teemo,Thresh,Tristana,Trundle,Tryndamere,"
				+ "Twisted Fate,Twitch,Udyr,Urgot,Varus,Vayne,Veigar,Vel'Koz,Vi,Viktor,Vladimir,"
				+ "Volibear,Warwick,Wukong,Xayah,Xerath,Xin Zhao,Yasuo,Yorick,Zac,Zed,Ziggs,Zilean,Zoe,Zyra";
		return champions.split(",");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		// create the mysql insert preparedstatement

		value = request.getParameter("champName");
		String destination = "WEB-INF/result.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(destination); 
		dispatcher.forward(request, response);

		// execute the preparedstatement


	}


	public static String[][] getResults(String query, Connection conn) throws SQLException {

		// create the mysql insert preparedstatement
		ResultSet rs;
		String[][] rowValues = null;
		rs = conn.createStatement().executeQuery(query);

		// get rowCount and columnCount
		rs.last();
		int rowCount = rs.getRow();
		rs.beforeFirst();
		columnCount = rs.getMetaData().getColumnCount();

		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		rowValues = new String[rowCount+1][columnCount];

		for (int i = 0; i < columnCount; i++) {
			rowValues[0][i] = rs.getMetaData().getColumnName(i+1);
		}


		// populate rowValues[row][col] to input into table, row=1 because row[0] is the column headers
		int row = 1;
		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (rs.getString(i).equals("")) {
					rowValues[row][i - 1] = "null";
				} else {
					rowValues[row][i - 1] = rs.getString(i);
				}

			}
			System.out.println("");
			row++;
		}

		return rowValues;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);

	}

}
