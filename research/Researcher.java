package research;

import java.util.Comparator;
import java.util.List;
 
public interface Researcher {
    int calculateHIndex();
    void printPapers(Comparator<ResearchPaper> c);
    List<ResearchPaper> getResearchPapers();
    List<ResearchProject> getResearchProjects();
}