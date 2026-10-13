
function findAverageOfStudentsMarks() {
    /*Declarying and initializing more studens is time cosuming */
    let s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;
    s1 = 45;
    s2 = 67;
    s3 = 64;
    s4 = 67;
    s5 = 56;
    s6 = 79;
    s7 = 80;
    s8 = 76;
    s9 = 67;
    s10 = 45;
    let total = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10;
    let average = total / 10;
    console.log(average);
}
function findAverageUsingArray() {
    let array = [];
    array = [45, 67, 64, 67, 56, 79, 80, 76, 67, 45];
    let total = 0;
    for (let i = 0; i < array.length; i++) {
        total = total + array[i];
    }
    average = total / array.length;
    console.log(average);
}


findAverageOfStudentsMarks();
findAverageUsingArray();

