package common.structure.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Group implements Serializable {
    private int groupNumber;
    private List<String> students;

    public Group(int groupNumber) {
        this.groupNumber = groupNumber;
        students = new ArrayList<>();
    }
    public boolean addStudent(String lastName){
        if (students.contains(lastName)){
            return false;
        }
        return students.add(lastName);
    }

    @Override
    public String toString() {
        return "Group" + groupNumber +
                "{ students=" + students +
                '}';
    }
}
