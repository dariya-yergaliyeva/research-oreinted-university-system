import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import university.University;
import users.*;
import courses.*;
import research.*;
import comunication.News;
import enums.*;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    static final String DATA_FILE = "university.dat";

    static University uni() { return University.getInstance(); }

    public static void start() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/login",                Server::handleLogin);
        server.createContext("/api/courses",              Server::handleCourses);
        server.createContext("/api/my-courses",           Server::handleMyCourses);
        server.createContext("/api/register",             Server::handleRegister);
        server.createContext("/api/marks",                Server::handleMarks);
        server.createContext("/api/transcript",           Server::handleTranscript);
        server.createContext("/api/news",                 Server::handleNews);
        server.createContext("/api/add-news",             Server::handleAddNews);
        server.createContext("/api/papers",               Server::handlePapers);
        server.createContext("/api/add-paper",            Server::handleAddPaper);
        server.createContext("/api/journals",             Server::handleJournals);
        server.createContext("/api/subscribe-journal",    Server::handleSubscribeJournal);
        server.createContext("/api/top-researcher",       Server::handleTopResearcher);
        server.createContext("/api/requests",             Server::handleRequests);
        server.createContext("/api/new-request",          Server::handleNewRequest);
        server.createContext("/api/request-action",       Server::handleRequestAction);
        server.createContext("/api/teacher-courses",      Server::handleTeacherCourses);
        server.createContext("/api/course-students",      Server::handleCourseStudents);
        server.createContext("/api/put-mark",             Server::handlePutMark);
        server.createContext("/api/send-complaint",       Server::handleSendComplaint);
        server.createContext("/api/course-report",        Server::handleCourseReport);
        server.createContext("/api/students",             Server::handleStudents);
        server.createContext("/api/assign-teacher",       Server::handleAssignTeacher);
        server.createContext("/api/approve-registration", Server::handleApproveRegistration);
        server.createContext("/api/users",                Server::handleUsers);
        server.createContext("/api/logs",                 Server::handleLogs);
        server.createContext("/api/add-student",          Server::handleAddStudent);
        server.createContext("/api/remove-user",          Server::handleRemoveUser);
        server.createContext("/api/rate-teacher",         Server::handleRateTeacher);
        server.createContext("/api/send-message",         Server::handleSendMessage);

        server.start();
        System.out.println("Server started: http://localhost:8080");
    }

    // ─────────────────────────── AUTH ────────────────────────────────────────

    static void handleLogin(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body, "email"));
        String pw    = dec(par(body, "password"));

        var found = uni().findByEmail(email);
        if (found.isEmpty() || !found.get().login(email, pw)) {
            send(ex, 401, "{\"error\":\"Invalid credentials\"}"); return;
        }
        User u = found.get();
        StringBuilder sb = new StringBuilder("{");
        app(sb, "fn",    u.getFirstName());
        sb.append(","); app(sb, "ln", u.getLastName());
        sb.append(","); app(sb, "email", u.getEmail());
        sb.append(","); app(sb, "role", u.getClass().getSimpleName());

        if (u instanceof GraduateStudent gs) {
            sb.append(",\"gpa\":").append(gs.getGpa());
            sb.append(",\"credits\":").append(gs.getCredits());
            sb.append(",\"hasResearch\":true");
            sb.append(",\"hindex\":").append(gs.getResearcherRole().calculateHIndex());
            sb.append(","); app(sb, "degree", gs.getDegreeType());
        } else if (u instanceof Student s) {
            sb.append(",\"gpa\":").append(s.getGpa());
            sb.append(",\"credits\":").append(s.getCredits());
            boolean hr = s.getResearcherRole() != null;
            sb.append(",\"hasResearch\":").append(hr);
            if (hr) sb.append(",\"hindex\":").append(s.getResearcherRole().calculateHIndex());
        }
        if (u instanceof Teacher t) {
            sb.append(",\"rating\":").append(t.getRating());
            sb.append(","); app(sb, "pos", t.getPosition().toString());
            boolean hr = t.getResearcherRole() != null;
            sb.append(",\"hasResearch\":").append(hr);
            if (hr) sb.append(",\"hindex\":").append(t.getResearcherRole().calculateHIndex());
        }
        if (u instanceof Manager m) {
            sb.append(","); app(sb, "managerType", m.getManagerType().toString());
        }
        sb.append("}");
        send(ex, 200, sb.toString());
    }

    // ─────────────────────────── COURSES ─────────────────────────────────────

    static void handleCourses(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        List<Course> list = uni().getCourses();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) { if (i > 0) sb.append(","); sb.append(cJson(list.get(i))); }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleMyCourses(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        var found = uni().findByEmail(email);
        if (found.isEmpty() || !(found.get() instanceof Student s)) {
            send(ex, 400, "{\"error\":\"Student not found\"}"); return;
        }
        List<Course> list = s.getCourses();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) { if (i > 0) sb.append(","); sb.append(cJson(list.get(i))); }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleRegister(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body     = body(ex);
        String email    = dec(par(body, "email"));
        String courseId = dec(par(body, "courseId"));

        var fu = uni().findByEmail(email);
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        if (fu.isEmpty() || !(fu.get() instanceof Student s)) { send(ex, 400, "{\"error\":\"Student not found\"}"); return; }
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }
        if (s.getCourses().stream().anyMatch(c -> c.getCourseId().equals(courseId))) {
            send(ex, 400, "{\"error\":\"Already registered\"}"); return;
        }
        Course c = fc.get();
        if (s.getCredits() + c.getCredits() > 21) { send(ex, 400, "{\"error\":\"Credit limit exceeded\"}"); return; }
        s.registerCourse(c);
        save();
        send(ex, 200, "{\"ok\":true,\"credits\":" + s.getCredits() + "}");
    }

    // ─────────────────────────── MARKS ───────────────────────────────────────

    static void handleMarks(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        var found = uni().findByEmail(email);
        if (found.isEmpty() || !(found.get() instanceof Student s)) {
            send(ex, 400, "{\"error\":\"Not found\"}"); return;
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Course c : s.getCourses()) {
            if (!first) sb.append(","); first = false;
            List<Mark> marks = c.getMarksForStudent(s);
            double a1=0, a2=0, fe=0, total=0; String grade = "—";
            if (!marks.isEmpty()) {
                Mark m = marks.get(marks.size() - 1);
                a1=m.getFirstAttestation(); a2=m.getSecondAttestation();
                fe=m.getFinalExam(); total=m.getTotalMark(); grade=m.getLetterGrade();
            }
            sb.append("{"); app(sb, "course", c.getName());
            sb.append(","); app(sb, "code", c.getCourseId());
            sb.append(",\"credits\":").append(c.getCredits());
            sb.append(",\"a1\":").append(a1).append(",\"a2\":").append(a2);
            sb.append(",\"exam\":").append(fe).append(",\"total\":").append(total);
            sb.append(","); app(sb, "grade", grade); sb.append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleTranscript(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        var found = uni().findByEmail(email);
        if (found.isEmpty() || !(found.get() instanceof Student s)) {
            send(ex, 400, "{\"error\":\"Not found\"}"); return;
        }
        StringBuilder sb = new StringBuilder("{");
        app(sb, "name", s.getFirstName() + " " + s.getLastName());
        sb.append(",\"gpa\":").append(s.getGpa()).append(",\"credits\":").append(s.getCredits());
        sb.append(",\"courses\":[");
        List<Course> cs = s.getCourses();
        for (int i = 0; i < cs.size(); i++) {
            if (i > 0) sb.append(",");
            Course c = cs.get(i); List<Mark> ms = c.getMarksForStudent(s);
            String grade = ms.isEmpty() ? "—" : ms.get(ms.size()-1).getLetterGrade();
            sb.append("{"); app(sb, "name", c.getName());
            sb.append(",\"credits\":").append(c.getCredits()).append(","); app(sb, "grade", grade); sb.append("}");
        }
        sb.append("]}");
        send(ex, 200, sb.toString());
    }

    // ─────────────────────────── NEWS ────────────────────────────────────────

    static void handleNews(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        List<News> list = uni().getNewsList();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            News n = list.get(i);
            sb.append("{"); app(sb, "title", n.getTitle());
            sb.append(","); app(sb, "body", n.getContent());
            sb.append(","); app(sb, "topic", n.getTopic());
            sb.append(",\"pinned\":").append(n.isPinned()).append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleAddNews(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body    = body(ex);
        String email   = dec(par(body, "email"));
        String title   = dec(par(body, "title"));
        String content = dec(par(body, "content"));
        String topic   = dec(par(body, "topic"));
        var found = uni().findByEmail(email);
        if (found.isEmpty()) { send(ex, 403, "{\"error\":\"Unauthorized\"}"); return; }
        uni().addNews(new News(title, content, topic.isEmpty() ? "General" : topic));
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    // ─────────────────────────── RESEARCH ────────────────────────────────────

    static void handlePapers(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        String sort  = qp(ex, "sort");
        var found = uni().findByEmail(email);
        if (found.isEmpty()) { send(ex, 400, "{\"error\":\"Not found\"}"); return; }
        ResearcherRole role = resRole(found.get());
        if (role == null) { send(ex, 200, "{\"hindex\":0,\"papers\":[]}"); return; }

        List<ResearchPaper> papers = new ArrayList<>(role.getResearchPapers());
        if ("date".equals(sort)) papers.sort(Comparator.comparing(ResearchPaper::getDate).reversed());
        else papers.sort(Comparator.comparingInt(ResearchPaper::getCitations).reversed());

        StringBuilder sb = new StringBuilder("{\"hindex\":").append(role.calculateHIndex()).append(",\"papers\":[");
        for (int i = 0; i < papers.size(); i++) {
            if (i > 0) sb.append(",");
            ResearchPaper p = papers.get(i);
            int yr = p.getDate().getYear() + 1900;
            int mo = p.getDate().getMonth() + 1;
            sb.append("{"); app(sb, "title", p.getTitle());
            sb.append(","); app(sb, "journal", p.getJournal());
            sb.append(","); app(sb, "date", yr + "-" + String.format("%02d", mo));
            sb.append(",\"citations\":").append(p.getCitations());
            sb.append(","); app(sb, "doi", p.getDoi());
            sb.append(",\"pages\":").append(p.getPages());
            sb.append(","); app(sb, "authors", String.join(", ", p.getAuthors())); sb.append("}");
        }
        sb.append("]}");
        send(ex, 200, sb.toString());
    }

    static void handleAddPaper(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body    = body(ex);
        String email   = dec(par(body, "email"));
        String title   = dec(par(body, "title"));
        String journal = dec(par(body, "journal"));
        String doi     = dec(par(body, "doi"));
        int pages = 1;
        try { pages = Integer.parseInt(par(body, "pages")); } catch (Exception ignored) {}

        var found = uni().findByEmail(email);
        if (found.isEmpty()) { send(ex, 400, "{\"error\":\"Not found\"}"); return; }
        ResearcherRole role = resRole(found.get());
        if (role == null) { send(ex, 400, "{\"error\":\"No researcher role\"}"); return; }

        ResearchPaper p = new ResearchPaper(title,
            List.of(found.get().getFirstName() + " " + found.get().getLastName()),
            journal.isEmpty() ? "Unknown" : journal, pages, new Date(), doi.isEmpty() ? "—" : doi);
        role.addPaper(p);
        uni().addNews(new News("New paper: " + title,
            "Published by " + found.get().getFirstName() + " in " + journal, "Research"));
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleJournals(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        Optional<User> uOpt = email.isEmpty() ? Optional.empty() : uni().findByEmail(email);
        List<ResearchJournal> list = uni().getJournals();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            ResearchJournal j = list.get(i);
            boolean sub = uOpt.isPresent() && j.getSubscribers().contains(uOpt.get());
            sb.append("{"); app(sb, "name", j.getName());
            sb.append(",\"papers\":").append(j.getPapers().size());
            sb.append(",\"subscribed\":").append(sub).append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleSubscribeJournal(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body  = body(ex);
        String email = dec(par(body, "email"));
        String jname = dec(par(body, "journalName"));
        var found = uni().findByEmail(email);
        if (found.isEmpty()) { send(ex, 400, "{\"error\":\"Not found\"}"); return; }
        var j = uni().getJournals().stream().filter(x -> x.getName().equals(jname)).findFirst();
        if (j.isEmpty()) { send(ex, 400, "{\"error\":\"Journal not found\"}"); return; }
        j.get().subscribe(found.get());
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleTopResearcher(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        var top = uni().getTopCitedResearcher();
        if (top == null) { send(ex, 200, "{\"name\":null,\"hindex\":0}"); return; }
        StringBuilder sb = new StringBuilder("{");
        User u = (top instanceof ResearcherRole rr) ? rr.getUser() : null;
        String name = (u != null) ? (u.getFirstName() + " " + u.getLastName()) : "—";
        app(sb, "name", name);
        sb.append(",\"hindex\":").append(top.calculateHIndex()).append("}");
        send(ex, 200, sb.toString());
    }

    // ─────────────────────────── TECH SUPPORT ────────────────────────────────

    static void handleRequests(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        var found = uni().findByEmail(email);

        List<TechRequest> reqs;
        if (found.isPresent() && found.get() instanceof TechSupportSpecialist tech) {
            reqs = tech.getAllRequests();
        } else {
            reqs = new ArrayList<>();
            for (User u : uni().getUsers()) {
                if (u instanceof TechSupportSpecialist tech) {
                    for (TechRequest r : tech.getAllRequests()) {
                        if (found.isPresent() && r.getCreatedBy().getEmail().equals(email))
                            reqs.add(r);
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < reqs.size(); i++) {
            if (i > 0) sb.append(",");
            TechRequest r = reqs.get(i);
            sb.append("{\"idx\":").append(i).append(",");
            app(sb, "desc", r.getDescription()); sb.append(",");
            app(sb, "status", r.getStatus().toString()); sb.append(",");
            app(sb, "by", r.getCreatedBy().getFirstName() + " " + r.getCreatedBy().getLastName()); sb.append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleNewRequest(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body  = body(ex);
        String email = dec(par(body, "email"));
        String desc  = dec(par(body, "desc"));
        var found = uni().findByEmail(email);
        if (found.isEmpty()) { send(ex, 400, "{\"error\":\"Not found\"}"); return; }
        TechRequest req = new TechRequest(desc, found.get());
        uni().getUsers().stream()
            .filter(u -> u instanceof TechSupportSpecialist).map(u -> (TechSupportSpecialist) u)
            .findFirst().ifPresent(t -> t.receiveRequest(req));
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleRequestAction(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body   = body(ex);
        String email  = dec(par(body, "email"));
        String action = dec(par(body, "action"));
        int idx = -1;
        try { idx = Integer.parseInt(par(body, "idx")); } catch (Exception ignored) {}

        var found = uni().findByEmail(email);
        if (found.isEmpty() || !(found.get() instanceof TechSupportSpecialist tech)) {
            send(ex, 403, "{\"error\":\"Not authorized\"}"); return;
        }
        List<TechRequest> reqs = tech.getAllRequests();
        if (idx < 0 || idx >= reqs.size()) { send(ex, 400, "{\"error\":\"Invalid index\"}"); return; }
        TechRequest r = reqs.get(idx);
        switch (action) {
            case "accept" -> tech.acceptRequest(r);
            case "reject" -> tech.rejectRequest(r);
            case "done"   -> tech.markDone(r);
            case "view"   -> r.markViewed();
        }
        save();
        send(ex, 200, "{\"ok\":true,\"status\":\"" + r.getStatus() + "\"}");
    }

    // ─────────────────────────── TEACHER ─────────────────────────────────────

    static void handleTeacherCourses(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String email = dec(qp(ex, "email"));
        var found = uni().findByEmail(email);
        if (found.isEmpty() || !(found.get() instanceof Teacher t)) {
            send(ex, 400, "{\"error\":\"Teacher not found\"}"); return;
        }
        List<Course> list = t.getCourses();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) { if (i > 0) sb.append(","); sb.append(cJson(list.get(i))); }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleCourseStudents(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String courseId = dec(qp(ex, "courseId"));
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }
        List<Student> list = fc.get().getStudents();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Student s = list.get(i);
            sb.append("{"); app(sb, "email", s.getEmail()); sb.append(",");
            app(sb, "name", s.getFirstName() + " " + s.getLastName());
            sb.append(",\"gpa\":").append(s.getGpa()).append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handlePutMark(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body, "email")), courseId = dec(par(body, "courseId")),
               stEmail = dec(par(body, "studentEmail"));
        double a1=0, a2=0, fe=0;
        try { a1 = Double.parseDouble(par(body,"a1")); } catch(Exception e) {}
        try { a2 = Double.parseDouble(par(body,"a2")); } catch(Exception e) {}
        try { fe = Double.parseDouble(par(body,"fe")); } catch(Exception e) {}

        var ft = uni().findByEmail(email);
        var fs = uni().findByEmail(stEmail);
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        if (ft.isEmpty() || !(ft.get() instanceof Teacher t)) { send(ex, 403, "{\"error\":\"Not a teacher\"}"); return; }
        if (fs.isEmpty() || !(fs.get() instanceof Student s)) { send(ex, 400, "{\"error\":\"Student not found\"}"); return; }
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }

        Mark mark = new Mark(a1, a2, fe);
        fc.get().addMark(s, mark);
        t.putMark(s, fc.get(), mark);
        save();
        send(ex, 200, "{\"ok\":true,\"grade\":\""+mark.getLetterGrade()+"\",\"total\":"+mark.getTotalMark()+"}");
    }

    static void handleSendComplaint(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), stEmail = dec(par(body,"studentEmail")),
               urg   = dec(par(body,"urgency"));
        var ft = uni().findByEmail(email);
        var fs = uni().findByEmail(stEmail);
        if (ft.isEmpty() || !(ft.get() instanceof Teacher t)) { send(ex, 403, "{\"error\":\"Not a teacher\"}"); return; }
        if (fs.isEmpty() || !(fs.get() instanceof Student s)) { send(ex, 400, "{\"error\":\"Student not found\"}"); return; }
        UrgencyLevel level = switch (urg.toUpperCase()) {
            case "HIGH" -> UrgencyLevel.HIGH; case "LOW" -> UrgencyLevel.LOW; default -> UrgencyLevel.MEDIUM;
        };
        t.sendComplaint(s, level);
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleCourseReport(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String courseId = dec(qp(ex, "courseId"));
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }
        Course course = fc.get();
        List<Student> students = course.getStudents();
        double total = 0; int cnt = 0;
        StringBuilder rows = new StringBuilder();
        for (int i = 0; i < students.size(); i++) {
            if (i > 0) rows.append(",");
            Student s = students.get(i);
            List<Mark> ms = course.getMarksForStudent(s);
            double avg = 0;
            if (!ms.isEmpty()) { double sum = ms.stream().mapToDouble(Mark::getTotalMark).sum(); avg = sum/ms.size(); total+=sum; cnt+=ms.size(); }
            rows.append("{"); app(rows, "name", s.getFirstName()+" "+s.getLastName());
            rows.append(",\"avg\":").append(String.format("%.1f", avg));
            rows.append(",\"marks\":").append(ms.size()).append("}");
        }
        double avg = cnt==0?0:total/cnt;
        StringBuilder sb = new StringBuilder("{");
        app(sb, "course", course.getName());
        sb.append(",\"enrolled\":").append(students.size());
        sb.append(",\"avgMark\":").append(String.format("%.1f", avg));
        sb.append(",\"students\":[").append(rows).append("]}");
        send(ex, 200, sb.toString());
    }

    // ─────────────────────────── MANAGER ─────────────────────────────────────

    static void handleStudents(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        String sort = qp(ex, "sort");
        List<Student> list = uni().getUsers().stream()
            .filter(u -> u instanceof Student).map(u -> (Student)u).collect(Collectors.toList());
        if ("alpha".equals(sort)) list.sort(Comparator.comparing(User::getLastName));
        else list.sort(Comparator.comparingDouble(Student::getGpa).reversed());
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            Student s = list.get(i);
            sb.append("{"); app(sb, "name", s.getFirstName()+" "+s.getLastName()); sb.append(",");
            app(sb, "email", s.getEmail()); sb.append(",");
            app(sb, "role", s.getClass().getSimpleName());
            sb.append(",\"gpa\":").append(s.getGpa()).append(",\"credits\":").append(s.getCredits()).append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleAssignTeacher(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), courseId = dec(par(body,"courseId")),
               tEmail = dec(par(body,"teacherEmail"));
        var fm = uni().findByEmail(email);
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        var ft = uni().findByEmail(tEmail);
        if (fm.isEmpty() || !(fm.get() instanceof Manager m)) { send(ex, 403, "{\"error\":\"Not a manager\"}"); return; }
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }
        if (ft.isEmpty() || !(ft.get() instanceof Teacher t)) { send(ex, 400, "{\"error\":\"Teacher not found\"}"); return; }
        m.assignCourseToTeacher(fc.get(), t);
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleApproveRegistration(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), stEmail = dec(par(body,"studentEmail")),
               courseId = dec(par(body,"courseId"));
        var fm = uni().findByEmail(email);
        var fs = uni().findByEmail(stEmail);
        var fc = uni().getCourses().stream().filter(c -> c.getCourseId().equals(courseId)).findFirst();
        if (fm.isEmpty() || !(fm.get() instanceof Manager m)) { send(ex, 403, "{\"error\":\"Not a manager\"}"); return; }
        if (fs.isEmpty() || !(fs.get() instanceof Student s)) { send(ex, 400, "{\"error\":\"Student not found\"}"); return; }
        if (fc.isEmpty()) { send(ex, 400, "{\"error\":\"Course not found\"}"); return; }
        m.approveRegistration(s, fc.get());
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    // ─────────────────────────── ADMIN ───────────────────────────────────────

    static void handleUsers(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        List<User> list = uni().getUsers();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            User u = list.get(i);
            sb.append("{"); app(sb, "name", u.getFirstName()+" "+u.getLastName()); sb.append(",");
            app(sb, "email", u.getEmail()); sb.append(",");
            app(sb, "role", u.getClass().getSimpleName()); sb.append("}");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleLogs(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        List<String> logs = uni().getLogs();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < logs.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(esc(logs.get(i))).append("\"");
        }
        sb.append("]");
        send(ex, 200, sb.toString());
    }

    static void handleAddStudent(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), fn = dec(par(body,"fn")), ln = dec(par(body,"ln")),
               em = dec(par(body,"em")), pw = dec(par(body,"pw")), sid = dec(par(body,"sid"));
        var fa = uni().findByEmail(email);
        if (fa.isEmpty() || !(fa.get() instanceof Admin admin)) { send(ex, 403, "{\"error\":\"Not an admin\"}"); return; }
        if (uni().findByEmail(em).isPresent()) { send(ex, 400, "{\"error\":\"Email exists\"}"); return; }
        Student s = new Student("u"+System.currentTimeMillis(), fn, ln, em, pw, sid);
        uni().addUser(s); admin.addUser(s);
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    static void handleRemoveUser(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), targetEmail = dec(par(body,"targetEmail"));
        var fa = uni().findByEmail(email);
        if (fa.isEmpty() || !(fa.get() instanceof Admin admin)) { send(ex, 403, "{\"error\":\"Not an admin\"}"); return; }
        var target = uni().findByEmail(targetEmail);
        if (target.isEmpty()) { send(ex, 400, "{\"error\":\"User not found\"}"); return; }
        uni().removeUser(target.get()); admin.removeUser(target.get());
        save();
        send(ex, 200, "{\"ok\":true}");
    }

    // ─────────────────────────── MISC ────────────────────────────────────────

    static void handleRateTeacher(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), tEmail = dec(par(body,"teacherEmail"));
        int rating = 0;
        try { rating = Integer.parseInt(par(body,"rating")); } catch(Exception e) {}
        var fs = uni().findByEmail(email);
        var ft = uni().findByEmail(tEmail);
        if (fs.isEmpty() || !(fs.get() instanceof Student s)) { send(ex, 403, "{\"error\":\"Not a student\"}"); return; }
        if (ft.isEmpty() || !(ft.get() instanceof Teacher t)) { send(ex, 400, "{\"error\":\"Teacher not found\"}"); return; }
        if (rating<1||rating>10) { send(ex, 400, "{\"error\":\"Rating must be 1-10\"}"); return; }
        s.rateTeacher(t, rating);
        save();
        send(ex, 200, "{\"ok\":true,\"newRating\":"+t.getRating()+"}");
    }

    static void handleSendMessage(HttpExchange ex) throws IOException {
        if (cors(ex)) return;
        if (!"POST".equals(ex.getRequestMethod())) { send(ex, 405, "{}"); return; }
        String body = body(ex);
        String email = dec(par(body,"email")), toEmail = dec(par(body,"toEmail")), text = dec(par(body,"text"));
        var from = uni().findByEmail(email);
        var to   = uni().findByEmail(toEmail);
        if (from.isEmpty()||to.isEmpty()) { send(ex, 400, "{\"error\":\"User not found\"}"); return; }
        from.get().sendMessage(to.get(), text);
        send(ex, 200, "{\"ok\":true}");
    }

    // ─────────────────────────── HELPERS ─────────────────────────────────────

    static ResearcherRole resRole(User u) {
        if (u instanceof Teacher t) return t.getResearcherRole();
        if (u instanceof Student s) return s.getResearcherRole();
        return null;
    }

    static String cJson(Course c) {
        String teacher = c.getTeachers().isEmpty() ? "" :
            c.getTeachers().get(0).getFirstName() + " " + c.getTeachers().get(0).getLastName();
        StringBuilder sb = new StringBuilder("{");
        app(sb,"id",c.getCourseId()); sb.append(",");
        app(sb,"name",c.getName());
        sb.append(",\"credits\":").append(c.getCredits()).append(",");
        app(sb,"type",c.getCourseType().toString());
        sb.append(",\"students\":").append(c.getStudents().size());
        sb.append(",\"maxStudents\":").append(c.getMaxStudents()).append(",");
        app(sb,"teacher",teacher);
        sb.append("}");
        return sb.toString();
    }

    static boolean cors(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equals(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            ex.getResponseBody().close();
            return true;
        }
        return false;
    }

    static void send(HttpExchange ex, int code, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    static String body(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    static String par(String body, String key) {
        for (String p : body.split("&")) {
            int eq = p.indexOf('=');
            if (eq > 0 && p.substring(0,eq).equals(key)) return eq+1<p.length()?p.substring(eq+1):"";
        }
        return "";
    }

    static String qp(HttpExchange ex, String key) {
        String q = ex.getRequestURI().getRawQuery();
        return q == null ? "" : par(q, key);
    }

    static String dec(String s) {
        try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch(Exception e) { return s; }
    }

    static String esc(String s) {
        if (s==null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }

    static void app(StringBuilder sb, String key, String val) {
        sb.append("\"").append(key).append("\":\"").append(esc(val)).append("\"");
    }

    static void save() { uni().saveToFile(DATA_FILE); }
}
