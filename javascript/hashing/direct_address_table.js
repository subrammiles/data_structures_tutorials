/*
Problem:Create a data structure which give the  student's full name ,marks on 
entering the roll number.
*/

/*
Note:In JS standard arrays  aren't really arrays at all , they're just 
objects backed by Array.prototype.
They aren't contiguous blocks of memory as in some other languages.
For understanding purpose assume them like a contiguous blocks of memory. 
*/
function Student(roll_number, full_name, total_marks) {
  this.full_name = full_name;
  this.total_marks = total_marks;
  this.roll_number = roll_number;
}


function insert(students, student) {
  /*We are using Student roll number as an index*/
  students[student.roll_number] = student;
  /* O(n) is n for insert operation*/
  /*We need to travel to the end of an array to insert*/
}

function update(students, student) {
  students[student.roll_number] = student;
  /*O(n) */
}

function deleteStudent(students, student) {
  students[student.roll_number] = null;
  /*O(n) */
}

function access(students, roll_number) {
  return students[roll_number];
  /*O(1) because we know the index .So we no nedd to
  travel the array.We can return directly.
  */
}

function search(students, roll_number) {
  return students[roll_number];
}

function main() {
  let students = new Array(); 
  //Or you can use let students=[]
  /*We are Passing the reference of students to insert */
  /*Arrays are called by refrence */
  insert(students, new Student(1, "Subbu", 78));
  insert(students, new Student(2, "Ravi", 95));
  insert(students, new Student(3, "Trump", 87));
  insert(students, new Student(4, "Ivanka Trump", 78));
  insert(students, new Student(5, "Putin", 68));
  console.log(students);
}

main();


/*
Problem with this solution:
1.
If 100 students are there,we need to allocate the memory for 
100 students in the array
2.Let say one student is deleted at 51st position,still that is allocated.So
this memory is wasted.


*/