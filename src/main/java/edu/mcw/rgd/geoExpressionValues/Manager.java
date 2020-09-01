package edu.mcw.rgd.geoExpressionValues;

import edu.mcw.rgd.dao.impl.OntologyXDAO;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.impl.StrainDAO;
import edu.mcw.rgd.datamodel.pheno.Experiment;
import edu.mcw.rgd.datamodel.pheno.Sample;

import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.*;

/**
 * Created by jthota on 8/21/2019.
 */
public class Manager {
    public static void main(String[] args) throws Exception {
        String processType= args[0];
        String studyId= args[1];
        String tissueTraitId=new String();
        if(processType.equalsIgnoreCase("tissue"))
         tissueTraitId=args[2];
        switch (processType){
            case "study":
                Manager.processStudy(Integer.parseInt(studyId));
                break;
            case "tissue":
                Manager.processTissue(Integer.parseInt(studyId), tissueTraitId);
                break;
            case "default":
                System.out.println("Please verify the arguments that you entered");

        }
    }
    public static int getExperimentIdOfTrait(String traitId, int studyId) throws Exception {
        GeoExpressionDAO dao=new GeoExpressionDAO();
        List<Experiment> experiments=dao.getExperimentsByTraitAndStudy(traitId,studyId);
        if(experiments.size()>0){
            return experiments.get(0).getId();
        }
        return 0;
    }
   static void processTissue(int studyId, String traitId) throws Exception{
       GeoExpressionDAO geo=new GeoExpressionDAO();
       // int study=3069;
       int study=studyId;
       int experimentId=getExperimentIdOfTrait(traitId,studyId);
       FileWriter fos = new FileWriter("data/"+study+"_TPM_include_samples.tab");
       PrintWriter dos = new PrintWriter(fos);

       List<ExpressionExpRec> records=geo.getRecsByExperimentIdOfStudy(study, experimentId);
       System.out.println("records size: "+ records.size());

       TreeSet<Integer> samples=new TreeSet<>();
       Set<Integer> rgdIds= new HashSet<>();
       TreeMap<Integer, String> geneRgdIdMap =new TreeMap<>();

       Map<Integer, List<ExpressionExpRec>> sampleExpRecsMap=new HashMap<>();
       Set<Integer> sampleIds= new HashSet<>();
       for(ExpressionExpRec r:records){
           samples.add(r.getSampleId());
           rgdIds.add(r.getExpressedObjectRgdId());
           geneRgdIdMap.put(r.getExpressedObjectRgdId(),r.getGeneSymbol());
           if(!sampleIds.contains(r.getSampleId())){
               sampleIds.add(r.getSampleId());
               List<ExpressionExpRec> mappedRecords=new ArrayList<>();
               for(ExpressionExpRec rec:records){
                   if(rec.getSampleId()==r.getSampleId()){
                       mappedRecords.add(rec);
                   }
               }
               sampleExpRecsMap.put(r.getSampleId(), mappedRecords);
           }
       }
       System.out.println("gene symbols size:"+geneRgdIdMap.size()+"\t"+"gene rgdIds size:"+rgdIds.size()+"\t"+ "samples size:"+ samples.size());
       System.out.println("SampleExpRecsMap"+ sampleExpRecsMap.size());

       Collections.sort(new ArrayList(samples));
       Collections.sort(new ArrayList(rgdIds));

       String[][] matrix= new String[rgdIds.size()+1+11][samples.size()+2];
       matrix[0][0]="Gene_RGD_ID";
       matrix[0][1] ="Gene/Sample";


       matrix[1][1]="RS_ID";
       matrix[2][1]="STRAIN";
       matrix[3][1]="GEO_SAMPLE_ACC_ID";
       matrix[4][1]="SEX";
       matrix[5][1]="UBERON_ID";
       matrix[6][1]="TISSUE";
       matrix[7][1]="AGE_DAYS_FROM_DOB_LOW_BOUND";
       matrix[8][1]="AGE_DAYS_FROM_DOB_HIGH_BOUND";
       matrix[9][1]="NO_OF_ANIMALS";
       matrix[10][1]="BIO_SAMPLE_ID";
       matrix[11][1]="LIFE_STAGE";
       int l=1+11;
       for(Map.Entry e:geneRgdIdMap.entrySet()){
           matrix[l][1]= e.getValue().toString();
           matrix[l][0]= String.valueOf(e.getKey());
           l++;
       }

       int m=2;
       OntologyXDAO xdao=new OntologyXDAO();
       for(int s:samples){

           matrix[0][m]= String.valueOf(s);
           Sample sample=geo.getPhenoSamples(s);
           if(sample!=null){
               matrix[1][m]= sample.getStrainAccId();
               matrix[2][m]= xdao.getTerm(sample.getStrainAccId()).getTerm();
               matrix[3][m]= sample.getGeoSampleAcc();
               matrix[4][m]= sample.getSex();
               matrix[5][m]= sample.getTissueAccId();
               matrix[6][m]= xdao.getTerm(sample.getTissueAccId()).getTerm();
               matrix[7][m]= String.valueOf(sample.getAgeDaysFromLowBound());
               matrix[8][m]= String.valueOf(sample.getAgeDaysFromHighBound());
               matrix[9][m]= String.valueOf(sample.getNumberOfAnimals());
               matrix[10][m]= String.valueOf(sample.getBioSampleId());
               String lifeStage=new String();
               if(sample.getAgeDaysFromLowBound()<0){
                   lifeStage="embryonic";
               }
               if(sample.getAgeDaysFromLowBound()>=0 && sample.getAgeDaysFromLowBound()<=34){
                   lifeStage="neonatal/weanling";
               }
               if(sample.getAgeDaysFromLowBound()>34){

                   lifeStage="adult";
               }
               matrix[11][m]=lifeStage;
           }
           m++;
       }


       int i=1+11;

       for(Map.Entry e:geneRgdIdMap.entrySet()){
           int id= (int) e.getKey();
           int j=2;
           for(int s:samples){
               boolean flag=false;
               double  value=0;
               for(ExpressionExpRec rec:(List<ExpressionExpRec>) sampleExpRecsMap.get(s)){
                   if(rec.getExpressedObjectRgdId()==id){
                       //     matrix[i][j]=String.valueOf(rec.getExpressionValue());
                       //    flag=true;
                       if(rec.getExpressionValue()>value)
                           value=rec.getExpressionValue();
                   }
               }
             /*   if(!flag){
                    matrix[i][j]="-";
                }*/
               if(value>0){
                   matrix[i][j]=String.valueOf(value);
               }else{
                   matrix[i][j]="-";
               }
               j++;
           }
           i++;
       }
       System.out.println("Matrix length:"+matrix.length);
       for(int p=0;p<rgdIds.size()+1+11;p++){
           int countofZero=0;
           for(int k=0;k<samples.size()+2; k++){
               if(matrix[p][k]!=null && matrix[p][k].trim().equals("0.0")){
                   countofZero=countofZero+1;
               }
           }
           //     System.out.println("count 0f zero: "+countofZero +"\t"+matrix[p][0]);
           if(countofZero!=samples.size()) {
               for (int k = 0; k < samples.size() + 2; k++) {
                   dos.print(matrix[p][k] + "\t");
               }
               dos.print("\n");
           }
       }
       dos.close();
       fos.close();
       System.out.println(records.size()+"\t"+ samples.size()+"\t"+rgdIds.size());

   }
   static void processStudy(int studyId) throws Exception{
        GeoExpressionDAO geo=new GeoExpressionDAO();
       // int study=3069;
        int study=studyId;
        FileWriter fos = new FileWriter("data/"+study+"_TPM_include_samples.tab");
        PrintWriter dos = new PrintWriter(fos);

        List<ExpressionExpRec> records=geo.getRecs(study);
        System.out.println("records size: "+ records.size());

        TreeSet<Integer> samples=new TreeSet<>();
        Set<Integer> rgdIds= new HashSet<>();
        TreeMap<Integer, String> geneRgdIdMap =new TreeMap<>();

        Map<Integer, List<ExpressionExpRec>> sampleExpRecsMap=new HashMap<>();
        Set<Integer> sampleIds= new HashSet<>();
        for(ExpressionExpRec r:records){
            samples.add(r.getSampleId());
            rgdIds.add(r.getExpressedObjectRgdId());
            geneRgdIdMap.put(r.getExpressedObjectRgdId(),r.getGeneSymbol());
            if(!sampleIds.contains(r.getSampleId())){
                sampleIds.add(r.getSampleId());
                List<ExpressionExpRec> mappedRecords=new ArrayList<>();
                for(ExpressionExpRec rec:records){
                    if(rec.getSampleId()==r.getSampleId()){
                        mappedRecords.add(rec);
                    }
                }
                sampleExpRecsMap.put(r.getSampleId(), mappedRecords);
            }
        }
        System.out.println("gene symbols size:"+geneRgdIdMap.size()+"\t"+"gene rgdIds size:"+rgdIds.size()+"\t"+ "samples size:"+ samples.size());
        System.out.println("SampleExpRecsMap"+ sampleExpRecsMap.size());

        Collections.sort(new ArrayList(samples));
        Collections.sort(new ArrayList(rgdIds));

        String[][] matrix= new String[rgdIds.size()+1+10][samples.size()+2];
        matrix[0][0]="Gene_RGD_ID";
        matrix[0][1] ="Gene/Sample";


        matrix[1][1]="RS_ID";
        matrix[2][1]="STRAIN";
        matrix[3][1]="GEO_SAMPLE_ACC_ID";
        matrix[4][1]="SEX";
        matrix[5][1]="UBERON_ID";
        matrix[6][1]="TISSUE";
        matrix[7][1]="AGE_DAYS_FROM_DOB_LOW_BOUND";
        matrix[8][1]="AGE_DAYS_FROM_DOB_HIGH_BOUND";
        matrix[9][1]="NO_OF_ANIMALS";
        matrix[10][1]="BIO_SAMPLE_ID";

        int l=1+10;
        for(Map.Entry e:geneRgdIdMap.entrySet()){
            matrix[l][1]= e.getValue().toString();
            matrix[l][0]= String.valueOf(e.getKey());
            l++;
        }

        int m=2;
        OntologyXDAO xdao=new OntologyXDAO();
        for(int s:samples){

            matrix[0][m]= String.valueOf(s);
            Sample sample=geo.getPhenoSamples(s);
            if(sample!=null){
                matrix[1][m]= sample.getStrainAccId();
                matrix[2][m]= xdao.getTerm(sample.getStrainAccId()).getTerm();
                matrix[3][m]= sample.getGeoSampleAcc();
                matrix[4][m]= sample.getSex();
                matrix[5][m]= sample.getTissueAccId();
                matrix[6][m]= xdao.getTerm(sample.getTissueAccId()).getTerm();
                matrix[7][m]= String.valueOf(sample.getAgeDaysFromLowBound());
                matrix[8][m]= String.valueOf(sample.getAgeDaysFromHighBound());
                matrix[9][m]= String.valueOf(sample.getNumberOfAnimals());
                matrix[10][m]= String.valueOf(sample.getBioSampleId());
            }
            m++;
        }


        int i=1+10;

        for(Map.Entry e:geneRgdIdMap.entrySet()){
            int id= (int) e.getKey();
            int j=2;
            for(int s:samples){
                boolean flag=false;
                double  value=0;
                for(ExpressionExpRec rec:(List<ExpressionExpRec>) sampleExpRecsMap.get(s)){
                    if(rec.getExpressedObjectRgdId()==id){
                        //     matrix[i][j]=String.valueOf(rec.getExpressionValue());
                        //    flag=true;
                        if(rec.getExpressionValue()>value)
                            value=rec.getExpressionValue();
                    }
                }
             /*   if(!flag){
                    matrix[i][j]="-";
                }*/
                if(value>0){
                    matrix[i][j]=String.valueOf(value);
                }else{
                    matrix[i][j]="-";
                }
                j++;
            }
            i++;
        }
        System.out.println("Matrix length:"+matrix.length);
        for(int p=0;p<rgdIds.size()+1+10;p++){
            int countofZero=0;
            for(int k=0;k<samples.size()+2; k++){
                if(matrix[p][k]!=null && matrix[p][k].trim().equals("0.0")){
                    countofZero=countofZero+1;
                }
            }
            //     System.out.println("count 0f zero: "+countofZero +"\t"+matrix[p][0]);
            if(countofZero!=samples.size()) {
                for (int k = 0; k < samples.size() + 2; k++) {
                    dos.print(matrix[p][k] + "\t");
                }
                dos.print("\n");
            }
        }
        dos.close();
        fos.close();
        System.out.println(records.size()+"\t"+ samples.size()+"\t"+rgdIds.size());

    }

}
