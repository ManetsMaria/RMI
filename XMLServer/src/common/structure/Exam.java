package common.structure;

import java.io.Serializable;

public class Exam implements Serializable {
    private double mark;
    private String subject;

    public Exam(){

    }

    public Exam(double mark, String subject) {
        this.mark = mark;
        this.subject = subject;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "mark=" + mark +
                ", subject='" + subject + '\'' +
                '}';
    }
}
