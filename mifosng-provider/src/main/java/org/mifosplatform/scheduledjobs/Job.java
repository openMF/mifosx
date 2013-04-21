package org.mifosplatform.scheduledjobs;

import java.util.Date;

public class Job {
    public Job(String jobParameter) {
        this.jobParameter = jobParameter;
    }

    public void execute(){
        System.out.println(new Date()+ " "+jobParameter);
    }

    private final String jobParameter;

}
