package edu.mcw.rgd.geoExpressionValues;

import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.*;

/**
 * Created by jthota on 8/21/2019.
 */
public class Manager {
    public static void main(String[] args) throws Exception {
      GeoExpressionDAO geo=new GeoExpressionDAO();

        FileWriter fos = new FileWriter("data/tab-file.tab");
        PrintWriter dos = new PrintWriter(fos);

        List<ExpressionExpRec> records=geo.getRecs();
        System.out.println("records size: "+ records.size());

        Set<Integer> samples=new HashSet<>();
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

        String[][] matrix= new String[rgdIds.size()+1][samples.size()+2];
        matrix[0][1] ="Gene/Sample";
        matrix[0][0]="Gene_RGD_ID";
        int l=1;
        for(Map.Entry e:geneRgdIdMap.entrySet()){
            matrix[l][1]= e.getValue().toString();
            matrix[l][0]= String.valueOf(e.getKey());
            l++;
        }
        System.out.println("Matrix Length after rgdIds:"+matrix.length);
        int m=2;
        for(int s:samples){

            matrix[0][m]= String.valueOf(s);

            m++;
        }
        System.out.println("Matrix Length after samples:"+matrix.length);

        int i=1;

        for(Map.Entry e:geneRgdIdMap.entrySet()){
            int id= (int) e.getKey();
            int j=2;
            for(int s:samples){
                boolean flag=false;
                for(ExpressionExpRec rec:(List<ExpressionExpRec>) sampleExpRecsMap.get(s)){
                    if(rec.getExpressedObjectRgdId()==id){
                        matrix[i][j]=String.valueOf(rec.getExpressionValue());
                        flag=true;
                    }
                }
                if(!flag){
                    matrix[i][j]="-";
                }
                j++;
            }
            i++;
        }
        System.out.println("Matrix length:"+matrix.length);
        for(int p=0;p<rgdIds.size()+1;p++){
            int countofZero=0;
            for(int k=0;k<samples.size()+2; k++){
                if(matrix[p][k].trim().equals("0.0")){
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
