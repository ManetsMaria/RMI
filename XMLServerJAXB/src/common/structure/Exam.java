package common.structure;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * <p>Вспомогательный Класс-сущность для {@link Student}, предоставляющая информацию об экзамене</p>
 *
 * Помечен аннотациями для генерации <tt><a href="https://ru.wikipedia.org/wiki/XML_Schema_(W3C)">.xsd файла</a></tt>
 *
 * @see XmlType
 * @author Manets Mariya
 */
@XmlType(propOrder = { "mark", "subject"}, name = "exam")
public class Exam implements Serializable {
    private double mark;
    private String subject;

    public Exam(){

    }

    public Exam(double mark, String subject) {
        this.mark = mark;
        this.subject = subject;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "mark=" + mark +
                ", subject='" + subject + '\'' +
                '}';
    }
}
