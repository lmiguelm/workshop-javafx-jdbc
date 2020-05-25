package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
		
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {

		PreparedStatement st = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO seller(Name, Email, BirthDate, BaseSalary, DepartmentId) ");
			sb.append("Values ");
			sb.append("(?, ?, ?, ?, ?)");
			
			st = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
						
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			if (st.executeUpdate() > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
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
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller obj) {

		PreparedStatement st = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("UPDATE seller SET ");
			sb.append("Name = ?,");
			sb.append("Email = ?,");
			sb.append("BirthDate = ?,");
			sb.append("BaseSalary = ?,");
			sb.append("DepartmentId = ? ");
			sb.append("WHERE Id = ?");
			
			st = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
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
			sb.append("DELETE FROM seller ");
			sb.append("WHERE Id = ?");
			
			st = conn.prepareStatement(sb.toString());
			st.setInt(1, id);
			
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Seller finById(Integer id) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT seller.*, department.Name as DepName ");
			sb.append("FROM seller INNER JOIN department ");
			sb.append("ON seller.DepartmentId = department.Id ");
			sb.append("WHERE seller.Id = ?");
			
			st = conn.prepareStatement(sb.toString());
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, dep);
				return seller;
				
			}else{
				return null;
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
			
		}
		return null;
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		seller.setDepartment(dep);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT seller.*, department.Name as DepName ");
			sb.append("FROM seller INNER JOIN department ");
			sb.append("ON seller.DepartmentId = department.Id ");
			sb.append("ORDER BY seller.Name");
			
			st = conn.prepareStatement(sb.toString());
			
			rs = st.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("Id"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
				}
				Seller seller = instantiateSeller(rs, dep);
				sellers.add(seller);
			}
			return sellers;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
		}	
		return null;
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT seller.*, department.Name as DepName ");
			sb.append("FROM seller INNER JOIN department ");
			sb.append("ON seller.DepartmentId = department.Id ");
			sb.append("WHERE department.Id = ? ");
			sb.append("ORDER BY seller.Name");
			
			st = conn.prepareStatement(sb.toString());
			st.setInt(1, department.getId());
			
			rs = st.executeQuery();
			
			List<Seller> sellers = new ArrayList<>();
			
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {					
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller seller = instantiateSeller(rs, dep);
				sellers.add(seller);
			}
			return sellers;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		catch (Exception e) {
			System.out.println("Falha genérica: "+e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
			
		}
		return null;		
	}
}
