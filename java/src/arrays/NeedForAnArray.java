package arrays.a_basics;

public class NeedForAnArray {
    public static void findAverageOfStudentsMarks(){
        float s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;
        s1=45;
        s2=67;
        s3=64;
        s4=67;
        s5=56;
        s6=79;
        s7=80;
        s8=76;
        s9=67;
        s10=45;
        float total=s1+s2+s3+s4+s5+s6+s7+s8+s9+s10;
        float average=total/10;
        System.out.println(average);
    }
    public static void findAverageUsingArray(){
        float[] array;
        array=new float[]{45,67,64,67,56,79,80,76,67,45};
        float total=0;
        for(int i=0;i<array.length;i++){
            total=total+array[i];
        }
        float average=total/array.length;
        System.out.println(average);
    }

    public static  void main(String[] args){
        findAverageOfStudentsMarks();
        findAverageUsingArray();
    }

}
