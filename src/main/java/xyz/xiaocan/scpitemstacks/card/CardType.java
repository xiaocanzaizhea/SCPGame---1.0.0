package xyz.xiaocan.scpitemstacks.card;

public enum CardType {
    Janitor("Janitor"),
    Scientist("Scientist"),
    Research("Research"),
    Zone("Zone"),
    Guard("Guard"),
    Private1("Private1"),
    Engineer("Engineer"),
    Mtfspy("Mtfspy"),
    Commander("Commander"),
    Facility("Facility"),
    Decoder("Decoder"),
    O5("O5");
    public String id;
    CardType(String id){
        this.id = id;
    }
}
