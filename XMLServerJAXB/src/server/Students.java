package server;

import common.structure.Student;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


/**
 * <p>Вспомогательный класс, содержащий список {@link Student} для работы со средствами <tt><a href="https://ru.wikipedia.org/wiki/Java_Architecture_for_XML_Binding">JAXB</a></tt></p>
 *
 * Помечен аннотациями для генерации <tt><a href="https://ru.wikipedia.org/wiki/XML_Schema_(W3C)">.xsd файла</a></tt>
 *
 * @see XmlType
 * @see XmlRootElement
 * @author Manets Mariya
 */
@XmlType(propOrder = { "students"}, name = "students")
@XmlRootElement
public class Students {
    private List<Student> students;

    public Students(){

    }

    public Students(List<Student> students){
        this.students = students;
    }

    @XmlElement(name = "student")
    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
