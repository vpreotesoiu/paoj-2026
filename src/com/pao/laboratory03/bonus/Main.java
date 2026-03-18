package com.pao.laboratory03.bonus;

import com.pao.laboratory03.bonus.exception.InvalidTransitionException;
import com.pao.laboratory03.bonus.model.Priority;
import com.pao.laboratory03.bonus.model.Status;
import com.pao.laboratory03.bonus.model.Task;
import com.pao.laboratory03.bonus.service.TaskService;

import java.util.List;
import java.util.Map;

/**
 * Exercițiul 5 (Bonus) — Sistem de gestiune task-uri cu audit log
 *
 * Construiește un sistem complet FĂRĂ schelet de cod.
 * Primești doar cerințele — tu decizi structura claselor.
 *
 * ═══════════════════════════════════════════════════════
 *  CERINȚE FUNCȚIONALE
 * ═══════════════════════════════════════════════════════
 *
 * Sistemul gestionează task-uri. Fiecare task are:
 *   - String id (unic, format "T001", "T002", ...)
 *   - String title
 *   - Status status (enum: TODO, IN_PROGRESS, DONE, CANCELLED)
 *   - Priority priority (enum: LOW, MEDIUM, HIGH, CRITICAL)
 *   - String assignee (persoana responsabilă, poate fi null)
 *
 * ═══════════════════════════════════════════════════════
 *  ENUM-URI (creează 2 enum-uri)
 * ═══════════════════════════════════════════════════════
 *
 * 1. Status — cu metoda abstractă boolean canTransitionTo(Status next):
 *    - TODO → poate trece la IN_PROGRESS sau CANCELLED
 *    - IN_PROGRESS → poate trece la DONE sau CANCELLED
 *    - DONE → nu poate trece nicăieri
 *    - CANCELLED → nu poate trece nicăieri
 *
 * 2. Priority — cu câmpuri (int level, double multiplier):
 *    - LOW(1, 1.0), MEDIUM(2, 1.5), HIGH(3, 2.0), CRITICAL(4, 3.0)
 *    - Metodă: double calculateScore(int baseDays)
 *      → returnează baseDays * multiplier (scor de urgență)
 *
 * ═══════════════════════════════════════════════════════
 *  EXCEPȚII (creează 3 excepții custom)
 * ═══════════════════════════════════════════════════════
 *
 * 1. DuplicateTaskException extends RuntimeException
 * 2. TaskNotFoundException extends RuntimeException
 * 3. InvalidTransitionException extends RuntimeException
 *    - Câmp extra: Status fromStatus, Status toStatus
 *    - getMessage() → "Nu se poate trece din IN_PROGRESS în TODO"
 *
 * ═══════════════════════════════════════════════════════
 *  SERVICIU — TaskService (Singleton)
 * ═══════════════════════════════════════════════════════
 *
 * Structuri de date interne:
 *   - Map<String, Task> tasksById — toate task-urile, indexate după id
 *   - Map<Priority, List<Task>> tasksByPriority — grupate pe prioritate
 *   - List<String> auditLog — jurnal cu TOATE operațiile efectuate
 *
 * Metode:
 *   a) Task addTask(String title, Priority priority)
 *      → generează id automat ("T001", "T002", ...)
 *      → adaugă în ambele map-uri
 *      → logează: "[ADD] T001: 'Fix bug' (HIGH)"
 *
 *   b) void assignTask(String taskId, String assignee)
 *      → aruncă TaskNotFoundException dacă id-ul nu există
 *      → logează: "[ASSIGN] T001 → Ana"
 *
 *   c) void changeStatus(String taskId, Status newStatus)
 *      → aruncă TaskNotFoundException dacă id-ul nu există
 *      → aruncă InvalidTransitionException dacă tranziția nu e permisă
 *        (folosește Status.canTransitionTo())
 *      → logează: "[STATUS] T001: TODO → IN_PROGRESS"
 *
 *   d) List<Task> getTasksByPriority(Priority priority)
 *      → returnează lista din map (sau listă goală)
 *
 *   e) Map<Status, Long> getStatusSummary()
 *      → returnează câte task-uri sunt pe fiecare status
 *      → exemplu: {TODO=3, IN_PROGRESS=2, DONE=5, CANCELLED=1}
 *
 *   f) List<Task> getUnassignedTasks()
 *      → task-uri cu assignee == null
 *
 *   g) void printAuditLog()
 *      → afișează toate intrările din jurnal
 *
 *   h) double getTotalUrgencyScore(int baseDays)
 *      → suma scorurilor de urgență ale task-urilor care NU sunt DONE/CANCELLED
 *      → folosește Priority.calculateScore(baseDays)
 *
 * ═══════════════════════════════════════════════════════
 *  MAIN — demonstrează TOATE funcționalitățile
 * ═══════════════════════════════════════════════════════
 *
 * În metoda main:
 * 1. Adaugă minim 5 task-uri cu priorități diferite
 * 2. Asignează 3 task-uri
 * 3. Schimbă status-ul la câteva task-uri (inclusiv o tranziție invalidă în try-catch)
 * 4. Afișează task-uri pe prioritate HIGH
 * 5. Afișează sumarul pe status
 * 6. Afișează task-uri neasignate
 * 7. Calculează scorul de urgență total
 * 8. Afișează audit log-ul complet
 * 9. Încearcă să adaugi un task cu id duplicat → DuplicateTaskException
 * 10. Încearcă să cauți un task inexistent → TaskNotFoundException
 *
 * Output așteptat (orientativ):
 *
 * === Adăugare task-uri ===
 * Adăugat: Task{id='T001', title='Fix login bug', priority=CRITICAL, status=TODO}
 * Adăugat: Task{id='T002', title='Add dark mode', priority=LOW, status=TODO}
 * Adăugat: Task{id='T003', title='Update docs', priority=MEDIUM, status=TODO}
 * Adăugat: Task{id='T004', title='Fix memory leak', priority=HIGH, status=TODO}
 * Adăugat: Task{id='T005', title='Refactor DB layer', priority=HIGH, status=TODO}
 *
 * === Asignare ===
 * T001 → Ana
 * T003 → Mihai
 * T004 → Elena
 *
 * === Schimbări status ===
 * T001: TODO → IN_PROGRESS ✓
 * T001: IN_PROGRESS → DONE ✓
 * T003: TODO → IN_PROGRESS ✓
 * T001: DONE → TODO → InvalidTransitionException: Nu se poate trece din DONE în TODO
 *
 * === Task-uri HIGH ===
 * Task{id='T004', title='Fix memory leak', priority=HIGH, status=TODO, assignee=Elena}
 * Task{id='T005', title='Refactor DB layer', priority=HIGH, status=TODO, assignee=null}
 *
 * === Sumar status ===
 * TODO: 2
 * IN_PROGRESS: 1
 * DONE: 1
 * CANCELLED: 0
 *
 * === Task-uri neasignate ===
 * T002: Add dark mode
 * T005: Refactor DB layer
 *
 * === Scor urgență (baseDays=5) ===
 * Total: 40.0
 *
 * === Audit Log ===
 * [ADD] T001: 'Fix login bug' (CRITICAL)
 * [ADD] T002: 'Add dark mode' (LOW)
 * [ADD] T003: 'Update docs' (MEDIUM)
 * [ADD] T004: 'Fix memory leak' (HIGH)
 * [ADD] T005: 'Refactor DB layer' (HIGH)
 * [ASSIGN] T001 → Ana
 * [ASSIGN] T003 → Mihai
 * [ASSIGN] T004 → Elena
 * [STATUS] T001: TODO → IN_PROGRESS
 * [STATUS] T001: IN_PROGRESS → DONE
 * [STATUS] T003: TODO → IN_PROGRESS
 *
 * === Excepții ===
 * TaskNotFoundException: Task-ul 'T999' nu a fost găsit
 */
