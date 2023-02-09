package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;

import projects.entity.Project;
import provided.util.DaoBase;
import projects.dao.DbConnection;
import projects.exception.DbException;

public class ProjectDao extends DaoBase {
	
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERINAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
				+ "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+ "VALUES "
				+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {
			// start transaction
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				Integer recipeId = getLastInsertId(conn, PROJECT_TABLE);
				
				commitTransaction(conn);
				
				project.setProjectId(recipeId);
				return project;
			}
			catch (Exception e) {
				// roll back if exception
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}
	
}
