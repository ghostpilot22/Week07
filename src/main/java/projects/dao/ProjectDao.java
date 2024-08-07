package projects.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase 
{
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) 
	{
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on
		try(Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql))
			{
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				stmt.executeUpdate();
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				project.setProjectId(projectId);
				return project;
			}
			catch(Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e)
		{ throw new DbException(e); }
	}

	public List<Project> fetchAllProjects() 
	{
		String sql = "SELECT * FROM project ORDER BY project_name";
		try(Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql))
			{
				try(ResultSet rs = stmt.executeQuery())
				{
					List<Project> projects = new LinkedList<Project>();
					while(rs.next())
					{ projects.add(extract(rs, Project.class)); }
					return projects;
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new DbException(e);
				}
			}
			catch(Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e)
		{ throw new DbException(e); }
	
	}

	public Optional<Project> fetchProjectById(Integer projectId) 
	{
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection())
		{
			startTransaction(conn);
			
			try
			{
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql))
				{
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery())
					{
						if(rs.next())
						project = (extract(rs, Project.class));
					}
				}
				
				if(Objects.nonNull(project))
				{
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
					
				commitTransaction(conn);
				return Optional.ofNullable(project);
				
			}
			catch(Exception e)
			{
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}
		catch(SQLException e)
		{
			throw new DbException(e); 
		 }
	}

	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException
	{
		// @formatter:off
		String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c "
		+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) WHERE project_id = ?";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql))
		{
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery())
			{
				List<Category> categories = new LinkedList<>();
				while(rs.next())
					categories.add(extract(rs, Category.class));
				return categories;
			}
		}
	}

	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException
	{
		// @formatter:off
		String sql = "SELECT s.* FROM " + STEP_TABLE + " s WHERE project_id = ?";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql))
		{
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery())
			{
				List<Step> steps = new LinkedList<>();
				while(rs.next())
					steps.add(extract(rs, Step.class));
				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException
	{
		// @formatter:off
		String sql = "SELECT m.* FROM " + MATERIAL_TABLE + " m WHERE project_id = ?";
		// @formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql))
		{
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery())
			{
				List<Material> materials = new LinkedList<>();
				while(rs.next())
					materials.add(extract(rs, Material.class));
				return materials;
			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
	    String sql = """
	        UPDATE %s
	        SET project_name = ?, estimated_hours = ?, actual_hours = ?, difficulty = ?, notes = ?
	        WHERE project_id = ?""".formatted(PROJECT_TABLE);

	    try (Connection conn = DbConnection.getConnection()) {  // conn is declared outside
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            startTransaction(conn);

	            stmt.setString(1, project.getProjectName());
	            stmt.setBigDecimal(2, project.getEstimatedHours());
	            stmt.setBigDecimal(3, project.getActualHours());
	            stmt.setInt(4, project.getDifficulty());
	            stmt.setString(5, project.getNotes());
	            stmt.setInt(6, project.getProjectId());

	            int rowsAffected = stmt.executeUpdate();
	            commitTransaction(conn);
	            return rowsAffected == 1;

	        } catch (SQLException e) {
	            rollbackTransaction(conn);  // Now conn is accessible
	            throw new DbException(e); 
	        } 
	    } catch (SQLException e) {  // Additional catch for conn
	        throw new DbException(e);
	    }
	}

	public boolean deleteProject(Integer projectId) 
	{
		String sql = """
		        DELETE FROM project WHERE project_id=?""".formatted(PROJECT_TABLE);

	    try (Connection conn = DbConnection.getConnection()) {  // conn is declared outside
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            startTransaction(conn);

	            stmt.setInt(1, projectId);

	            int rowsAffected = stmt.executeUpdate();
	            commitTransaction(conn);
	            return rowsAffected == 1;

	        } catch (SQLException e) {
	            rollbackTransaction(conn);  // Now conn is accessible
	            throw new DbException(e); 
	        } 
	    } catch (SQLException e) {  // Additional catch for conn
	        throw new DbException(e);
	    }
	}
}
