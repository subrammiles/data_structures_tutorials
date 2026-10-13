"""Why arrays? Ten separate variables vs one list.

With separate variables every new student means a new variable and a longer formula.
With an array (a Python list) one loop handles any number of students.
"""


def average_without_array():
    s1, s2, s3, s4, s5, s6, s7, s8, s9, s10 = 45, 67, 64, 67, 56, 79, 80, 76, 67, 45
    total = s1 + s2 + s3 + s4 + s5 + s6 + s7 + s8 + s9 + s10
    return total / 10


def average_with_array(marks):
    total = 0
    for m in marks:
        total += m
    return total / len(marks)


if __name__ == "__main__":
    marks = [45, 67, 64, 67, 56, 79, 80, 76, 67, 45]
    assert average_without_array() == average_with_array(marks) == 64.6
    print("average:", average_with_array(marks))
    print("all checks passed")
