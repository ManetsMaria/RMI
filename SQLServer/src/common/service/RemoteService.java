package common.service;

import common.structure.Student;
import common.structure.view.Group;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RemoteService extends Remote {

    boolean add(Student student) throws RemoteException;

    Student delete(String name, int group) throws RemoteException;

    boolean deleteAll() throws RemoteException;

    List<Student> getAllStudents() throws RemoteException;

    List<Group> join() throws RemoteException;
}