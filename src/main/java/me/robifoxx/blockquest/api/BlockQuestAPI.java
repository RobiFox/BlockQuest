package me.robifoxx.blockquest.api;

public class BlockQuestAPI {
    private static BlockQuestAPI instance;
    
    public static BlockQuestAPI getInstance() {
        return instance == null ? (instance = new BlockQuestAPI()) : instance;
    }
    
    private BlockQuestAPI() { }
}
