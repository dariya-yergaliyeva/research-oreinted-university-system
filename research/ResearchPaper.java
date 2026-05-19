package research;

import enums.CitationFormat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
 
public class ResearchPaper implements Comparable<ResearchPaper>, Serializable {
    private String title;
    private List<String> authors;
    private String journal;
    private int pages;
    private Date date;
    private String doi;
    private int citations;
 
    public ResearchPaper(String title, List<String> authors, String journal, int pages, Date date, String doi) {
        this.title = title;
        this.authors = authors;
        this.journal = journal;
        this.pages = pages;
        this.date = date;
        this.doi = doi;
        this.citations = 0;
    }
 
    public String getCitation(CitationFormat f) {
        if (f == CitationFormat.BIBTEX) {
            return "@article{" + doi + ",\n"
                 + "  title={" + title + "},\n"
                 + "  author={" + String.join(" and ", authors) + "},\n"
                 + "  journal={" + journal + "},\n"
                 + "  pages={" + pages + "},\n"
                 + "  year={" + (date.getYear() + 1900) + "},\n"
                 + "  doi={" + doi + "}\n"
                 + "}";
        } else {
            return String.join(", ", authors)
                 + " (" + (date.getYear() + 1900) + "). "
                 + title + ". "
                 + journal + ". "
                 + "Pages: " + pages + ". "
                 + "DOI: " + doi;
        }
    }
 
    public void addCitation() {
        this.citations++;
    }
 

    @Override
    public int compareTo(ResearchPaper other) {
        return Integer.compare(other.citations, this.citations);
    }
 

    public String getTitle() { 
        return title; 
    }
    public List<String> getAuthors(){ 
        return authors; 
    }
    public String getJournal(){ 
        return journal; 
    }
    public int getPages(){ 
        return pages; 
    }
    public Date getDate(){ 
        return date; 
    }
    public String getDoi(){ 
        return doi; 
    }
    public int getCitations() { 
        return citations; 
    }
 
    @Override
    public String toString() {
        return "ResearchPaper{"
             + "title='" + title + '\''
             + ", journal='" + journal + '\''
             + ", citations=" + citations
             + ", pages=" + pages
             + '}';
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper that = (ResearchPaper) o;
        return Objects.equals(doi, that.doi);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }
}