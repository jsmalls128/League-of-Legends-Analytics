import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.stelar7.api.l4j8.basic.cache.impl.FileSystemCacheProvider;
import no.stelar7.api.l4j8.basic.calling.DataCall;
import no.stelar7.api.l4j8.basic.constants.api.LogLevel;
import no.stelar7.api.l4j8.basic.constants.api.Platform;
import no.stelar7.api.l4j8.basic.constants.types.TeamType;
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.builders.summoner.SummonerBuilder;
import no.stelar7.api.l4j8.impl.raw.DDragonAPI;
import no.stelar7.api.l4j8.pojo.championmastery.ChampionMastery;
import no.stelar7.api.l4j8.pojo.league.LeaguePosition;
import no.stelar7.api.l4j8.pojo.match.Match;
import no.stelar7.api.l4j8.pojo.match.MatchReference;
import no.stelar7.api.l4j8.pojo.match.Participant;
import no.stelar7.api.l4j8.pojo.match.ParticipantIdentity;
import no.stelar7.api.l4j8.pojo.match.ParticipantStats;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;

public class main {

	final static String[] seedNames = {"jsmalls128","TSM Johnsun", "Scarf Zone", "Taiga", "Damonte", "Livie", "Jefito", "Maplesurps", "MegaTouch9000", "Hatasawa"};
  	public static void populateChampions() {
		
		final L4J8 l4j8 = new L4J8(SecretFile.CREDS);
		DDragonAPI api = l4j8.getDDragonAPI();
        DataCall.setCacheProvider(new FileSystemCacheProvider());
        DataCall.setLogLevel(LogLevel.DEBUG);        
        Map<Integer, StaticChampion> list = api.getChampions();
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            for (Map.Entry<Integer, StaticChampion> entry : list.entrySet()) {
                int key = entry.getKey();
                String value = entry.getValue().getName();
                String sql = "insert into championdata values(" + key+" , \""+value+"\" )";
                stmt.execute(sql);
            }
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
	}
  	public static void populateSummoner(String accountId, String summonerName, String PUUID, String summID, String league,int level, int profileIcon) {
        /* In database change profileIcon to int
         * replace matchHistory with level int
         * change accountId to varchar
         */
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            String sql = "insert into summoner values('"+accountId+"' , \""+summonerName+"\", \""+PUUID+"\",'"+summID+"','"+league+"',"
            		+level+ ","+profileIcon+" )";
            stmt.execute(sql);
            
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
	}
  	public static void populateMastery(String summonerName,long points, int champId) {
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            String sql = "insert into mastery values("+champId+", \""+summonerName+"\","+points+")";
            stmt.execute(sql);
            
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
	}
	public static Set<String> scrapeSummNames(String seed) {
		final L4J8 l4j8 = new L4J8(SecretFile.CREDS);
        String   user   = seed;
        Platform region = Platform.NA1;
		Summoner summoner  = new SummonerBuilder().withPlatform(region).withName(user).get();
        List<MatchReference> matches = summoner.getGames().get();
        // Sort matches by date ASC
		Collections.sort(matches,Comparator.comparing(MatchReference::getTimestamp));
		Set<String> summonerNames = new HashSet<>();
		
		for(int i = matches.size()-1; i>matches.size()-16;i--) {
			MatchReference game = matches.get(i);
			Match match = game.getFullMatch();
			List<ParticipantIdentity> participantIds = match.getParticipantIdentities();
			
			for(ParticipantIdentity p : participantIds) {
				summonerNames.add(p.getSummonerName());
			}
			
		}
		return summonerNames;
	}		
	public static void populatePlayed(long gameId,String summonerName,String victory, String team, int champId, String summ1, String summ2, String role ) {
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            String sql = "insert into played values("+gameId+",\""+summonerName+"\","+champId+",'"+victory+"','"+team+"',"
            		+ "'"+summ1+"','"+summ2+"','"+role+"')";
            stmt.execute(sql);
            
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
		
	}
	public static void populateMatchStats(long gameId, String summonerName, long kills, long deaths, long assists, long totalMinionsKilled, long wardsPlaced,
			long trueDmgToChampions,long magicDmgToChampions, long physicalDmgToChampions, long goldEarned, long visionScore) {
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            String sql = "insert into matchstats values("+gameId+", \""+summonerName+"\","+kills+", "+deaths+","+assists+","+totalMinionsKilled+","
            		+ wardsPlaced+","+trueDmgToChampions+","+magicDmgToChampions+","+physicalDmgToChampions+","+goldEarned+","+visionScore+")";
            stmt.execute(sql);
            
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
		
	}
	public static void populateMatch(String season,long gameId, String patch, String region, String gameMode, String map, String gameType, long gameLength ) {
		try {  
			Connection conn = MySQLJDBCUtil.getConnection();
            Statement stmt  = conn.createStatement();
            stmt.execute("use finaldb");
            String sql = "insert IGNORE into matches values('"+season+"',"+gameId+",'"+patch+"','"+region+"','"+gameMode+"',"
            		+ "\""+map+"\",'"+gameType+"',"+gameLength+")";
            stmt.execute(sql);
            
        }
        catch(SQLException ex) {
        	System.out.println(ex.getMessage());
        	System.exit(1);
        }
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		populateChampions();
		
		Set<String> allSummNames = new HashSet<>();
		for(int i = 0; i < seedNames.length; i ++) {
			allSummNames.addAll(scrapeSummNames(seedNames[i]));
		}
		
		
		System.out.println(allSummNames.size());
		for(String item : allSummNames) {
			Summoner summoner  = new SummonerBuilder().withPlatform(Platform.NA1).withName(item).get();
	        
	        // Summoner Information
			if(summoner == null) {
				continue;
			}
	        String accountId = summoner.getAccountId();
	        String summonerName = summoner.getName();
	        String PUUID = summoner.getPUUID();
	        String summID = summoner.getSummonerId();
	        List<LeaguePosition> leagueInfo = summoner.getLeagueEntry();
	        String league = "UNRANKED";
	        int level = summoner.getSummonerLevel();
	        if(!leagueInfo.isEmpty())
	        	league = leagueInfo.get(0).getTier() + " " + leagueInfo.get(0).getRank();
	        int profileIcon = summoner.getProfileIconId();
	        populateSummoner(accountId,summonerName, PUUID, summID, league,level, profileIcon); 
	        System.out.println("Made Summ");
	        
	        // Mastery Information
	        List<ChampionMastery> masteries = summoner.getChampionMasteries();
	        for(ChampionMastery champ: masteries) {
	        	long points = champ.getChampionPoints();
		        int championId = champ.getChampionId();
		        populateMastery(summonerName,points,championId);
	        }
	        
	        
	        List<MatchReference> matches = summoner.getGames().get();
	        Collections.sort(matches,Comparator.comparing(MatchReference::getTimestamp));
	        
	        for(int i = matches.size()-1; i>matches.size()-11;i--) {
				// match
	        	MatchReference game = matches.get(i);
				Match match = game.getFullMatch();
				String season = match.getSeason().name();  // SEASON_2019
				long gameId = match.getMatchId(); // matchId == gameId
				String patch = match.getMatchVersion(); // patch number
				String region = match.getPlatform().name(); // region
				String gameMode = match.getMatchMode().toString(); // == GAMEMODE
				String map = match.getMap().name();
				String gameType = match.getGameQueueType().name(); // QUEUETYPE
				long gameLength = match.getMatchDuration().getSeconds(); // GAME LENGTH IN SECS
				populateMatch(season,gameId,patch,region,gameMode,map,gameType,gameLength);
				
				
				// Played		
				Participant self = match.getParticipantFromSummonerId(summoner.getSummonerId());
				int championId = self.getChampionId();
				String teamColor = self.getTeam().name();
				String summ1 = "UNKNOWN"; 
				String summ2 = "UNKNOWN";
				try {
					summ1 = self.getSpell1().toString();
					summ2 = self.getSpell2().toString();
				}catch(Exception e) {
					System.out.println("NULL????");
				}
				finally {
					String vic = "NO";
					boolean victory = match.didWin(self);
					String role = self.getTimeline().getLane() +" "+ self.getTimeline().getRole();
					if(victory) {
						vic ="YES";
					}
					populatePlayed(gameId,summonerName,vic, teamColor,championId, summ1,summ2, role );
					
					// matchstats
					ParticipantStats playerStats = self.getStats();
					long kills = playerStats.getKills();
					long assists = playerStats.getAssists();
					long deaths = playerStats.getDeaths();
					long totalMinionsKilled = playerStats.getTotalMinionsKilled();
					long wardsPlaced = playerStats.getWardsPlaced();
					long truDmgToChampions = playerStats.getTrueDamageDealtToChampions();
					long magicDmgToChampions = playerStats.getMagicDamageDealtToChampions();
					long physicalDmgToChampions = playerStats.getPhysicalDamageDealtToChampions();
					long goldEarned = playerStats.getGoldEarned();
					long visionScore = playerStats.getVisionScore();
					populateMatchStats(gameId,summonerName,kills,deaths,assists,totalMinionsKilled,wardsPlaced,
							truDmgToChampions,magicDmgToChampions,physicalDmgToChampions,
							goldEarned,visionScore);
					
				
					
				}
				}
				
		}
		
		/*
		Match                match         = recentGame.getFullMatch();
        Participant          self          = match.getParticipantFromSummonerId(summoner.getSummonerId()); //game data for user (summs, champ etc)
        StaticChampion       champion      = champData.get(recentGame.getChampionId());
        MatchPerks           summs         = self.getPerks();
        boolean              won           = match.didWin(self);
        ParticipantIdentity  opponentId    = match.getLaneOpponentIdentity(self); //get lane opponent id
        Participant          opponent      = match.getParticipantFromParticipantId(opponentId.getParticipantId()); //summs, champ, etc for lane opponent
        StaticChampion       opponentChamp = champData.get(opponent.getChampionId());
        
        
        System.out.println("Profile icon: " + pfp);
        System.out.println(name + ", Level " + level);
        System.out.println();
        System.out.format(name + " %s their most recent game.", won ? "won" : "lost");
        System.out.println();
        System.out.println("They were playing " + self.getTimeline().getLane() + " " + champion.getName() + " against " + opponentChamp.getName() + ".");	
		*/   

	}

}
