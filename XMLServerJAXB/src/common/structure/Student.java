package common.structure;

import java.io.Serializable;
import java.util.List;
import common.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Класс-сущность, представляющая информацию о студенте</p>
 *
 * Помечен аннотациями для генерации <tt><a href="https://ru.wikipedia.org/wiki/XML_Schema_(W3C)">.xsd файла</a></tt>
 *
 * @see XmlType
 * @author Manets Mariya
 */
@XmlType(propOrder = { "lastName", "group", "exams"}, name = "student")
public class Student implements Serializable {
    private String lastName;
    private int group;
    private int id;
    private List<Exam> exams;

    public Student(){

    }

    public Student(String lastName, int group, List<Exam> exams) {
        this.lastName = lastName;
        this.group = group;
        this.exams = exams;
        id = GeneratorId.getId(lastName, group);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * @see XmlElementWrapper
     * @see XmlElement
     * @return список экзаменов
     */
    @XmlElementWrapper(name = "exams")
    @XmlElement(name = "exam")
    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    @Override
    public String toString() {
        return "Student{" +
                "lastName='" + lastName + '\'' +
                ", group=" + group +
                ", exams=" + exams.toString() +
                '}';
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        return id == student.id || (lastName.equals(student.lastName) && group == student.group);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
