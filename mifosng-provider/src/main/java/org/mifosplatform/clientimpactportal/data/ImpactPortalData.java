package org.mifosplatform.clientimpactportal.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ImpactPortalData {


    @SuppressWarnings("unused")
    private final int id;

    @SuppressWarnings("unused")
    private final Date dateCaptured;

    @SuppressWarnings("unused")
    private final String dataPoint;

    @SuppressWarnings("unused")
    private final String dataPointLabel;

    @SuppressWarnings("unused")
    private final String value;
    
    private ArrayList<cachedDataResult> resultValues;
    
    


    public int getId() {
        return id;
    }

    public Date getDateCaptured() {
        return dateCaptured;
    }

    public String getDataPoint() {
        return dataPoint;
    }

    public String getDataPointLabel() {
        return dataPointLabel;
    }

    public String getValue() {
        return value;
    }





    public ImpactPortalData(final int id, final Date dateCaptured, final String dataPoint, final String dataPointLabel, final String value){

        this.id=id ;
        this.dateCaptured=dateCaptured;
        this.dataPoint=dataPoint;
        this.dataPointLabel=dataPointLabel;
        this.value=value;
    }

    public HashMap getValues(){

        HashMap tempValues=new HashMap();
        String key;
        String values;


        if(!value.isEmpty()){
            StringTokenizer tokenizer = new StringTokenizer(value, ",");
            while(tokenizer.hasMoreTokens()){
                
                StringTokenizer tokenizer2 = new StringTokenizer(tokenizer.nextToken(), "*");
                while(tokenizer2.hasMoreTokens()){
                    StringTokenizer tokenizer3 = new StringTokenizer(tokenizer2.nextToken(), "=");
                    //savings amount,50
                    tokenizer3.nextToken();//skinping the datapoint name
                    values=tokenizer3.nextToken();
                }
            }
        }
        tempValues.put("Loan amount", 58);
        tempValues.put("Loan amount1", 100);

        return tempValues;
    }
    
    public ArrayList<cachedDataResult> getResultValues(){

        ArrayList<cachedDataResult> results=new ArrayList<cachedDataResult>();

        
        cachedDataResult cr2=new cachedDataResult();

        if(!value.isEmpty()){
            StringTokenizer tokenizer = new StringTokenizer(value, ",");

            while(tokenizer.hasMoreTokens()){
                StringTokenizer tokenizer2 = new StringTokenizer(tokenizer.nextToken(), "*");
                cachedDataResult cr=new cachedDataResult();
                ArrayList<String> tempResults=new ArrayList<String>();
                while(tokenizer2.hasMoreTokens()){
                    StringTokenizer tokenizer3 = new StringTokenizer(tokenizer2.nextToken(), "=");


                        while(tokenizer3.hasMoreTokens()){

                            tokenizer3.nextToken();//skinping the datapoint name

                            tempResults.add(tokenizer3.nextToken());

                            }


                }
                cr.setDataPointValues(tempResults);
                results.add(cr);
            }


        }
        
        
        
        


        return results;
    }
}
