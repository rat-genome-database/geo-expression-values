package edu.mcw.rgd.geoExpressionValues;

import edu.mcw.rgd.dao.AbstractDAO;
import edu.mcw.rgd.dao.DataSourceFactory;

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
    public List<ExpressionExpRec> getRecs() throws SQLException {
        String sql="select g.rgd_id, g.gene_symbol, eer.sample_id, gv.expression_value from gene_expression_exp_record eer, experiment e,genes g, " +
                "gene_expression_values gv " +
                "where e.experiment_id=eer.experiment_id " +
                "and gv.gene_expression_exp_record_id =eer.gene_expression_exp_record_id " +
                "and g.rgd_id=gv.expressed_object_rgd_id " +
                "and e.study_id=3012";

        Connection connection= null;
        PreparedStatement stmt=null;
        ResultSet rs= null;
        List<ExpressionExpRec> records= new ArrayList<>();
        try{
            connection = DataSourceFactory.getInstance().getDataSource().getConnection();
            stmt =connection.prepareStatement(sql);
            rs=stmt.executeQuery();

            while(rs.next()){
                ExpressionExpRec rec=new ExpressionExpRec();
                rec.setSampleId(rs.getInt("sample_id"));
                rec.setGeneSymbol(rs.getString("gene_symbol"));
                rec.setExpressedObjectRgdId(rs.getInt("rgd_id"));
                rec.setExpressionValue(rs.getFloat("expression_value"));
                records.add(rec);
            }
            rs.close();
            stmt.close();
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) {
                    stmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            }catch (SQLException sqlEx){
                sqlEx.printStackTrace();
            }
        }
        return records;
    }
}
