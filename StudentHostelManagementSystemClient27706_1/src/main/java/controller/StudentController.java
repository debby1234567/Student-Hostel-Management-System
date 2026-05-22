package controller;

import java.rmi.RemoteException;
import model.Student;
import rmi.ServiceLocator;
import java.util.List;

public class StudentController {

    public boolean addStudent(Student student) {
        try { return ServiceLocator.getHostelService().addStudent(student); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public boolean updateStudent(Student student) {
        try { return ServiceLocator.getHostelService().updateStudent(student); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public boolean deleteStudent(Long id) {
        try { return ServiceLocator.getHostelService().deleteStudent(id); }
        catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public List<Student> getAllStudents() throws Exception {
        try { return ServiceLocator.getHostelService().getAllStudents(); }
        catch (RemoteException e) { throw new RuntimeException(e.getMessage()); }
    }
}
