# Query for winrate by rank All replace 'name' with input
SELECT (SELECT count(*) from played where championId = (SELECT championId from championdata WHERE name = "jax") AND victory ="YES")/
(SELECT count(*) from played where championId = (SELECT championId from championdata WHERE name = "jax")) as winratio, name, championId from championdata where name = "jax";

# Query for winrate by rank certain replace 'name' with input and like attribute with rank
WITH tempMatches as (Select * from played natural join summoner where league like 'gold%')
Select 
(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = "jax") AND victory ="YES")/
(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = "jax")) as winratio, name, championId from championdata where name = "jax";

# Query for match history stats replace 'summonerName'
SELECT * FROM (summoner NATURAL JOIN played) natural join matchstats WHERE summonerName = "jsmalls128" ;

# Pickrate of champions all elos
Select count(*), name,(count(*))/(select count(*) from matches) as PickRatio from championdata cd natural join played p group by name;

# Query for champ pickrate by rank
WITH tempMatches as (Select * from played natural join summoner where league like 'gold%')
Select 
(SELECT count(*) from tempMatches where championId = (SELECT championId from championdata WHERE name = "jax"))/
(SELECT count(*) from tempMatches) as PickRatio, name, championId from championdata where name = "jax";

# Query for champ winrate by patch


# Average vision score per rank
WITH vis as (SELECT summonerName, avg(visionScore) as avgVisionScore FROM matchstats group by summonerName)
select avg(avgVisionScore) as avgVisionScore,league from vis natural join summoner group by league;