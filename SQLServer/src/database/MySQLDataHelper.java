package database;


import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import common.service.RemoteService;
import common.structure.Exam;
import common.structure.Student;
import common.structure.view.Group;
import common.util.GeneratorId;

public class MySQLDataHelper implements RemoteService {
    private Connection con;
    private Statement stmt;


    public boolean add(Student student)throws RemoteException{
        if (!connect()) {
            return false;
        }
        try {
            stmt.executeUpdate("INSERT INTO exams.student (lastName, form, studentID) VALUES ('" + student.getLastName() + "'," + student.getGroup() + "," + student.getId() + ")");
            List<Exam> exams = student.getExams();
            for (int i = 0; i < exams.size(); i++) {
                try {
                    stmt.executeUpdate("INSERT INTO exams.subject (subjectName) VALUES ('" + exams.get(i).getSubject() + "')", Statement.RETURN_GENERATED_KEYS);
                } catch (MySQLIntegrityConstraintViolationException e) {
                    continue;
                }
            }
            for (int i = 0; i < exams.size(); i++) {
                try {
                    ResultSet rs = stmt.executeQuery("SELECT subjectID FROM exams.subject WHERE subjectName = '" + exams.get(i).getSubject() + "'");
                    if (rs.next()) {
                        int subjectId = rs.getInt("subjectID");
                        stmt.executeUpdate("INSERT INTO exams.marks(subjectID, studentID, mark) VALUES (" + subjectId + "," + student.getId() + "," + exams.get(i).getMark() + ")");
                    }
                } catch (MySQLIntegrityConstraintViolationException e) {
                    continue;
                }
            }
            return true;
        }catch (MySQLIntegrityConstraintViolationException e){
            return false;
        } catch (SQLException e) {
            return false;
        } catch (Exception e){
            return false;
        }finally {
            close();
        }
    }

    public boolean deleteAll()throws RemoteException{
        if (!connect()){
            return false;
        }
        try {
            stmt.executeUpdate("DELETE FROM exams.marks");
            stmt.executeUpdate("DELETE FROM exams.student");
            stmt.executeUpdate("DELETE FROM exams.subject");
        } catch (SQLException e) {
            return false;
        } finally {
            close();
        }
        return true;
    }

    public List<Group> join()throws RemoteException{
        List<Group> students = new ArrayList<>();
        if (!connect()){
            return null;
        }
        String query = "SELECT * FROM exams.marks LEFT JOIN  exams.student ON exams.marks.studentID = exams.student.studentID WHERE exams.marks.mark<4 ORDER BY form, lastName";
        int previousGroup = -1;
        Group currentGroup = null;
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                String lastName = rs.getString("lastName");
                int group = rs.getInt("form");
                if (previousGroup != group) {
                    currentGroup = new Group(group);
                    students.add(currentGroup);
                    previousGroup = group;
                }
                currentGroup.addStudent(lastName);
            }
        } catch (SQLException e) {
            return null;
        }
        return students;
    }

    public List<Student> getAllStudents()throws RemoteException{
        if (!connect()){
            return null;
        }
        List<Student> students = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM exams.student");
            while(rs.next()){
                String name = rs.getString("lastName");
                int group = rs.getInt("form");
                int id = rs.getInt("studentID");
                List<Exam> exams = new ArrayList<>();
                Statement marksStatement = con.createStatement();
                ResultSet marks = marksStatement.executeQuery("SELECT * FROM exams.marks WHERE studentID="+id);

                while (marks.next()){
                    double mark = marks.getDouble("mark");
                    int subjectId = marks.getInt("subjectID");
                    String subject = null;
                    Statement subjectStatment = con.createStatement();
                    ResultSet subjectName = subjectStatment.executeQuery("SELECT * FROM exams.subject WHERE subjectID="+subjectId);
                    if (subjectName.next()){
                       subject = subjectName.getString("subjectName");
                    }
                    subjectStatment.close();
                    Exam exam = new Exam(mark, subject);
                    exams.add(exam);
                }
                marksStatement.close();
                Student student = new Student(name, group, exams);
                students.add(student);
            }
        } catch (SQLException e) {
        }finally {
            close();
        }
        return students;
    }

    private boolean connect(){
        try {
            String url = MySQLData.URL;
            String user = MySQLData.USER;
            String password = MySQLData.PASSWORD;
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            return true;
        } catch (SQLException sqlEx) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    private void close(){
        try {
            con.close();
            stmt.close();
        } catch (SQLException e) {

        }
    }

    public Student delete(String name, int group) throws RemoteException{
        if (!connect()){
            return null;
        }
        try {
            ResultSet students = stmt.executeQuery("SELECT * FROM exams.student WHERE lastName='"+name+"' AND form="+group);
            if (students.next()){
                int id = students.getInt("studentID");
                stmt.executeUpdate("DELETE FROM exams.marks WHERE studentID="+id);
                stmt.executeUpdate("DELETE FROM exams.student WHERE studentID="+id);
                Student student = new Student(name, group, new ArrayList<>());
                return student;
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
