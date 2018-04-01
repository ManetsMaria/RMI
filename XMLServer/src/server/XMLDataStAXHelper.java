package server;

import common.service.RemoteService;
import common.structure.Exam;
import common.structure.Student;
import common.structure.view.Group;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class XMLDataStAXHelper implements RemoteService {

    public static final String FILE_NAME = "XMLServer/xml/student.xsd.xml";
    private XMLStreamReader reader;
    private XMLEventWriter writer;
    private XMLEventReader eventReader;
    private XMLEventFactory eventFactory;



    private boolean connect(){
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaLocation = new File("XMLServer/xml/student.xsd");
        Schema schema;
        try {
            schema = factory.newSchema(schemaLocation);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        XMLInputFactory inputFactory =
                XMLInputFactory.newInstance();
        eventFactory = XMLEventFactory.newInstance();
        Validator validator = schema.newValidator();
        Source source = new StreamSource(FILE_NAME);
        try {
            validator.validate(source);
            reader =
                    inputFactory.createXMLStreamReader(source);
            eventReader = inputFactory.createXMLEventReader(source);
            System.out.println("XMLDataAccessObject Created");
            return true;
        } catch (SAXException | IOException | XMLStreamException e) {
            System.out.println("XML file is not valid.");
            return false;
        }
    }

    @Override
    public boolean add(Student student) throws RemoteException {
        List<Student> students = getAllStudents();
        if (students == null || students.contains(student)) {
            return false;
        }
        try {
            XMLOutputFactory outputFactory =
                    XMLOutputFactory.newInstance();
            writer = outputFactory.createXMLEventWriter(new FileWriter(FILE_NAME));
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                switch (event.getEventType()) {
                    case XMLEvent.END_ELEMENT:
                        EndElement endElement = event.asEndElement();
                        String tagName = endElement.getName().getLocalPart();
                        switch (tagName) {
                            case "students":
                                writeStudent(student);
                                writeSimpleTag("students", false);
                                return true;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                writer.add(event);
            }
            return false;
        } catch (XMLStreamException | ParseException | IOException e) {
            return false;
        } finally {
            if (writer != null) {
                try {
                    writeSimpleTag("students", false);
                    writer.close();
                } catch (XMLStreamException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    @Override
    public Student delete(String name, int group) throws RemoteException {
        if (!connect()){
            return null;
        }
        Student student = null;
        List <Student> students = getAllStudents();
        int index = students.indexOf(new Student(name, group, new ArrayList<>()));
        if (index >= 0){
            student = students.remove(index);
            try {
                FileWriter fileWriter = new FileWriter(new File(FILE_NAME));
                fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><stud:students xmlns:stud=\"http://www.example.com/student\"> \n");
                fileWriter.close();
                XMLOutputFactory outputFactory =
                        XMLOutputFactory.newInstance();
                writer = outputFactory.createXMLEventWriter(new FileWriter(FILE_NAME, true));
                for (Student st: students){
                    writeStudent(st);
                    writeSimpleTag("student", false);
                }
                writer.close();
                fileWriter = new FileWriter(FILE_NAME, true);
                fileWriter.write("</stud:students>");
                fileWriter.close();
            } catch (IOException | XMLStreamException | ParseException e) {
                return null;
            }
        }
        return student;
    }

    @Override
    public boolean deleteAll() throws RemoteException {
        if(!connect()) {
            return false;
        }
        try {
            FileWriter fileWriter = new FileWriter(new File(FILE_NAME));
            fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><stud:students xmlns:stud=\"http://www.example.com/student\">");
            fileWriter.write("</stud:students>");
            fileWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        if (!connect()){
            System.out.println("here");
            return null;
        }
        String name;
        List<Student> students = new ArrayList<>();
        Student student;
        List<Exam> exams = null;
        Exam exam;
        String subject = null;
        String lastName = null;
        int group = 0;
        double mark = 0;
        try {
            while (reader.hasNext()) {
    // определение типа "прочтённого" элемента (тега)
                int type = reader.next();
                switch (type) {
                    case XMLStreamConstants.START_ELEMENT:
                        name = reader.getLocalName();
                        switch (name) {
                            case "student":

                                break;
                            case "last-name":
                                lastName = reader.getElementText();
                                break;
                            case "group":
                                group = Integer.valueOf(reader.getElementText());
                                break;
                            case "exams":
                                exams = new ArrayList<>();
                                break;
                            case "exam":

                                break;
                            case "mark":
                                mark = Double.valueOf(reader.getElementText());
                                break;
                            case "subject":
                                subject = reader.getElementText();
                                break;
                            default:

                                break;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        name = reader.getLocalName();
                        switch (name) {
                            case "student":
                                student = new Student(lastName, group, exams);
                                students.add(student);
                                break;
                            case "exam":
                                exam = new Exam(mark, subject);
                                exams.add(exam);
                                break;
                            default:

                                break;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        //writeText(reader.getText());
                        break;
                        default:
                        break;
                }
            }
        } catch (XMLStreamException e) {
            return null;
        }
        return students;
    }

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

    private void writeStudent(Student student) throws XMLStreamException, ParseException {
        XMLEvent newLineSequence = eventFactory.createCharacters("\n");
        XMLEvent tabSequence = eventFactory.createCharacters("\t");
        writer.add(tabSequence);
        writeSimpleTag("student", true);
        writer.add(newLineSequence);
        writeSimpleElement("last-name", student.getLastName());
        writer.add(newLineSequence);
        writeSimpleElement("group", String.valueOf(student.getGroup()));
        writer.add(newLineSequence);
        writeSimpleTag("exams", true);
        writer.add(newLineSequence);
        List<Exam> exams = student.getExams();
        for (Exam exam: exams){
            writeSimpleTag("exam", true);
            writer.add(newLineSequence);
            writeSimpleElement("mark", String.valueOf(exam.getMark()));
            writer.add(newLineSequence);
            writeSimpleElement("subject", exam.getSubject());
            writer.add(newLineSequence);
            writeSimpleTag("exam", false);
            writer.add(newLineSequence);
        }
        writeSimpleTag("student", false);
        writer.add(newLineSequence);
    }

    private void writeSimpleTag(String tagName, boolean isOpen) throws XMLStreamException {
        XMLEvent event;
        if (isOpen) {
            QName name = QName.valueOf(tagName);
            event = eventFactory.createStartElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart());
        } else {
            QName name = QName.valueOf(tagName);
            event = eventFactory.createEndElement(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart());
        }
        writer.add(event);
    }

    private void writeSimpleElement(String tagName, String value) throws XMLStreamException {
        XMLEvent controlSeq1 = eventFactory.createCharacters("\t\t");
        writer.add(controlSeq1);
        writeSimpleTag(tagName, true);
        XMLEvent event = eventFactory.createCharacters(value);
        writer.add(event);
        writeSimpleTag(tagName, false);
    }
}
