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
import no.stelar7.api.l4j8.impl.L4J8;
import no.stelar7.api.l4j8.impl.builders.summoner.SummonerBuilder;
import no.stelar7.api.l4j8.impl.raw.DDragonAPI;
import no.stelar7.api.l4j8.pojo.match.Match;
import no.stelar7.api.l4j8.pojo.match.MatchReference;
import no.stelar7.api.l4j8.pojo.match.ParticipantIdentity;
import no.stelar7.api.l4j8.pojo.staticdata.champion.StaticChampion;
import no.stelar7.api.l4j8.pojo.summoner.Summoner;
import no.stelar7.api.l4j8.tests.SecretFile;

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
	public static Set<String> scrapeSummNames(String seed) {
		
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Set<String> allSummNames = new HashSet<>();
		for(int i = 0; i < seedNames.length; i ++) {
			allSummNames.addAll(scrapeSummNames(seedNames[i]));
		}
		
		
		System.out.println(allSummNames.size());
		for(String item : allSummNames) {
			Summoner summoner  = new SummonerBuilder().withPlatform(Platform.NA1).withName(item).get();
	        List<MatchReference> matches = summoner.getGames().get();
	        Collections.sort(matches,Comparator.comparing(MatchReference::getTimestamp));
	        
	        summoner.getAccountId();
	        summoner.getName();
	        summoner.getPUUID();
	        summoner.getSummonerId();
	        summoner.getLeaguePosition();
	        summoner.getProfileIconId();
	        
	        for(int i = matches.size()-1; i>matches.size()-11;i--) {
				MatchReference game = matches.get(i);
				Match match = game.getFullMatch();
				match.getSeason();
				match.getMatchId();
				match.getMatchVersion();
				match.getPlatform().getValue();
				match.getMatchMode().toString();
				match.getGameQueueType().name();
				match.getMatchDuration();
				
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
