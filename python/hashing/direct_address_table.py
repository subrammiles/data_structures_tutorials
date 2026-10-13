"""Direct address table: use the key itself as the array index.

Every operation is O(1), but it only works when keys are small non-negative integers, and the
array must be as large as the biggest key - wasteful when keys are sparse (roll numbers 1 and
1,000,000). Hash tables (next lessons) fix this by mapping any key into a small array.
"""


class Student:
    def __init__(self, roll_number, full_name, total_marks):
        self.roll_number = roll_number
        self.full_name = full_name
        self.total_marks = total_marks


def insert(table, student):
    table[student.roll_number] = student  # roll number IS the index


def search(table, roll_number):
    return table[roll_number]


def delete(table, roll_number):
    table[roll_number] = None


if __name__ == "__main__":
    students = [None] * (5 + 1)
    insert(students, Student(1, "Subbu", 78))
    insert(students, Student(2, "Ravi", 95))
    insert(students, Student(3, "Asha", 87))
    assert search(students, 2).full_name == "Ravi"
    delete(students, 2)
    assert search(students, 2) is None
    assert search(students, 4) is None
    print("all checks passed")
