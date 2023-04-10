package com.jetflix;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

public class VideoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 
	  protected void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  	  String requestedUrl = request.getRequestURI();
		  	   
		  	if (requestedUrl.contains("play")) {
		  		response.setContentType("image/jpeg");
	    		String regex = "/videos/play/(\\d+)";
	        	Pattern pattern = Pattern.compile(regex);
	        	Matcher matcher = pattern.matcher(requestedUrl);
	        	String videoId = "";
	        	if (matcher.matches()) {
	        	    videoId = matcher.group(1);
	        	} else {
	        	    System.out.print("No");
	        	}
		  		FileInputStream inputStream = new FileInputStream(String.format("/mnt/assets/%s/pv.mp4",videoId));
				  response.setContentType("video/mp4");
				  ServletOutputStream outputStream = response.getOutputStream();
				  byte[] buffer = new byte[4096];
				  int bytesRead;
				  while ((bytesRead = inputStream.read(buffer)) != -1) {
				    outputStream.write(buffer, 0, bytesRead);
				  }
				  inputStream.close();
				  outputStream.flush();
				  outputStream.close();
		  	} else if (requestedUrl.equals("/videos/get")) {
		  		response.setContentType("application/json");
		  	    response.setCharacterEncoding("UTF-8");
		  	    PrintWriter out = response.getWriter();
		  	  try {
		          // Connect to the MySQL database
		          Class.forName("com.mysql.cj.jdbc.Driver");
		          String url = "jdbc:mysql://192.168.163.30:3306/assetDB?useUnicode=true&characterEncoding=UTF-8";
		          String username = "video_uploader";
		          String password = "p@ssw0rd";
		          Connection conn = DriverManager.getConnection(url, username, password);

		          // Execute the SELECT query
		          String query = "SELECT * FROM asset";
		          PreparedStatement pstmt = conn.prepareStatement(query);
		          ResultSet rs = pstmt.executeQuery();
		          
		          // Convert the ResultSet to a JSONArray
		          JSONArray jsonArray = new JSONArray();
		          while (rs.next()) {
		              JSONObject jsonObj = new JSONObject();
		              jsonObj.put("id", rs.getInt("id"));
		              jsonObj.put("title", rs.getString("title"));
		              jsonObj.put("overview", rs.getString("overview"));
		              jsonObj.put("bgPath", rs.getString("bgPath"));
		              jsonObj.put("posterPath", rs.getString("posterPath"));
		              jsonObj.put("videoPath", rs.getString("videoPath"));
		              jsonArray.add(jsonObj);
		          }
		          
		          // Send the JSON response to the client
		          out.print(jsonArray);
		          out.flush();
		      } catch (Exception ex) {
		          System.out.println("An error occurred while fetching the data: " + ex.getMessage());
		      } finally {
		          out.close();
		      }
		  	} else if(requestedUrl.contains("get")) {
		  		response.setContentType("application/json");
		  	    response.setCharacterEncoding("UTF-8");
		  	    PrintWriter out = response.getWriter();

		  	    try {
		  	   // Connect to the MySQL database
			          Class.forName("com.mysql.cj.jdbc.Driver");
			          String url = "jdbc:mysql://192.168.163.30:3306/assetDB?useUnicode=true&characterEncoding=UTF-8";
			          String username = "video_uploader";
			          String password = "p@ssw0rd";
			          Connection conn = DriverManager.getConnection(url, username, password);

			          // Execute the SELECT query
			          
			          String query = "SELECT * FROM asset WHERE REPLACE(title,' ','') LIKE ?";
			          
			          PreparedStatement selectStmt = conn.prepareStatement(query);
			          String keyword = URLDecoder.decode(requestedUrl.substring(12),"UTF-8");
			          selectStmt.setString(1, "%" + keyword + "%");
			          ResultSet rs = selectStmt.executeQuery();
			        
			          System.out.println(rs.getRow());	
			          // Convert the ResultSet to a JSONArray
			          JSONArray jsonArray = new JSONArray();
			          
	        	      while (rs.next()) {
	        	    	  JSONObject jsonObj = new JSONObject();
	        	    	  jsonObj.put("id", rs.getInt("id"));
			              jsonObj.put("title", rs.getString("title"));
			              jsonObj.put("overview", rs.getString("overview"));
			              jsonObj.put("bgPath", rs.getString("bgPath"));
			              jsonObj.put("posterPath", rs.getString("posterPath"));
			              jsonObj.put("videoPath", rs.getString("videoPath"));
			              jsonArray.add(jsonObj);
			          }
    
			          // Send the JSON response to the client
			          out.print(jsonArray);
			          out.flush();
		  	    } catch(Exception ex) {
		  	    	System.out.println("An error occurred while fetching the data: " + ex.getMessage());
		  	    }finally {
		  	    	out.close();
		  	    	}
		  	}
	  }
	  
	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException {
		  
		  	request.setCharacterEncoding("UTF-8");
		    response.setContentType("text/plain");
		    PrintWriter out = response.getWriter();
	
		    try {
	    	Part backgroundPart = request.getPart("background");
	        Part posterPart = request.getPart("poster");
	        Part trailerPart = request.getPart("trailer");
	      
	        if (backgroundPart == null || posterPart == null || trailerPart == null) {
	            throw new ServletException("Please select all files.");
	          }
	          // ADD TO MYSQL DATABASE
	          String url = "jdbc:mysql://192.168.163.30:3306/assetDB?useUnicode=true&characterEncoding=UTF-8";
	          String username = "video_uploader";
	          String password = "p@ssw0rd";
	          String title = request.getParameter("title");
	          String overview = request.getParameter("overview");
	          
	          
	          Class.forName("com.mysql.cj.jdbc.Driver");
	          try (Connection connection = DriverManager.getConnection(url, username, password)) {
	              String sql = "INSERT INTO asset (title, overview) VALUES (?, ?)";
	              PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	              stmt.setString(1, title);
	              stmt.setString(2, overview);
	              stmt.executeUpdate();
	              ResultSet rs = stmt.getGeneratedKeys();
	              int id = rs.next() ? rs.getInt(1) : 0;
	              
	              String bgPath = "/mnt/assets/" + id + "/";
	              String posterPath = "/mnt/assets/" + id + "/";
	              String videoPath = "/mnt/assets/" + id + "/";
	              
	              // FILE DOWNLOAD
	              uploadFile(backgroundPart, bgPath, "bg.jpg");
	              uploadFile(posterPart, posterPath, "post.jpg");
	              uploadFile(trailerPart, videoPath,"pv.mp4");   
	              
	              // UPDATE MYSQL WITH PATH FOR INSERT PATH
	              sql = "UPDATE asset SET bgPath = ?, posterPath = ?, videoPath = ? WHERE id = ?";
	              PreparedStatement updateStmt = connection.prepareStatement(sql);
	              updateStmt.setString(1, bgPath);
	              updateStmt.setString(2, posterPath);
	              updateStmt.setString(3, videoPath);
	              updateStmt.setInt(4, id);
	              updateStmt.executeUpdate();
	              
	              out.print("File upload successful.");
	            } catch (SQLException ex) {
	              System.out.println("An error occurred while saving the video: " + ex.getMessage());
	            }  
		    } catch (Exception e) {
		      out.print("File upload failed: " + e.getMessage());
		    }
		  }
	  	
	  		private void uploadFile(final Part part, String path, String name) throws IOException {
			      File file = new File(path + name);
			      File parentDir = file.getParentFile();
			      if (!parentDir.exists()) {
			          parentDir.mkdirs();
			      }
			      part.write(file.getAbsolutePath());
			      System.out.print("File uploaded successfully!");
	  		}
		  private String getFileName(final Part part) {
		    final String partHeader = part.getHeader("content-disposition");
		    for (String content : partHeader.split(";")) {
		      if (content.trim().startsWith("filename")) {
		        return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
		      }
		    }
		    return null;
		  }
}
	