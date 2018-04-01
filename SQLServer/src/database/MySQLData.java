package database;

public class MySQLData {
    public static final String URL = "jdbc:mysql://localhost:3306/student?useSSL=False";
    public static final String USER = "root";
    public static final String PASSWORD = "Alligator.3";
    public static final String CLASS_NAME = "com.mysql.jdbc.Driver";
    public static final String LAST_NAME = "lastName";
    public static final String GROUP = "form";
    public static final String STUDENT_ID = "studentID";
    public static final String MARK = "mark";
    public static final String SUBJECT_ID = "subjectID";
    public static final String SUBJECT_NAME = "subjectName";
    public static final String JOIN_QUERY = "SELECT * FROM exams.marks LEFT JOIN  exams.student ON exams.marks.studentID = exams.student.studentID WHERE exams.marks.mark<4 ORDER BY form, lastName";
    public static final String DELETE_MARK = "DELETE FROM exams.marks";
    public static final String DELETE_STUDENT = "DELETE FROM exams.student";
    public static final String DELETE_SUBJECT = "DELETE FROM exams.subject";
    public static final String INSERT_INTO_STUDENT = "INSERT INTO exams.student (lastName, form, studentID) VALUES ('";
    public static final String INSERT_INTO_SUBJECT = "INSERT INTO exams.subject (subjectName) VALUES ('";
    public static final String INSERT_INTO_MARK = "INSERT INTO exams.marks(subjectID, studentID, mark) VALUES (";
    public static final String SELECT_FROM_EXAM = "SELECT subjectID FROM exams.subject WHERE subjectName = '";
    public static final String SELECT_FROM_STUDENT_BY_ID = "SELECT * FROM exams.marks WHERE studentID=";
    public static final String SELECT_FROM_SUBJECT_BY_ID = "SELECT * FROM exams.subject WHERE subjectID=";
    public static final String SELECT_ALL_STUDENT = "SELECT * FROM exams.student";
    //public static final String studentInsert = "INSERT INTO student.Student (lastName, form, id) VALUES (?, ?, ?)";
}
