package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		
		PreparedStatement st = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO department(Name)");
			sb.append(" VALUES ");
			sb.append("(?)");
			
			st = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			st.setString(1, obj.getName());
			
			if (st.executeUpdate() > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
				}
				else {
					throw new DbException("Erro. Nenhuma linha inserida!");
				}
				DB.closeResultSet(rs);
			}
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		
		PreparedStatement st = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE department SET ");
			sb.append("Name = ? ");
			sb.append("WHERE Id = ?");
			
			st = conn.prepareStatement(sb.toString());
			
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.executeUpdate();
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement st = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("DELETE FROM department ");
			sb.append("WHERE Id = ?");
			
			st = conn.prepareStatement(sb.toString());
			
			st.setInt(1, id);
			
			st.executeUpdate();
		} 
		catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
		
	}

	@Override
	public Department finById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM department ");
			sb.append("WHERE Id = ?");
			
			st = conn.prepareStatement(sb.toString());
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				return instantiateDepartment(rs);
			}
			else {
				return null;
			}
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica. "+e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		return null;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT * FROM department ");
			
			st = conn.prepareStatement(sb.toString());
			rs = st.executeQuery();
			
			List<Department> deps = new ArrayList<>();
			
			while (rs.next()) {
				 deps.add(instantiateDepartment(rs));
			}
			return deps;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica. "+e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
		return null;
	}

}
