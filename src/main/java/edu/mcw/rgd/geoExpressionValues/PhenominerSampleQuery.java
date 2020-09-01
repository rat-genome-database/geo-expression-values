package edu.mcw.rgd.geoExpressionValues;

import edu.mcw.rgd.datamodel.pheno.Sample;
import org.springframework.jdbc.object.MappingSqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PhenominerSampleQuery extends MappingSqlQuery {

    public PhenominerSampleQuery(DataSource ds ,String sql){
        super(ds,sql);
    }
    @Override
    protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Sample s= new Sample();
        s.setId(rs.getInt("sample_id"));
        s.setStrainAccId(rs.getString("STRAIN_ONT_ID"));
        s.setAgeDaysFromHighBound(rs.getInt("AGE_DAYS_FROM_DOB_HIGH_BOUND"));
        s.setAgeDaysFromLowBound(rs.getInt("AGE_DAYS_FROM_DOB_LOW_BOUND"));
        s.setBioSampleId(rs.getString("BIOSAMPLE_ID"));
        s.setGeoSampleAcc(rs.getString("GEO_SAMPLE_ACC"));
        s.setNumberOfAnimals(rs.getInt("NUMBER_OF_ANIMALS"));
        s.setSex(rs.getString("SEX"));
        s.setTissueAccId(rs.getString("TISSUE_ONT_ID"));

        return s;
    }
}
