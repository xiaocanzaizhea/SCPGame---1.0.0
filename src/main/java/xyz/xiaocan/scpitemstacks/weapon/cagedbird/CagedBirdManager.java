package xyz.xiaocan.scpitemstacks.weapon.cagedbird;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CagedBirdManager{

    private static CagedBirdManager instance;

    public Map<UUID, CagedBird> cageBirdMap;

    private CagedBirdManager(){
        cageBirdMap = new HashMap<>();
    }

    public static CagedBirdManager getInstance(){
        if(instance ==null){
            instance = new CagedBirdManager();
        }
        return instance;
    }
}
