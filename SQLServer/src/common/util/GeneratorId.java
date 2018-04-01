package common.util;

public class GeneratorId {

    public static int getId(String lastName, int group){
        return lastName.hashCode()+group*10;
    }
}
