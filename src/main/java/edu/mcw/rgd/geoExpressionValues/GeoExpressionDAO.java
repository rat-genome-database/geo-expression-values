package edu.mcw.rgd.geoExpressionValues;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;
import edu.mcw.rgd.dao.impl.PhenominerDAO;
import edu.mcw.rgd.dao.spring.ExperimentQuery;
import edu.mcw.rgd.datamodel.pheno.Experiment;
import edu.mcw.rgd.datamodel.pheno.Sample;
import org.springframework.jdbc.core.SqlParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jthota on 8/21/2019.
 */
public class GeoExpressionDAO extends AbstractDAO {
    public List<ExpressionExpRec> getRecs(int studyId) throws SQLException {
        String sql = "select g.rgd_id, g.gene_symbol, eer.sample_id, gv.expression_value from " +
                "gene_expression_exp_record eer, experiment e,genes g, " +
                "gene_expression_values gv , rgd_ids r " +
                "where e.experiment_id=eer.experiment_id " +
                "and gv.gene_expression_exp_record_id =eer.gene_expression_exp_record_id " +
                "and g.rgd_id=gv.expressed_object_rgd_id " +
                "and r.rgd_id=g.rgd_id " +
                "and r.object_status='ACTIVE'" +
                //   "and e.study_id=3013";
                "and e.study_id=" + studyId +
                "and gv.expression_unit='TPM' "
                //    "and g.rgd_id=2257"
                ;

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ExpressionExpRec> records = new ArrayList<>();
        try {
            connection = DataSourceFactory.getInstance().getDataSource().getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ExpressionExpRec rec = new ExpressionExpRec();
                rec.setSampleId(rs.getInt("sample_id"));
                rec.setGeneSymbol(rs.getString("gene_symbol"));
                rec.setExpressedObjectRgdId(rs.getInt("rgd_id"));
                rec.setExpressionValue(rs.getFloat("expression_value"));
                records.add(rec);
            }
            rs.close();
            stmt.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        }
        return records;
    }

    public List<ExpressionExpRec> getRecsByExperimentIdOfStudy(int studyId, int experimentId) throws SQLException {
        String sql = "select g.rgd_id, g.gene_symbol, eer.sample_id, gv.expression_value from " +
                "gene_expression_exp_record eer, experiment e,genes g, " +
                "gene_expression_values gv , rgd_ids r " +
                "where e.experiment_id=eer.experiment_id " +
                "and gv.gene_expression_exp_record_id =eer.gene_expression_exp_record_id " +
                "and g.rgd_id=gv.expressed_object_rgd_id " +
                "and r.rgd_id=g.rgd_id " +
                "and r.object_status='ACTIVE'" +
                //   "and e.study_id=3013";
                "and e.study_id=" + studyId +
                " and e.experiment_id=" + experimentId +
                "and gv.expression_unit='TPM' "
                //    "and g.rgd_id=2257"
                ;

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<ExpressionExpRec> records = new ArrayList<>();
        try {
            connection = DataSourceFactory.getInstance().getDataSource().getConnection();
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ExpressionExpRec rec = new ExpressionExpRec();
                rec.setSampleId(rs.getInt("sample_id"));
                rec.setGeneSymbol(rs.getString("gene_symbol"));
                rec.setExpressedObjectRgdId(rs.getInt("rgd_id"));
                rec.setExpressionValue(rs.getFloat("expression_value"));
                records.add(rec);
            }
            rs.close();
            stmt.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
            }
        }
        return records;
    }

    public Sample getPhenoSamples(int sampleId) throws Exception {

        String sql = "select * " +

                " from SAMPLE sa where sa.SAMPLE_ID= " + sampleId;

        PhenominerSampleQuery q = new PhenominerSampleQuery(this.getDataSource(), sql);
        List<Sample> samples = q.execute();
        if (samples.size() > 0) {
            return samples.get(0);

        }
        return null;
    }

    public List<Experiment> getExperimentsByTraitAndStudy(String traitId, int studyId) throws Exception {
        String query = "SELECT * from experiment where study_id=? and trait_ont_id=?";
        ExperimentQuery sq = new ExperimentQuery(this.getDataSource(), query);

        return execute(sq,studyId, traitId);
    }



}
