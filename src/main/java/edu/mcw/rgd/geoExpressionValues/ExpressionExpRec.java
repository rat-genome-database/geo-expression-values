package edu.mcw.rgd.geoExpressionValues;

/**
 * Created by jthota on 8/21/2019.
 */
public class ExpressionExpRec {
    private int expressionExpRecId;
    private   int experimentId;
    private   int sampleId;
    private int expressedObjectRgdId;
    private float expressionValue;
    private int mapKey;
    private String geneSymbol;


    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public int getExpressedObjectRgdId() {
        return expressedObjectRgdId;
    }

    public void setExpressedObjectRgdId(int expressedObjectRgdId) {
        this.expressedObjectRgdId = expressedObjectRgdId;
    }

    public float getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(float expressionValue) {
        this.expressionValue = expressionValue;
    }

    public int getMapKey() {
        return mapKey;
    }

    public void setMapKey(int mapKey) {
        this.mapKey = mapKey;
    }

    public int getExpressionExpRecId() {
        return expressionExpRecId;
    }

    public void setExpressionExpRecId(int expressionExpRecId) {
        this.expressionExpRecId = expressionExpRecId;
    }

    public int getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }
}
