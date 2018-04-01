package sample;

import common.service.RemoteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import common.structure.Exam;
import common.structure.Student;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Класс для обработки и отображения информации, полученной от сервера</p>
 *
 * выполнен средствами <tt><a href="https://ru.wikipedia.org/wiki/JavaFX">JavaFX</a></tt>
 * @author Manets Mariya
 */
public class Controller {


    private RemoteService service;

    private ObservableList<Student> studentsData = FXCollections.observableArrayList();

    @FXML
    private TableView<Student> tableStudents;

    @FXML
    private TableColumn<Student, String> lastNameColumn;

    @FXML
    private TableColumn<Student, Integer> groupNumberColumn;

    @FXML
    private TableColumn<Student, List<Exam>> marksColumn;

    @FXML
    private void initialize() {
        serverConnect();
        initData();

        groupNumberColumn.setCellValueFactory(new PropertyValueFactory<Student, Integer>("group"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Student, String>("lastName"));
        marksColumn.setCellValueFactory(new PropertyValueFactory<Student, List<Exam> >("exams"));

        tableStudents.setItems(studentsData);
    }

    private void initData() {
        try {
            List<Student> students = service.getAllStudents();
            if(students != null) {
                for (Student student: students){
                    studentsData.add(student);
                }
            }
        } catch (RemoteException e) {
            show("remote exception", "init data");
        }
    }

    /**
     * Метод для обработки нажатия на кнопку для добавления студента
     *
     * @see FXML
     */
    @FXML
    public void addStudent(){
        Dialog<String> dialog = new TextInputDialog("name group subject mark...");
        dialog.setTitle("Add student");
        Optional<String> optional = dialog.showAndWait();
        if (!optional.isPresent()){
            show("Close", "don't add anything");
            return;
        }
        String studentString = optional.get();
        String[] studentArray = studentString.split(" ");
        try {
            String lastName = studentArray[0];
            int group = Integer.parseInt(studentArray[1]);
            List<Exam> exams = new ArrayList<>();
            for (int i = 2; i< studentArray.length; i+=2){
                String subject = studentArray[i];
                double mark = Double.parseDouble(studentArray[i+1]);
                Exam exam = new Exam(mark, subject);
                exams.add(exam);
            }
            if (!exams.isEmpty()){
                Student student = new Student(lastName, group, exams);
                if (service.add(student)){
                    studentsData.add(student);
                    show("Success","Added");
                }else{
                    show("Error","still have this student");
                }
            }else{
                    show("Error","not enough information about exams");
            }
        }catch (NumberFormatException e){
            show("Error","Incorrect data");
        }catch (IndexOutOfBoundsException e){
            show("Error","Incorrect number of args");
        } catch (RemoteException e) {
            show("Error","Remote problems");
        }
    }

    /**
     * Метод для обработки нажатия на кнопку для удаления студентов
     *
     * @see FXML
     */
    @FXML
    public void deleteStudent(){
        try {
            if (service.deleteAll()){
                studentsData.remove(0, studentsData.size());
                show("Success","Deleted");
            }else{
                show("Error","connection problems");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для обработки нажатия на кнопку для выборки студентов
     *
     * @see FXML
     */
    @FXML
    public void joinStudent(){
        try {
            show("specific student join:", service.join().toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для обработки нажатия на кнопку для добавления студента
     *
     * @see FXML
     */
    @FXML
    public void deleteOneStudent(){
        Dialog<String> dialog = new TextInputDialog("name group");
        dialog.setTitle("Delete student");
        Optional<String> optional = dialog.showAndWait();
        if (!optional.isPresent()){
            show("Close", "don't enter anything");
            return;
        }
        String studentString = optional.get();
        String[] studentArray = studentString.split(" ");
        try {
            String lastName = studentArray[0];
            int group = Integer.parseInt(studentArray[1]);
            Student student = service.delete(lastName, group);
            if (student != null){
                studentsData.remove(student);
                show("Success","Delete");
            }else{
                show("Error","don't have this student");
            }
        }catch (NumberFormatException e){
            show("Error","Incorrect data");
        }catch (IndexOutOfBoundsException e){
            show("Error","Incorrect number of args");
        } catch (RemoteException e) {
            show("Error","Remote problems");
        }
    }

    private void show(String header, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("student information");
        alert.setContentText(message);
        alert.setHeaderText(header);
        alert.setResizable(true);
        alert.getDialogPane().setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        alert.show();
    }

    private void serverConnect(){
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry("localhost", 4396);
        } catch (RemoteException e) {
            show("remote exception", "connection");
        }
        try {
            assert registry != null;
            service = (RemoteService) registry.lookup("StudentService");
        } catch (RemoteException | NotBoundException e) {
            show("remote exception", "connection");
            System.exit(0);
        }
    }

}
