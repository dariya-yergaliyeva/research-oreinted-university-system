package research;

import patterns.observer.Observer;
import patterns.observer.Subject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
 

public class ResearchJournal implements Subject, Serializable {
 
    private String name;
    private List<ResearchPaper> papers;
    private List<Observer> subscribers;
 
    public ResearchJournal(String name) {
        this.name = name;
        this.papers = new ArrayList<>();
        this.subscribers = new ArrayList<>();
    }
 
    @Override
    public void subscribe(Observer o) {
        subscribers.add(o);
        System.out.println("Subscribed to journal: " + name);
    }
 
    @Override
    public void unsubscribe(Observer o) {
        subscribers.remove(o);
        System.out.println("Unsubscribed from journal: " + name);
    }
 
    @Override
    public void notifyObservers(String message) {
        for (Observer o : subscribers) {
            o.update(message);
        }
    }
 
 
    public void publishPaper(ResearchPaper p) {
        papers.add(p);
        notifyObservers("New paper published in [" + name + "]: "
                      + p.getTitle());
    }
 
    
    public String getName() { 
        return name; 
    }
    public List<ResearchPaper> getPapers(){ 
        return papers; 
    }
    public List<Observer> getSubscribers(){ 
        return subscribers; 
    }
 
    @Override
    public String toString() {
        return "ResearchJournal{"
             + "name='" + name + '\''
             + ", papers=" + papers.size()
             + ", subscribers=" + subscribers.size()
             + '}';
    }
}