package common.util;

/**
 * <p>Утилитный класс для генерации id на основе имени и группы</p>
 *
 * @author Manets Mariya
 */

public class GeneratorId {

    public static int getId(String lastName, int group){
        return lastName.hashCode()+group*10;
    }
}
