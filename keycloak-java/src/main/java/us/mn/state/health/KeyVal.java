package us.mn.state.health;

/**
 * Created by malmsh1 on 3/19/2018.
 */
public class KeyVal {
    String key;
    String value;
    
    public KeyVal(){
    }
    
    public KeyVal(String k, String v){
        this.key = k;
        this.value = v;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
