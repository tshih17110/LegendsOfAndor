package com.example.LegendsOfAndor;

import java.util.ArrayList;

enum TurnOptions{
    MOVE, FIGHT, NONE
}
enum GameStatus{
    GAME_WON, GAME_LOST, GAME_ERROR, IN_PROGRESS
}

public class Game  {
    private int maxNumPlayers;
    private int currentNumPlayers;
    private int goldenShields;
    private Player[] players;
    private String gameName;
    private boolean isActive;
    private boolean itemsDistributed;
    private String itemsDistributedMessage;
    private RegionDatabase regionDatabase;
    private Hero currentHero;
    private TurnOptions currentHeroSelectedOption;
    private ArrayList<Farmer> farmers;
    private Boolean difficultMode;
    private Fight currentFight;
    private GameStatus gameStatus;
    private Narrator narrator;

    public Game() {}

    public Game(Player p, int maxNumPlayers, String gameName, Boolean difficult) {
        this.narrator = new Narrator();
        this.gameName = gameName;
        this.maxNumPlayers = maxNumPlayers;
        this.currentNumPlayers = 1;
        this.players = new Player[maxNumPlayers];
        this.players[0] = p;
        if(difficult){
            regionDatabase = new RegionDatabase(Difficulty.HARD);
        }else{
            regionDatabase = new RegionDatabase(Difficulty.EASY);
        }
        farmers = new ArrayList<Farmer>();
        this.difficultMode = difficult;
        itemsDistributedMessage = "";
        currentHero = null;
        this.gameStatus = GameStatus.IN_PROGRESS;
        this.narrator = new Narrator();
    }

    public int getMaxNumPlayers() {
        return maxNumPlayers;
    }

    public void setMaxNumPlayers(int maxNumPlayers) {
        this.maxNumPlayers = maxNumPlayers;
    }

    public int getCurrentNumPlayers() {
        return currentNumPlayers;
    }

    public void setCurrentNumPlayers(int currentNumPlayers) {
        this.currentNumPlayers = currentNumPlayers;
    }

    public int getGoldenShields() {
        return goldenShields;
    }

    public void setGoldenShields(int goldenShields) {
        this.goldenShields = goldenShields;
    }

    public Player[] getPlayers() {
        return players;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isItemsDistributed() {
        return itemsDistributed;
    }

    public void setItemsDistributed(boolean itemsDistributed) {
        this.itemsDistributed = itemsDistributed;
    }

    public Player getSinglePlayer(String username) {
        for (Player p : players) {
            if (p.getUsername().equals(username)) {
                return p;
            }
        }
        return null;
    }

    public void addPlayer(Player p) {
        for (int i = 0; i < maxNumPlayers; i++) {
            if (players[i] == null) {
                players[i] = p;
                currentNumPlayers++;
                break;
            }
        }
    }

    public void removePlayer(String username) {
        for (int i = 0; i < currentNumPlayers; i++) {
            if (players[i].getUsername().equals(username)) {
                players[i].setHero(null);
                players[i].setReady(false);
                for (int j = i; j < maxNumPlayers; j++) {
                    if (j == maxNumPlayers-1) {
                        players[j] = null;
                    } else {
                        players[j] = players[j + 1]; // shift every element down by one unit
                    }
                }
                currentNumPlayers--;
                return;
            }
        }
    }

    public ArrayList<HeroClass> getAllHeroes() {
        ArrayList<HeroClass> heroes = new ArrayList<>();
        for (int i = 0; i < currentNumPlayers; i++) {
            if (players[i].getHero() != null) {
                heroes.add(players[i].getHero().getHeroClass());
            }
        }
        return heroes;
    }

    public boolean allReady() {
        for (int i = 0; i < currentNumPlayers; i++) {
            if (!players[i].isReady()) {
                return false;
            }
        }
        return true;
    }

    public Hero getNextHero(String username) {
        int currentHeroIndex=0;
        for (int i = 0; i < currentNumPlayers; i++) {
            if (players[i].getUsername().equals(username)) {
                currentHeroIndex = i;
            }
        }

        for (int i = 1; i < currentNumPlayers; i++) {
            if (currentHeroIndex+i == currentNumPlayers) {
                currentHeroIndex = i * -1;
            }
            System.out.println(currentHeroIndex+i);
            if (!players[currentHeroIndex+i].getHero().isHasEndedDay()) {
                return players[currentHeroIndex+i].getHero();
            }

        }
        return null;
    }

    public RegionDatabase getRegionDatabase() {
        return regionDatabase;
    }

    public Hero getCurrentHero() {
        return currentHero;
    }

    public void setCurrentHero(Hero currentHero) {
        this.currentHero = currentHero;
    }

    public TurnOptions getCurrentHeroSelectedOption() {
        return currentHeroSelectedOption;
    }

    public void setCurrentHeroSelectedOption(TurnOptions currentHeroSelectedOption) {
        this.currentHeroSelectedOption = currentHeroSelectedOption;
    }

    public void appendToDistributedItemsMessage(String message){
        this.itemsDistributedMessage += message + "\n";
    }

    public String getItemsDistributedMessage(){
        return itemsDistributedMessage;
    }

    public ArrayList<Farmer> getFarmers() {
        return farmers;
    }

    public void setFarmers(ArrayList<Farmer> farmers) {
        this.farmers = farmers;
    }

    public Boolean getDifficultMode() {
        return difficultMode;
    }

    public void setDifficultMode(Boolean difficultMode) {
        this.difficultMode = difficultMode;
    }

    public Fight getCurrentFight() {
        return currentFight;
    }

    public void setCurrentFight(Fight currentFight) {
        this.currentFight = currentFight;
    }

    public GameStatus getGameStatus() { return gameStatus; }

    public void setGameStatus(GameStatus gameStatus) { this.gameStatus = gameStatus; }

    public Narrator getNarrator() {
        return narrator;
    }

    public void setNarrator(Narrator narrator) {
        this.narrator = narrator;
    }
}
