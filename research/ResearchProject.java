package research;
import exception.NotResearchException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 
public class ResearchProject implements Serializable {
    private String topic;
    private List<Researcher> participants;
    private List<ResearchPaper> publishedPapers;
 
    public ResearchProject(String topic) {
        this.topic = topic;
        this.participants = new ArrayList<>();
        this.publishedPapers = new ArrayList<>();
    }
 
    public void addParticipant(Researcher r) throws NotResearchException {
        if (r == null) {
            throw new NotResearchException(
                "Cannot add null as participant — must be a Researcher");
        }
        participants.add(r);
        System.out.println("Participant added to project: " + topic);
    }
 
    public void addPaper(ResearchPaper p) {
        publishedPapers.add(p);
        System.out.println("Paper added: " + p.getTitle());
    }
 
    public String getTopic(){ 
        return topic;
    }
    public List<Researcher> getParticipants(){ 
        return participants; 
    }
    public List<ResearchPaper> getPublishedPapers()  {
        return publishedPapers; 
    }
 
    @Override
    public String toString() {
        return "ResearchProject{"
             + "topic='" + topic + '\''
             + ", participants=" + participants.size()
             + ", papers=" + publishedPapers.size()
             + '}';
    }
}