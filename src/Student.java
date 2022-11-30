import java.io.*;
import java.util.*;

public class Student implements Eligible, Comparable<Student> {
    protected String name;
    protected float cgpa;
    protected int id;
    protected String branch;
    protected ArrayList<String> subjects;
    protected PreferenceOrder preferenceOrder;
    /* 
     * status 'a' : accepted
     * status 'r' : rejected
     * status 'w' : withdrawn
     */
    protected char status;
    /* 
     * finalized acts as a security check.
     * once accepted / withdrawn, student should not be able to unfreeze
     */
    protected boolean finalized;

    public Student(String name, float cgpa, int id, String branch, ArrayList<String> subjects) {
        this.name = name;
        this.cgpa = cgpa;
        this.id = id;
        this.branch = branch;
        this.subjects = subjects;
        /*
         * default status = rejected
         * consider for further rounds until accepted or withdrawn
         * default finalized = false
         * student can change status until finalized
         */
        this.status = 'r';
        this.finalized = false;
    }

    public Student(File studentFile, File preferenceOrderFile) {
        try {
            Scanner sc = new Scanner(studentFile);
            String line = sc.nextLine();
            String[] data = line.split(",");

            this.name = data[0];

            this.cgpa = Float.parseFloat(data[1]);

            this.id = Integer.parseInt(data[2]);

            this.branch = data[3];

            this.subjects = new ArrayList<String>();
            for (int i = 4; i < data.length; i++) {
                this.subjects.add(data[i]);
            }

            sc.close();

            this.preferenceOrder = new PreferenceOrder(preferenceOrderFile);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } finally {
            this.status = 'r';
            this.finalized = false;
        }
    }

    public String toString() {
        return this.name + ", " + this.cgpa + ", " + this.id + ", " + this.branch + ", " + this.subjects + "\n";
    }

    public String getName() {
        return this.name;
    }

    public float getCgpa() {
        return this.cgpa;
    }

    public int getId() {
        return this.id;
    }

    public String getBranch() {
        return this.branch;
    }

    public ArrayList<String> getSubjects() {
        return this.subjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCgpa(float cgpa) {
        this.cgpa = cgpa;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setSubjects(ArrayList<String> subjects) {
        this.subjects = subjects;
    }

    public PreferenceOrder getPreferenceOrder() {
        return this.preferenceOrder;
    }

    public void setPreferenceOrder(PreferenceOrder preferenceOrder) {
        this.preferenceOrder = preferenceOrder;
    }

    public void addStation(Station station) {
        this.preferenceOrder.addStation(station);
    }

    public void addStations(ArrayList<Station> stations) {
        this.preferenceOrder.addStations(stations);
    }

    public boolean isEligible(PreferenceOrder preferenceOrder) {
        ArrayList<Station> order = preferenceOrder.getOrder();
        for (int i = 0; i < order.size(); i++) {
            Station station = order.get(i);
            if (station.getBranches().contains(this.branch)) {
                ArrayList<String> compulsorySubjects = station.getCompulsorySubjects();
                for (int j = 0; j < compulsorySubjects.size(); j++) {
                    if (!this.subjects.contains(compulsorySubjects.get(j))) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    public int compareTo(Student student) {
        if (this.cgpa > student.getCgpa()) {
            return -1;
        } else if (this.cgpa < student.getCgpa()) {
            return 1;
        } else {
            if (this.id < student.getId()) {
                return -1;
            } else if (this.id > student.getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public void finalize() {
        this.finalized = true;
    }
    
    @Override
    public boolean isEligible(Station station) {
        ArrayList<String> branches = station.getBranches();
        
        for (String branch : branches) {
            if (this.branch.equals(branch)) {
                ArrayList<String> compulsorySubjects = station.getCompulsorySubjects();

                for (String compulsorySubject : compulsorySubjects) {
                    if (!this.subjects.contains(compulsorySubject)) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }

    /* status changes only allowed if not yet finalized. */
    @Override
    public void accept() {
        if (!this.finalized) {
            this.status = 'a';
            finalize();
        }
    }

    @Override
    public void reject() {
        if (!this.finalized) {
            this.status = 'r';
            finalize();
        }
    }

    @Override
    public void withdraw() {
        if (!this.finalized) {
            this.status = 'w';
            finalize();
        }      
    }

    /* status getters */
    public boolean hasAccepted() {
        return  this.status == 'a';
    }

    public boolean hasRejected() {
        return this.status == 'r';
    }

    public boolean hasWithdrawn() {
        return this.status == 'w';
    }
}
