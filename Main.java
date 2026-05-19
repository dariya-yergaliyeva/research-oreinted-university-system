import comunication.News;
import courses.Course;
import courses.Mark;
import courses.TechRequest;
import enums.*;
import exception.LowHIndexException;
import research.*;
import university.University;
import users.*;

import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final String DATA_FILE = "university.dat";
    private static University uni;

    public static void main(String[] args) throws Exception {
        Server.start();
        uni = University.loadFromFile(DATA_FILE);
        seedData();


        while (true) {
            System.out.println("\n--- Login (type 'exit' to quit) ---");
            System.out.print("Email: ");
            String email = sc.nextLine().trim();
            if (email.equalsIgnoreCase("exit")) break;

            System.out.print("Password: ");
            String pass = sc.nextLine().trim();

            Optional<User> found = uni.findByEmail(email);
            if (found.isEmpty() || !found.get().login(email, pass)) {
                System.out.println("Invalid credentials.");
                continue;
            }

            User user = found.get();
            System.out.println("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
            uni.log("Login: " + user.getEmail());

            routeUser(user);

            user.logout();
            uni.saveToFile(DATA_FILE);
        }

        System.out.println("Goodbye.");
    }

    private static void routeUser(User user) {
        if (user instanceof Admin a)                      adminMenu(a);
        else if (user instanceof Manager m)               managerMenu(m);
        else if (user instanceof TechSupportSpecialist t) techMenu(t);
        else if (user instanceof Teacher t)               teacherMenu(t);
        else if (user instanceof GraduateStudent gs)      graduateMenu(gs);
        else if (user instanceof Student s)               studentMenu(s);
    }

    // student

    private static void studentMenu(Student student) {
        while (true) {
            System.out.println("\n Student: " + student.getFirstName() + " | GPA: " + student.getGpa() + " | Credits: " + student.getCredits() + "/21 ");
            System.out.println(" 1. View available courses");
            System.out.println(" 2. Register for course");
            System.out.println(" 3. View my marks");
            System.out.println(" 4. View transcript");
            System.out.println(" 5. View attendance");
            System.out.println(" 6. Rate a teacher");
            System.out.println(" 7. View news");
            System.out.println(" 8. Submit tech request");
            System.out.println(" 9. Subscribe to journal");
            if (student.getResearcherRole() != null) {
                System.out.println("10. Research actions");
            }
            System.out.println(" 0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> uni.getCourses().forEach(System.out::println);
                case 2 -> {
                    List<Course> courses = uni.getCourses();
                    printIndexed(courses, c -> c.getName() + " (" + c.getCredits() + " cr, " + c.getCourseType() + ")");
                    System.out.print("Choose: ");
                    int idx = readInt() - 1;
                    if (validIdx(idx, courses)) student.registerCourse(courses.get(idx));
                }
                case 3 -> {
                    List<Mark> marks = student.viewMarks();
                    if (marks.isEmpty()) System.out.println("No marks yet.");
                    else marks.forEach(System.out::println);
                }
                case 4 -> student.getTranscript();
                case 5 -> {
                    var att = student.viewAttendance();
                    System.out.println("Attendance records: " + att.size());
                    att.forEach(System.out::println);
                }
                case 6 -> {
                    List<Teacher> teachers = teacherList();
                    printIndexed(teachers, t -> t.getFirstName() + " " + t.getLastName() + " [" + t.getPosition() + "] rating=" + t.getRating());
                    System.out.print("Choose teacher: ");
                    int ti = readInt() - 1;
                    if (!validIdx(ti, teachers)) break;
                    System.out.print("Rating (1-10): ");
                    int rating = readInt();
                    student.rateTeacher(teachers.get(ti), rating);
                }
                case 7 -> viewNews();
                case 8 -> {
                    System.out.print("Describe the problem: ");
                    String desc = sc.nextLine();
                    TechRequest req = new TechRequest(desc, student);
                    techSpecialistList().stream().findFirst().ifPresentOrElse(
                        t -> t.receiveRequest(req),
                        () -> System.out.println("No tech support available.")
                    );
                }
                case 9 -> subscribeToJournal(student);
                case 10 -> {
                    if (student.getResearcherRole() != null) researchMenu(student.getResearcherRole());
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // graduate student

    private static void graduateMenu(GraduateStudent gs) {
        while (true) {
            System.out.println("\n=== Graduate Student: " + gs.getFirstName() + " [" + gs.getDegreeType() + "] ===");
            System.out.println("1. Student actions");
            System.out.println("2. Research actions");
            System.out.println("3. View supervisor");
            System.out.println("4. View diploma projects");
            System.out.println("5. Add diploma paper");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> studentMenu(gs);
                case 2 -> { if (gs.getResearcherRole() != null) researchMenu(gs.getResearcherRole()); }
                case 3 -> {
                    if (gs.getSupervisor() != null) System.out.println("Supervisor: " + gs.getSupervisor());
                    else System.out.println("No supervisor assigned.");
                }
                case 4 -> {
                    System.out.println("=== Diploma Projects ===");
                    if (gs.getDiplomaProjects().isEmpty()) System.out.println("None.");
                    else gs.getDiplomaProjects().forEach(System.out::println);
                }
                case 5 -> {
                    ResearchPaper p = inputPaper(gs.getFirstName());
                    gs.getResearcherRole().addPaper(p);
                    gs.addDiplomaProject(p);
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // teacher

    private static void teacherMenu(Teacher teacher) {
        while (true) {
            System.out.println("\n=== Teacher: " + teacher.getFirstName() + " [" + teacher.getPosition() + "] | Rating: " + teacher.getRating() + " ===");
            System.out.println("1. View my courses");
            System.out.println("2. View students in a course");
            System.out.println("3. Put mark");
            System.out.println("4. Send complaint about student");
            System.out.println("5. Generate course report");
            System.out.println("6. View news");
            System.out.println("7. Send message to employee");
            if (teacher.getResearcherRole() != null) {
                System.out.println("8. Research actions");
            }
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> {
                    if (teacher.getCourses().isEmpty()) System.out.println("No courses assigned.");
                    else teacher.getCourses().forEach(System.out::println);
                }
                case 2 -> {
                    Course c = selectTeacherCourse(teacher);
                    if (c != null) teacher.viewStudents(c).forEach(Student::displayInfo);
                }
                case 3 -> putMark(teacher);
                case 4 -> sendComplaint(teacher);
                case 5 -> {
                    Course c = selectTeacherCourse(teacher);
                    if (c != null) teacher.generateReport(c);
                }
                case 6 -> viewNews();
                case 7 -> {
                    List<Employee> emps = employeeList();
                    printIndexed(emps, e -> e.getFirstName() + " " + e.getLastName());
                    System.out.print("Choose recipient: ");
                    int ei = readInt() - 1;
                    if (!validIdx(ei, emps)) break;
                    System.out.print("Message: ");
                    String msg = sc.nextLine();
                    teacher.sendMessage(emps.get(ei), msg);
                }
                case 8 -> { if (teacher.getResearcherRole() != null) researchMenu(teacher.getResearcherRole()); }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void putMark(Teacher teacher) {
        Course c = selectTeacherCourse(teacher);
        if (c == null) return;
        List<Student> students = c.getStudents();
        if (students.isEmpty()) { System.out.println("No students enrolled."); return; }
        printIndexed(students, s -> s.getFirstName() + " " + s.getLastName());
        System.out.print("Choose student: ");
        int si = readInt() - 1;
        if (!validIdx(si, students)) return;
        System.out.print("Attestation 1 (0-30): ");
        double a1 = readDouble();
        System.out.print("Attestation 2 (0-30): ");
        double a2 = readDouble();
        System.out.print("Final exam (0-40): ");
        double fe = readDouble();
        Mark mark = new Mark(a1, a2, fe);
        c.addMark(students.get(si), mark);
        teacher.putMark(students.get(si), c, mark);
        System.out.println("Grade: " + mark.getLetterGrade() + " total=" + mark.getTotalMark() );
    }

    private static void sendComplaint(Teacher teacher) {
        List<Student> students = studentList();
        if (students.isEmpty()) { System.out.println("No students found."); return; }
        printIndexed(students, s -> s.getFirstName() + " " + s.getLastName());
        System.out.print("Choose student: ");
        int si = readInt() - 1;
        if (!validIdx(si, students)) return;
        System.out.println("Urgency: 1.LOW  2.MEDIUM  3.HIGH");
        System.out.print("> ");
        int ul = readInt();
        UrgencyLevel level = switch (ul) {
            case 1 -> UrgencyLevel.LOW;
            case 3 -> UrgencyLevel.HIGH;
            default -> UrgencyLevel.MEDIUM;
        };
        teacher.sendComplaint(students.get(si), level);
    }

    // ───────────────────────── ADMIN ─────────────────────────

    private static void adminMenu(Admin admin) {
        while (true) {
            System.out.println("\n=== Admin: " + admin.getFirstName() + " ===");
            System.out.println("1. List all users");
            System.out.println("2. View system logs");
            System.out.println("3. Add new student");
            System.out.println("4. Remove user by email");
            System.out.println("5. Change user language");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> uni.getUsers().forEach(User::displayInfo);
                case 2 -> {
                    admin.viewLogs();
                    System.out.println("--- University logs ---");
                    uni.getLogs().forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("First name: "); String fn = sc.nextLine();
                    System.out.print("Last name: "); String ln = sc.nextLine();
                    System.out.print("Email: "); String em = sc.nextLine();
                    System.out.print("Password: "); String pw = sc.nextLine();
                    System.out.print("Student ID: "); String sid = sc.nextLine();
                    Student s = new Student("u" + System.currentTimeMillis(), fn, ln, em, pw, sid);
                    uni.addUser(s);
                    admin.addUser(s);
                }
                case 4 -> {
                    System.out.print("Email to remove: ");
                    String em = sc.nextLine();
                    uni.findByEmail(em).ifPresentOrElse(u -> {
                        uni.removeUser(u);
                        admin.removeUser(u);
                    }, () -> System.out.println("User not found."));
                }
                case 5 -> {
                    System.out.print("User email: ");
                    String em = sc.nextLine();
                    uni.findByEmail(em).ifPresentOrElse(u -> {
                        System.out.println("Language: 1.EN  2.RU  3.KZ");
                        System.out.print("> ");
                        Language lang = switch (readInt()) {
                            case 2 -> Language.RU;
                            case 3 -> Language.KZ;
                            default -> Language.EN;
                        };
                        u.setLanguage(lang);
                        System.out.println("Language set to " + lang);
                    }, () -> System.out.println("User not found."));
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ───────────────────────── MANAGER ─────────────────────────

    private static void managerMenu(Manager manager) {
        while (true) {
            System.out.println("\n Manager: " + manager.getFirstName() + " " + manager.getManagerType() );
            System.out.println("1. View students by GPA");
            System.out.println("2. View students alphabetically");
            System.out.println("3. Assign teacher to course");
            System.out.println("4. Approve student registration");
            System.out.println("5. Add news");
            System.out.println("6. View all courses");
            System.out.println("7. Top cited researcher");
            System.out.println("8. View news");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> manager.viewStudentSortedByGPA(studentList());
                case 2 -> manager.viewStudentsSortedAlphabetically(studentList());
                case 3 -> {
                    List<Course> courses = uni.getCourses();
                    List<Teacher> teachers = teacherList();
                    if (courses.isEmpty() || teachers.isEmpty()) { System.out.println("No data."); break; }
                    printIndexed(courses, Course::getName);
                    System.out.print("Choose course: ");
                    int ci = readInt() - 1;
                    printIndexed(teachers, t -> t.getFirstName() + " " + t.getLastName());
                    System.out.print("Choose teacher: ");
                    int ti = readInt() - 1;
                    if (validIdx(ci, courses) && validIdx(ti, teachers))
                        manager.assignCourseToTeacher(courses.get(ci), teachers.get(ti));
                }
                case 4 -> {
                    List<Student> studs = studentList();
                    List<Course> courses = uni.getCourses();
                    if (studs.isEmpty() || courses.isEmpty()) { System.out.println("No data."); break; }
                    printIndexed(studs, s -> s.getFirstName() + " " + s.getLastName());
                    System.out.print("Choose student: ");
                    int si = readInt() - 1;
                    printIndexed(courses, c -> c.getName() + " (" + c.getCredits() + " cr)");
                    System.out.print("Choose course: ");
                    int ci = readInt() - 1;
                    if (validIdx(si, studs) && validIdx(ci, courses))
                        manager.approveRegistration(studs.get(si), courses.get(ci));
                }
                case 5 -> {
                    System.out.print("Title: "); String title = sc.nextLine();
                    System.out.print("Content: "); String content = sc.nextLine();
                    System.out.print("Topic (General/Research and etc): "); String topic = sc.nextLine();
                    News n = new News(title, content, topic);
                    uni.addNews(n);
                    manager.manageNews(n);
                }
                case 6 -> uni.getCourses().forEach(System.out::println);
                case 7 -> {
                    var top = uni.getTopCitedResearcher();
                    if (top == null) System.out.println("No researchers found.");
                    else System.out.println("Top researcher: " + top);
                }
                case 8 -> viewNews();
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // tech support

    private static void techMenu(TechSupportSpecialist tech) {
        while (true) {
            System.out.println("\n Tech Support: " + tech.getFirstName() );
            System.out.println("1. View pending requests");
            System.out.println("2. Accept a request");
            System.out.println("3. Reject a request");
            System.out.println("4. Mark request as done");
            System.out.println("0. Logout");
            System.out.print("> ");

            switch (readInt()) {
                case 1 -> {
                    var reqs = tech.viewNewRequests();
                    if (reqs.isEmpty()) System.out.println("No pending requests.");
                    else printIndexed(reqs, Object::toString);
                }
                case 2, 3, 4 -> {
                    var all = tech.getAllRequests();
                    if (all.isEmpty()) { System.out.println("No requests."); break; }
                    printIndexed(all, Object::toString);
                    System.out.print("Choose request: ");
                    int ri = readInt() - 1;
                    if (!validIdx(ri, all)) break;
                    System.out.println("Action: 2.Accept  3.Reject  4.Done");
                    System.out.print("> ");
                    int action = readInt();
                    if (action == 2) tech.acceptRequest(all.get(ri));
                    else if (action == 3) tech.rejectRequest(all.get(ri));
                    else if (action == 4) tech.markDone(all.get(ri));
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // research

    private static void researchMenu(ResearcherRole role) {
        while (true) {
            System.out.println("\n Research: " + role.getUser().getFirstName() + " | h-index: " + role.calculateHIndex() );
            System.out.println("1. Papers by citations");
            System.out.println("2. Papers by date");
            System.out.println("3. Papers by length (pages)");
            System.out.println("4. Add a paper");
            System.out.println("5. Cite a paper (BibTeX / Plain)");
            System.out.println("6. View my projects");
            System.out.println("7. All university researchers (top cited)");
            System.out.println("0. Back");
            System.out.print("> ");

            switch (readInt()) {
                // Strategy pattern: Comparator is plugged in as a strategy
                case 1 -> role.printPapers(Comparator.comparingInt(ResearchPaper::getCitations).reversed());
                case 2 -> role.printPapers(Comparator.comparing(ResearchPaper::getDate).reversed());
                case 3 -> role.printPapers(Comparator.comparingInt(ResearchPaper::getPages).reversed());
                case 4 -> {
                    ResearchPaper p = inputPaper(role.getUser().getFirstName());
                    role.addPaper(p);
                    uni.addNews(new News("New paper: " + p.getTitle(),
                            "Published by " + role.getUser().getFirstName() + " in " + p.getJournal(),
                            "Research"));
                    System.out.println("Announcement posted to news.");
                }
                case 5 -> {
                    List<ResearchPaper> papers = role.getResearchPapers();
                    if (papers.isEmpty()) { System.out.println("No papers."); break; }
                    printIndexed(papers, ResearchPaper::getTitle);
                    System.out.print("Choose paper: ");
                    int pi = readInt() - 1;
                    if (!validIdx(pi, papers)) break;
                    System.out.println("Format: 1.Plain Text  2.BibTeX");
                    System.out.print("> ");
                    CitationFormat fmt = readInt() == 2 ? CitationFormat.BIBTEX : CitationFormat.PLAIN_TEXT;
                    System.out.println(papers.get(pi).getCitation(fmt));
                }
                case 6 -> {
                    var projects = role.getResearchProjects();
                    if (projects.isEmpty()) System.out.println("No projects.");
                    else projects.forEach(System.out::println);
                }
                case 7 -> {
                    var top = uni.getTopCitedResearcher();
                    if (top == null) System.out.println("No researchers.");
                    else System.out.println("Top cited: " + top);
                }
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    //shared helpers

    private static void viewNews() {
        List<News> news = uni.getNewsList();
        if (news.isEmpty()) { System.out.println("No news."); return; }
        System.out.println(" News (pinned first)");
        news.stream()
            .sorted(Comparator.comparing(News::isPinned).reversed())
            .forEach(n -> System.out.printf("[%s] %s (%s)%n",
                n.isPinned() ? "PINNED" : "      ", n.getTitle(), n.getTopic()));
    }

    private static void subscribeToJournal(User user) {
        List<ResearchJournal> journals = uni.getJournals();
        if (journals.isEmpty()) { System.out.println("No journals available."); return; }
        printIndexed(journals, j -> j.getName() + " " + j.getPapers().size() + " papers");
        System.out.print("Choose: ");
        int ji = readInt() - 1;
        if (validIdx(ji, journals)) {
            journals.get(ji).subscribe(user);
        }
    }

    private static ResearchPaper inputPaper(String authorName) {
        System.out.print("Title: "); String title = sc.nextLine();
        System.out.print("Journal: "); String journal = sc.nextLine();
        System.out.print("Pages: "); int pages = readInt();
        System.out.print("DOI: "); String doi = sc.nextLine();
        return new ResearchPaper(title, List.of(authorName), journal, pages, new Date(), doi);
    }

    private static Course selectTeacherCourse(Teacher teacher) {
        List<Course> courses = teacher.getCourses();
        if (courses.isEmpty()) { System.out.println("No courses assigned."); return null; }
        printIndexed(courses, Course::getName);
        System.out.print("Choose course: ");
        int idx = readInt() - 1;
        return validIdx(idx, courses) ? courses.get(idx) : null;
    }

    private static List<Student> studentList() {
        return uni.getUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    private static List<Teacher> teacherList() {
        return uni.getUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .collect(Collectors.toList());
    }

    private static List<Employee> employeeList() {
        return uni.getUsers().stream()
                .filter(u -> u instanceof Employee)
                .map(u -> (Employee) u)
                .collect(Collectors.toList());
    }

    private static List<TechSupportSpecialist> techSpecialistList() {
        return uni.getUsers().stream()
                .filter(u -> u instanceof TechSupportSpecialist)
                .map(u -> (TechSupportSpecialist) u)
                .collect(Collectors.toList());
    }

    private static <T> void printIndexed(List<T> list, java.util.function.Function<T, String> label) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println((i + 1) + ". " + label.apply(list.get(i)));
        }
    }

    private static boolean validIdx(int idx, List<?> list) {
        if (idx >= 0 && idx < list.size()) return true;
        System.out.println("Invalid choice.");
        return false;
    }

    private static int readInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Enter a number: "); }
        }
    }

    private static double readDouble() {
        while (true) {
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Enter a number: "); }
        }
    }

    //seed data

    @SuppressWarnings("deprecation")
    private static void seedData() {
        if (!uni.getUsers().isEmpty()) return;

        // Users
        Admin admin = new Admin("1", "Assel", "Bazhikey", "admin@kbtu.kz", "admin123", "EMP001", "IT");

        Teacher prof = new Teacher("2", "Dariya", "Yergaliyeva", "prof@kbtu.kz", "prof123", "EMP002", "CS", TeacherPosition.PROFESSOR);
        ResearcherRole profRole = new ResearcherRole(prof);
        ResearchPaper p1 = new ResearchPaper("Deep Learning Survey",
                List.of("Dariya Yergaliyeva", "Bek Erlepes"), "Journal of CS", 20,
                new Date(120, 0, 1), "10.1234/dl");
        ResearchPaper p2 = new ResearchPaper("NLP Advances",
                List.of("Dariya Yergaliyeva"), "AI Review", 15,
                new Date(121, 5, 15), "10.1234/nlp");
        ResearchPaper p3 = new ResearchPaper("Transformer Models",
                List.of("Dariya Yergaliyeva", "Anel Yeraliyeva"), "IEEE", 25,
                new Date(122, 3, 10), "10.1234/trans");
        for (int i = 0; i < 5; i++) p1.addCitation();
        for (int i = 0; i < 4; i++) p2.addCitation();
        for (int i = 0; i < 3; i++) p3.addCitation();
        profRole.addPaper(p1); profRole.addPaper(p2); profRole.addPaper(p3);
        prof.setResearcherRole(profRole);

        Student student = new Student("3", "Bek", "Erlepes", "student@kbtu.kz", "student123", "STU001");
        student.setGpa(3.5);

        GraduateStudent grad = new GraduateStudent("4", "Anel", "Yeraliyeva", "grad@kbtu.kz", "grad123", "STU002", "MASTER");
        try { grad.setSupervisor(profRole); } catch (LowHIndexException e) { System.out.println(e.getMessage()); }
        ResearchPaper gradPaper = new ResearchPaper("Graph Neural Networks",
                List.of("Anel Yeraliyeva"), "ICML", 12, new Date(), "10.5678/gnn");
        grad.getResearcherRole().addPaper(gradPaper);
        grad.addDiplomaProject(gradPaper);
        grad.setGpa(3.8);

        Manager manager = new Manager("5", "Nurasyl", "Dulatuly", "manager@kbtu.kz", "manager123", "EMP003", "Academic", ManagerType.DEPARTMENT);
        TechSupportSpecialist tech = new TechSupportSpecialist("6", "Serdar", "Dundar", "tech@kbtu.kz", "tech123", "EMP004", "IT");

        // Courses
        Course cs101  = new Course("CS101",   "Intro to CS",  3, CourseType.MAJOR,       30);
        Course math   = new Course("MATH201", "Calculus",     4, CourseType.MAJOR,        25);
        Course phy101 = new Course("PHY101",  "Physics",      3, CourseType.MINOR,        20);
        Course free   = new Course("OG101",   "Oil & Gas 101",3, CourseType.FREE_ELECTIVE, 15);

        prof.addCourse(cs101); cs101.addTeacher(prof);
        prof.addCourse(math);  math.addTeacher(prof);

        cs101.addStudent(student);
        cs101.addMark(student, new Mark(25, 22, 35));

        // Research journal (Observer pattern)
        ResearchJournal journal = new ResearchJournal("Journal of CS");
        journal.publishPaper(p1);

        // Tech request demo
        TechRequest req = new TechRequest("Projector in room 305 is broken", student);
        tech.receiveRequest(req);

        // News
        uni.addNews(new News("Semester start", "New semester begins September 1.", "General"));
        uni.addNews(new News("Research Symposium", "Annual event this Friday.", "Research"));

        // Register everything
        uni.addUser(admin);
        uni.addUser(prof);
        uni.addUser(student);
        uni.addUser(grad);
        uni.addUser(manager);
        uni.addUser(tech);

        uni.addCourse(cs101);
        uni.addCourse(math);
        uni.addCourse(phy101);
        uni.addCourse(free);

        uni.addJournal(journal);

        System.out.println("Demo data initialized.");
        System.out.println("Accounts: admin@kbtu.kz/admin123 | prof@kbtu.kz/prof123 | student@kbtu.kz/student123");
        System.out.println("          grad@kbtu.kz/grad123   | manager@kbtu.kz/manager123 | tech@kbtu.kz/tech123");
    }
}