public class Main {
    public static void main(String[] args) {
        // TODO: implementează toți cei 10 pași de mai sus
        // Creează TOATE clasele necesare în acest pachet (bonus/)
        // Nu ai subpachete impuse — organizează cum consideri
        TaskService ts = TaskService.getInstance();
        // sa zicem ca este un restaurant
        // adaugare 5 taskuri
        System.out.println("=== Adaugare task-uri===");
        ts.addTask("Pregateste chiflele", Priority.MEDIUM);
        ts.addTask("Cumpara ketchup", Priority.HIGH);
        ts.addTask("Schimba uleiul", Priority.LOW);
        ts.addTask("Repara cuptorul", Priority.CRITICAL);
        ts.addTask("Spala vasele", Priority.MEDIUM);
        // asignare 3 taskuri
        System.out.println("=== Asignare ===");
        ts.assignTask("T0001", "Andrei");
        ts.assignTask("T0002", "George");
        ts.assignTask("T0003", "Maria");
        // schimbare status taskuri
        System.out.println("=== Schimbari status ===");
        ts.changeStatus("T0001", Status.IN_PROGRESS);
        // try catch pt tranzitie invalida
        try {
            ts.changeStatus("T0001", Status.DONE);
        } catch (InvalidTransitionException e) {
            System.out.println("Am prins exceptia de tranzitionare invalida!");
        }
        ts.changeStatus("T0002", Status.IN_PROGRESS);
        ts.changeStatus("T0003", Status.CANCELLED);
        // afiseaza task-urile de prioritate high
        List<Task> tasks = ts.getTasksByPriority(Priority.HIGH);
        System.out.println("=== Task-uri HIGH ===");
        for (Task t : tasks) {
            System.out.println(t);
        }
        // afiseaza sumar pe status
        System.out.println("=== Sumar status ===");
        Map<Status, Long> statusSummary = ts.getStatusSummary();
        for (Map.Entry<Status, Long> elem : statusSummary.entrySet()) {
            Status s = elem.getKey();
            Long v = elem.getValue();
            System.out.println(s.name() + ": " + v);
        }
        // afiseaza task-uri neasignate
        System.out.println("=== Task-uri neasignate ===");
        List<Task> unassignedTasks = ts.getUnassignedTasks();
        for (Task t : unassignedTasks) {
            System.out.println(t.getId() + ": " + t.getTitle());
        }
        // calculeaza scor de urgenta total
        System.out.println("=== Scor urgenta (baseDays=5) ===");
        System.out.println("Total: " + ts.getTotalUrgencyScore(5));
        // audit log
        ts.printAuditLog();
        // trigger la exceptii
        // ts.assignTask("T999", "Gigel");
    }
}


