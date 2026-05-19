package research;
import exception.LowHIndexException;
import users.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
 

public class ResearcherRole implements Researcher, Serializable {
 
    private User user;
    private List<ResearchPaper> papers;
    private List<ResearchProject> projects;
 
    public ResearcherRole(User user) {
        this.user = user;
        this.papers = new ArrayList<>();
        this.projects = new ArrayList<>();
    }
 
 
    @Override
    public int calculateHIndex() {
        List<Integer> sorted = papers.stream().map(ResearchPaper::getCitations).sorted(Comparator.reverseOrder()).toList();
 
        int h = 0;
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i) >= i + 1) h = i + 1;
            else break;
        }
        return h;
    }
 
    @Override
    public void printPapers(Comparator<ResearchPaper> c) {
        System.out.println("=== Papers of " + user.getFirstName() + " " + user.getLastName() + " ===");
        papers.stream().sorted(c).forEach(System.out::println);
    }
 
    @Override
    public List<ResearchPaper> getResearchPapers() {
        return papers;
    }
 
    @Override
    public List<ResearchProject> getResearchProjects() {
        return projects;
    }
 
 
    public void addPaper(ResearchPaper paper) {
        papers.add(paper);
        System.out.println(user.getFirstName() + " published: " + paper.getTitle());
    }
 
    public void addProject(ResearchProject project) {
        projects.add(project);
    }

    public void validateAsSupervisor() throws LowHIndexException {
        int h = calculateHIndex();
        if (h < 3) {
            throw new LowHIndexException(
                user.getFirstName() + " " + user.getLastName()
                + " has h-index = " + h + " (minimum 3 required)");
        }
    }
 
    public User getUser() { return user; }
 
    @Override
    public String toString() {
        return "ResearcherRole{"
             + "user=" + user.getFirstName() + " " + user.getLastName()
             + ", papers=" + papers.size()
             + ", h-index=" + calculateHIndex()
             + '}';
    }
}