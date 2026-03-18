package com.pao.laboratory03.exercise.service;

import com.pao.laboratory03.exercise.exception.StudentNotFoundException;
import com.pao.laboratory03.exercise.model.Student;
import com.pao.laboratory03.exercise.model.Subject;

import java.util.*;

public class StudentService {
    private List<Student> students;
    private StudentService() {
        this.students = new ArrayList<Student>();
    }

    private static class Holder {
        private static final StudentService INSTANCE = new StudentService();
    }

    public static StudentService getInstance() {
        return StudentService.Holder.INSTANCE;
    }

    public void addStudent(String name, int age) {
        Student s = new Student(name, age);
        for (Student student : students) {
            if (student.getName().equals(name)) {
                throw new RuntimeException("Deja exista in sistem un student cu acelasi nume!");
            }
        }
        students.add(s);
    }
    public Student findByName(String name) {
        for (Student s : students) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        throw new StudentNotFoundException("Studentul cu numele " + name + " nu a fost gasit in sistem!");
    }
    public void addGrade(String studentName, Subject subject, double grade) {
        Student s = findByName(studentName);
        s.addGrade(subject, grade);
    }
    public void printAllStudents() {
        System.out.println("Situatia studentilor:");
        for (Student s : students) {
            System.out.println("Studentul " + s.getName() + "are urmatoarele note:");
            Map<Subject,Double> grades = s.getGrades();
            for (Map.Entry<Subject, Double> elem : grades.entrySet()) {
                Subject materie = elem.getKey();
                Double nota = elem.getValue();
                System.out.println("Materia " + materie.getFullName() + ": " + nota);
            }
        }
    }
    public void printTopStudents() {
        System.out.println("Performantele studentilor in ordine descrescatoare:");
        students.sort((a, b) -> Double.compare(b.getAverage(), a.getAverage()));
        for (Student s: students) {
            System.out.println("Studentul " + s.getName() + " are media " + s.getAverage());
        }
    }
    public Map<Subject, Double> getAveragePerSubject() {
        Map<Subject, Integer> gradeAmount = new HashMap<Subject, Integer>();
        Map<Subject, Double> averages = new HashMap<Subject, Double>();
        for (Student s: students) {
            Map<Subject, Double> grades = s.getGrades();
            for (Map.Entry<Subject, Double> elem: grades.entrySet()) {
                Subject materie = elem.getKey();
                Double nota = elem.getValue();
                if (!gradeAmount.containsKey(materie)) {
                    gradeAmount.put(materie, 0);
                }
                gradeAmount.put(materie, gradeAmount.get(materie) + 1);
                if (!averages.containsKey(materie)) {
                    averages.put(materie, (double) 0);
                }
                averages.put(materie, averages.get(materie) + nota);
            }
        }
        for (Map.Entry<Subject, Double> elem : averages.entrySet()) {
            Subject materie = elem.getKey();
            Double average = elem.getValue() / (double) gradeAmount.get(materie);
            averages.put(materie, average);
        }
        return averages;
    }
}
