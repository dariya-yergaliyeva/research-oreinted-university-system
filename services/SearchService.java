package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import research.ResearchJournal;
import research.ResearchPaper;
import university.University;
import users.Student;
import users.Teacher;
import users.User;

public class SearchService {

    private static Pattern buildPattern(String regex) {
        if (regex == null || regex.trim().isEmpty()) {
            throw new IllegalArgumentException("Regex must not be empty");
        }

        try {
            return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex: " + regex + " | " + e.getMessage());
        }
    }

    private static boolean matches(Pattern pattern, String text) {
        return text != null && pattern.matcher(text).find();
    }

    private static boolean matchesAny(Pattern pattern, String... fields) {
        for (String field : fields) {
            if (matches(pattern, field)) {
                return true;
            }
        }
        return false;
    }

    public List<Student> searchStudents(University university, String regex) {
        Pattern pattern = buildPattern(regex);
        List<Student> result = new ArrayList<>();

        for (User user : university.getUsers()) {
            if (user instanceof Student) {
                Student student = (Student) user;
                String fullName = student.getFirstName() + " " + student.getLastName();

                if (matchesAny(pattern,
                        student.getId(),
                        student.getStudentId(),
                        student.getFirstName(),
                        student.getLastName(),
                        fullName,
                        student.getEmail(),
                        String.valueOf(student.getGpa()),
                        String.valueOf(student.getCredits()))) {
                    result.add(student);
                }
            }
        }

        return result;
    }

    public List<Teacher> searchTeachers(University university, String regex) {
        Pattern pattern = buildPattern(regex);
        List<Teacher> result = new ArrayList<>();

        for (User user : university.getUsers()) {
            if (user instanceof Teacher) {
                Teacher teacher = (Teacher) user;
                String fullName = teacher.getFirstName() + " " + teacher.getLastName();

                if (matchesAny(pattern,
                        teacher.getId(),
                        teacher.getEmployeeId(),
                        teacher.getFirstName(),
                        teacher.getLastName(),
                        fullName,
                        teacher.getEmail(),
                        teacher.getDepartment(),
                        String.valueOf(teacher.getPosition()),
                        String.valueOf(teacher.getRating()))) {
                    result.add(teacher);
                }
            }
        }

        return result;
    }

    public List<ResearchPaper> searchPapers(University university, String regex) {
        Pattern pattern = buildPattern(regex);
        Set<ResearchPaper> result = new LinkedHashSet<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (ResearchJournal journal : university.getJournals()) {
            for (ResearchPaper paper : journal.getPapers()) {
                if (matchesPaper(pattern, paper, dateFormat)) {
                    result.add(paper);
                }
            }
        }

        for (User user : university.getUsers()) {
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getResearcherRole() != null) {
                    for (ResearchPaper paper : student.getResearcherRole().getResearchPapers()) {
                        if (matchesPaper(pattern, paper, dateFormat)) {
                            result.add(paper);
                        }
                    }
                }
            }

            if (user instanceof Teacher) {
                Teacher teacher = (Teacher) user;
                if (teacher.getResearcherRole() != null) {
                    for (ResearchPaper paper : teacher.getResearcherRole().getResearchPapers()) {
                        if (matchesPaper(pattern, paper, dateFormat)) {
                            result.add(paper);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }

    private boolean matchesPaper(Pattern pattern, ResearchPaper paper, SimpleDateFormat dateFormat) {
        String authors = paper.getAuthors() == null ? "" : String.join(", ", paper.getAuthors());
        String date = paper.getDate() == null ? "" : dateFormat.format(paper.getDate());

        return matchesAny(pattern,
                paper.getTitle(),
                authors,
                paper.getJournal(),
                paper.getDoi(),
                date,
                String.valueOf(paper.getPages()),
                String.valueOf(paper.getCitations()));
    }

    public List<User> searchUsers(University university, String regex) {
        Pattern pattern = buildPattern(regex);
        List<User> result = new ArrayList<>();

        for (User user : university.getUsers()) {
            String fullName = user.getFirstName() + " " + user.getLastName();

            if (matchesAny(pattern,
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    fullName,
                    user.getEmail(),
                    user.getClass().getSimpleName())) {
                result.add(user);
            }
        }

        return result;
    }

    public void printStudents(List<Student> students) {
        System.out.println("=== STUDENT SEARCH RESULT: " + students.size() + " ===");
        for (Student student : students) {
            System.out.println(student.getStudentId() + " | "
                    + student.getFirstName() + " " + student.getLastName()
                    + " | email: " + student.getEmail()
                    + " | GPA: " + student.getGpa()
                    + " | credits: " + student.getCredits());
        }
    }

    public void printTeachers(List<Teacher> teachers) {
        System.out.println("=== TEACHER SEARCH RESULT: " + teachers.size() + " ===");
        for (Teacher teacher : teachers) {
            System.out.println(teacher.getEmployeeId() + " | "
                    + teacher.getFirstName() + " " + teacher.getLastName()
                    + " | email: " + teacher.getEmail()
                    + " | department: " + teacher.getDepartment()
                    + " | position: " + teacher.getPosition()
                    + " | rating: " + teacher.getRating());
        }
    }

    public void printPapers(List<ResearchPaper> papers) {
        System.out.println("=== PAPER SEARCH RESULT: " + papers.size() + " ===");
        for (ResearchPaper paper : papers) {
            System.out.println(paper.getTitle()
                    + " | authors: " + String.join(", ", paper.getAuthors())
                    + " | journal: " + paper.getJournal()
                    + " | DOI: " + paper.getDoi()
                    + " | citations: " + paper.getCitations());
        }
    }
}
