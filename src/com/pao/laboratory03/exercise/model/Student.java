package com.pao.laboratory03.exercise.model;

import com.pao.laboratory03.exercise.exception.InvalidGradeException;
import com.pao.laboratory03.exercise.exception.InvalidStudentException;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private String name;
    private int age;
    private Map<Subject, Double> grades;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        this.grades = new HashMap<Subject, Double>();
        if (age < 18 || age > 60) {
            throw new InvalidStudentException("Studentul are o varsta invalida!");
        }
    }

    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public Map<Subject, Double> getGrades() {
        return grades;
    }

    public void addGrade(Subject subject, double grade) {
        if (grade < 1 || grade > 10) {
            throw new InvalidGradeException(" Nota poate fi doar intre 1 si 10!");
        }
        grades.put(subject, grade);
    }

    public double getAverage() {
        double sum = 0;
        for (Map.Entry<Subject, Double> elem : grades.entrySet()) {
            Double val = elem.getValue();
            sum += val;
        }
        return sum / grades.size();
    }

    @Override
    public String toString() {
        return "Student{name='" + name + "', age=" + age + ", avg=" + getAverage() + "}";
    }
}
