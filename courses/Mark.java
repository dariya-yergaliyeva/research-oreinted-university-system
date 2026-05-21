package courses;
import java.io.Serializable;

public class Mark implements Serializable{
    private double firstAttestation;
    private double secondAttestation;
    private double finalExam;
    public Mark(){
        this.firstAttestation = 0;
        this.secondAttestation = 0;
        this.finalExam = 0;
    }
    public Mark(double firstAttestation, double secondAttestation, double finalExam){
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
    }
    public double getTotalMark(){
        return firstAttestation + secondAttestation + finalExam;
    }
    public String getLetterGrade(){
        double att = firstAttestation + secondAttestation;
        if (firstAttestation == 0 && secondAttestation == 0 && finalExam == 0) return "—";
        if (finalExam == 0) {
            if (firstAttestation > 0 && secondAttestation > 0 && att < 30) return "F";
            return "—";
        }
        if (att < 30) return "F";
        if (finalExam < 9.5) return "R";
        if (finalExam <= 19.5) return "FX";
        double total = getTotalMark();
        if(total >= 95) return "A";
        if(total >= 90) return "A-";
        if (total >= 85) return "B+";
        if (total >= 80) return "B";
        if (total >= 75) return "B-";
        if (total >= 70) return "C+";
        if (total >= 65) return "C";
        if (total >= 60) return "C-";
        if (total >= 55) return "D+";
        if (total >= 50) return "D";
        return "F";
    }
    public boolean isPassed(){
        String g = getLetterGrade();
        return !(g.equals("F") || g.equals("R") || g.equals("FX"));
    }
    public double getFirstAttestation()  { 
        return firstAttestation; 
    }
    public double getSecondAttestation(){ 
        return secondAttestation;
    }
    public double getFinalExam() { 
        return finalExam; 
    }
 
    public void setFirstAttestation(double v){ 
        this.firstAttestation = v; 
    }
    public void setSecondAttestation(double v){ 
        this.secondAttestation = v; 
    }
    public void setFinalExam(double v){ 
        this.finalExam = v; 
    }
    @Override
    public String toString(){
        return "Mark{"
             + "att1=" + firstAttestation
             + ", att2=" + secondAttestation
             + ", final=" + finalExam
             + ", total=" + getTotalMark()
             + ", grade='" + getLetterGrade() + '\''
             + '}';
    }
}