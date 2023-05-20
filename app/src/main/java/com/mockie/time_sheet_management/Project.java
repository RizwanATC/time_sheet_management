package com.mockie.time_sheet_management;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Project {
    String Nameproject, taskName, datefrom, dateto, status, assignto;

    public Project() {

    }

    public String getNameproject() {
        return Nameproject;
    }

    public void setNameproject(String Nameproject) {
        this.Nameproject = Nameproject;
    }

    public String gettaskName() {
        return taskName;
    }

    public void settaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDatefrom() {
        return datefrom;
    }

    public void setDatefrom(String datefrom) {
        this.datefrom = datefrom;
    }

    public String getDateto() {
        return dateto;
    }

    public void setDateto(String dateto) {
        this.dateto = dateto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignto() {
        return assignto;
    }

    public void setAssignto(String assignto) {
        this.assignto = assignto;
    }

    public Project(String Nameproject, String taskName, String datefrom, String dateto, String status, String assignto) {
        this.Nameproject = Nameproject;
        this.taskName = taskName;
        this.datefrom = datefrom;
        this.dateto = dateto;
        this.status = status;
        this.assignto = assignto;
    }

}
