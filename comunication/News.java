package comunication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
public class News implements Serializable {
 
    private String title;
    private String content;
    private String topic;
    private boolean isPinned;
    private List<String> comments;
    private Date date;
 
    public News(String title, String content, String topic) {
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.isPinned = false;
        this.comments = new ArrayList<>();
        this.date = new Date();
 
        
        if ("Research".equalsIgnoreCase(topic)) {
            this.isPinned = true;
        }
    }
 
    public void addComment(String c) {
        comments.add(c);
        System.out.println("Comment added to: " + title);
    }
 
    public void pin() {
        this.isPinned = true;
        System.out.println("News pinned: " + title);
    }
 
    public void unpin() {
        this.isPinned = false;
    }
 
    public String getTitle(){ 
        return title; 
    }
    public String getContent(){ 
        return content;
    }
    public String getTopic(){ 
        return topic; 
    }
    public boolean isPinned(){ 
        return isPinned;
    }
    public List<String> getComments(){ 
        return comments; 
    }
    public Date getDate(){ 
        return date;
    }
 
    @Override
    public String toString() {
        return "News{"
             + "title='" + title + '\''
             + ", topic='" + topic + '\''
             + ", pinned=" + isPinned
             + ", comments=" + comments.size()
             + '}';
    }
}