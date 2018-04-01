package common.service;

import common.structure.Student;
import common.structure.view.Group;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * <p>Интерфейс <b>удаленного сервиса</b>, для реализации
 * вызова методов с помощью библиотеки <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
 *
 * @see Remote
 */
public interface RemoteService extends Remote {

    /**
     * Метод для добавления студента
     *
     * @param student - добавляемый студент
     * @return был ли добавлен студент
     * @throws RemoteException исключение <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
     */
    boolean add(Student student) throws RemoteException;

    /**
     * Метод для удаления студента
     *
     * @param name - имя студента
     * @param group - группа студента
     * @return student - удалённого студента (null, если удаление невозможно)
     * @throws RemoteException исключение <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
     */
    Student delete(String name, int group) throws RemoteException;

    /**
     * Метод для удаления всех студентов
     *
     * @return произошло ли удаление
     * @throws RemoteException исключение <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
     */
    boolean deleteAll() throws RemoteException;

    /**
     * Метод для получения списка студентов
     *
     * @return список студентов, находящихся на данный момент в хранилище
     * @throws RemoteException исключение <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
     */
    List<Student> getAllStudents() throws RemoteException;

    /**
     * Метод для выборки студентов, не сдавших сессию, с сортировкой по группам и фамилиям
     *
     * @return список студентов, удолятворяющих требованию
     * @throws RemoteException исключение <tt><a href="https://ru.wikipedia.org/wiki/RMI">RMI</a></tt>
     */
    List<Group> join() throws RemoteException;
}