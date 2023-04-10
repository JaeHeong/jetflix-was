package com.jetflix;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String requestedUrl = request.getRequestURI();
 
    	if (requestedUrl.contains("bg")) {
    		response.setContentType("image/jpeg");
    		String regex = "/images/bg/(\\d+)";
        	Pattern pattern = Pattern.compile(regex);
        	Matcher matcher = pattern.matcher(requestedUrl);
        	String videoId = "";
        	if (matcher.matches()) {
        	    videoId = matcher.group(1);
        	    System.out.print(videoId);
        	} else {
        	    System.out.print("No");
        	}
       	 	
        	InputStream inputStream = new FileInputStream(String.format("/mnt/assets/%s/bg.jpg",videoId));
        	OutputStream outputStream = response.getOutputStream();
        	byte[] buffer = new byte[4096];
        	int bytesRead;
        	while ((bytesRead = inputStream.read(buffer)) != -1) {
        	  outputStream.write(buffer, 0, bytesRead);
        	}
        	inputStream.close();
        	outputStream.close();
        } else if (requestedUrl.contains("poster")) {
        	response.setContentType("image/jpeg");
        	String regex = "/images/poster/(\\d+)";
        	Pattern pattern = Pattern.compile(regex);
        	Matcher matcher = pattern.matcher(requestedUrl);
        	String videoId = "";
        	if (matcher.matches()) {
        	    videoId = matcher.group(1);
        	    System.out.print(videoId);
        	} else {
        	    System.out.print("No");
        	}
          	
        	InputStream inputStream = new FileInputStream(String.format("/mnt/assets/%s/post.jpg",videoId));
        	OutputStream outputStream = response.getOutputStream();
        	byte[] buffer = new byte[4096];
        	int bytesRead;
        	while ((bytesRead = inputStream.read(buffer)) != -1) {
        	  outputStream.write(buffer, 0, bytesRead);
        	}
        	inputStream.close();
        	outputStream.close();
        }
    }
}