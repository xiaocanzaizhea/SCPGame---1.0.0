package xyz.xiaocan.configload.option.scpoption;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import xyz.xiaocan.scpitemstacks.card.CardType;

import java.util.*;

@Setter
@Getter
public class SCP914Setting {

    public static SCP914Setting instance;
    public long startTime;
    public long handleTime;

    public Map<CardType, List<List<String>>> ConversionList; //cardtype->list
    public SCP914Setting(double startTime,
                         double handleTime, Map<CardType, List<List<String>>> list){
        this.startTime = (long)(startTime * 20);
        this.handleTime = (long)(handleTime * 20);
        this.ConversionList = list;
    }
    public static SCP914Setting getInstance(){
        if(instance==null){
            Bukkit.getLogger().warning("scp914配置错误");
            return null;
        }
        return instance;
    }

    public static void setInstance(SCP914Setting scp914Setting){
        instance = scp914Setting;
    }

    public String getRandomCardOfList(CardType cardType,int model){
        if(cardType==null)return null;

        if(!ConversionList.containsKey(cardType))return null;

        List<List<String>> lists = ConversionList.get(cardType);
        List<String> strings = lists.get(model);
        Collections.shuffle(strings);
        Random random = new Random();
        int num = random.nextInt(strings.size());
        String converCardId = strings.get(num);

        if(converCardId.equals("[DESTROY]"))return null;

        return converCardId;
    }

    @Override
    public String toString() {
        return "SCP914Setting{" +
                "startTime=" + startTime +
                ", handleTime=" + handleTime +
                ", ConversionList=" + ConversionList.size() +
                '}';
    }
}
