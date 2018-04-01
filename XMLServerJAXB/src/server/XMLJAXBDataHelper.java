package server;

import common.service.RemoteService;
import common.structure.Exam;
import common.structure.Student;
import common.structure.view.Group;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Класс служит для реализации процедур {@link RemoteService}
 * для работы с базой файловой системой посредством XML-файлов
 * средствами <tt><a href="https://ru.wikipedia.org/wiki/Java_Architecture_for_XML_Binding">JAXB</a></tt></p>
 *
 * @author Manets Mariya
 */
public class XMLJAXBDataHelper implements RemoteService {

    public static final String FILE_NAME = "XMLServerJAXB/xml/schema1.xsd.xml";

    /**
     *@see RemoteService#add(Student)
     */
    @Override
    public boolean add(Student student) throws RemoteException {
        List <Student> studentList = getAllStudents();
        if (studentList.contains(student)){
            return false;
        }
        studentList.add(student);
        try{
            write(new Students(studentList));
            return true;
        } catch (FileNotFoundException | JAXBException  e) {
            return false;
        }
    }

    /**
     *@see RemoteService#delete(String, int)
     */
    @Override
    public Student delete(String name, int group) throws RemoteException {
        List <Student> studentList = getAllStudents();
        int index = studentList.indexOf(new Student(name, group, new ArrayList<>()));
        if (index < 0){
            return null;
        }
        Student student = studentList.remove(index);
        try{
            write(new Students(studentList));
            return student;
        } catch (FileNotFoundException | JAXBException  e) {
            return null;
        }
    }

    /**
     *@see RemoteService#deleteAll()
     */
    @Override
    public boolean deleteAll() throws RemoteException {
        try {
            write(new Students());
            return true;
        } catch (JAXBException | FileNotFoundException e) {
            return false;
        }
    }

    /**
     *@see RemoteService#getAllStudents()
     */
    @Override
    public List<Student> getAllStudents() throws RemoteException {
        try {
            JAXBContext context = JAXBContext.newInstance(Students.class);
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            File schemaLocation = new File("XMLServerJAXB/xml/schema1.xsd");
            Schema schema =  factory.newSchema(schemaLocation);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            Students students = (Students) unmarshaller.unmarshal(new File(FILE_NAME));
            return students.getStudents();
        } catch (SAXException | JAXBException e) {
            System.err.print(e);
            return null;
        }
    }

    private void write(Students students) throws JAXBException, FileNotFoundException {

        JAXBContext context = JAXBContext.newInstance(Students.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(FILE_NAME));
        marshaller.marshal(students, fileOutputStream);
    }

    /**
     *@see RemoteService#join()
     */
    @Override
    public List<Group> join() throws RemoteException {
        List<Student> students = getAllStudents();
        if (students == null){
            return null;
        }
        for (int i = 0; i < students.size(); i++){
            if (!isNeed(students.get(i))){
                students.remove(i);
                i--;
            }
        }
        System.out.println(students);
        List<Group> groups = new ArrayList<>();
        if (students.isEmpty()){
            return groups;
        }
        Comparator<Student> comparator = Comparator.comparing(student -> student.getGroup());
        comparator = comparator.thenComparing(Comparator.comparing(student -> student.getLastName()));
        students.sort(comparator);
        Group currentGroup = null;
        int previousGroup = -1;
        for (Student student: students) {
            int group = student.getGroup();
            if (previousGroup != group) {
                currentGroup = new Group(group);
                groups.add(currentGroup);
                previousGroup = group;
            }
            currentGroup.addStudent(student.getLastName());
        }
        return groups;
    }

    private boolean isNeed(Student student){
        List<Exam> exams = student.getExams();
        for (Exam exam: exams){
            if (exam.getMark() < 4){
                return true;
            }
        }
        return false;
    }
}
