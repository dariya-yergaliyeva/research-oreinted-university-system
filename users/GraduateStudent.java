package users;
import exception.LowHIndexException;
import java.util.ArrayList;
import java.util.List;
import research.ResearchPaper;
import research.ResearcherRole;

public class GraduateStudent extends Student {
    private String degreeType; // MASTER или PHD
    private ResearcherRole supervisor;
    private List<ResearchPaper> diplomaProjects;

    public GraduateStudent(String id, String firstName, String lastName, String email, String password, String studentId, String degreeType) {
        super(id, firstName, lastName, email, password, studentId);
        this.degreeType = degreeType;
        this.diplomaProjects = new ArrayList<>();
        this.setResearcherRole(new ResearcherRole(this));
    }

    public GraduateStudent() {
        super();
        this.diplomaProjects = new ArrayList<>();
        this.degreeType = "MASTER";
        this.setResearcherRole(new ResearcherRole(this));
    }

    public void setSupervisor(ResearcherRole supervisor) throws LowHIndexException {
        if (supervisor.calculateHIndex() < 3) {
            throw new LowHIndexException(
                "Supervisor h-index is too low: " + supervisor.calculateHIndex() + ". minimum is 3."
            );
        }
        this.supervisor = supervisor;
        System.out.println("supervisor set for " + getFirstName());
    }

    public void addDiplomaProject(ResearchPaper paper) {
        diplomaProjects.add(paper);
        System.out.println("diploma project added: " + paper.getTitle());
    }

    @Override
    public void displayInfo() {
        System.out.println("GraduateStudent: " + getFirstName() + " "
                         + getLastName() + " | Degree: " + degreeType
                         + " | GPA: " + getGpa());
    }

    public String getDegreeType() { 
        return degreeType; 
    }
    public ResearcherRole getSupervisor() { 
        return supervisor; 
    }
    public List<ResearchPaper> getDiplomaProjects() { 
        return diplomaProjects; 
    }

    @Override
    public String toString() {
        return "GraduateStudent{name='" + getFirstName() + "', degree=" + degreeType + "}";
    }
}