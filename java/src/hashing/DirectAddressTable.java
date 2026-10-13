package hashing.a_basics.dat;

public class DAT {


    static void insert(Student[] students,Student student){
         /*We are using Student roll number as an index*/
         students[student.roll_number]=student;
    }

    static void update(Student[] students,Student student){
        students[student.roll_number]=student;
    }

    static void delete(Student[] students,Student student){
        students[student.roll_number]=null;
    }

    static Student search(Student[] students,int roll_number){
        return students[roll_number];
    }

    public  static  void main(String[] args){
        Student[] students=new Student[5+1];
        insert(students,new Student(1,"Subbu",78));
        insert(students,new Student(2,"Ravi",95));
        insert(students,new Student(3,"Trump",87));
        insert(students,new Student(4,"Ivanka Trump",78));
        insert(students,new Student(5,"Putin",68));
    }
}
