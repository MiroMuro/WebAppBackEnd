package com.Taskmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import com.Taskmanager.service.TaskService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class TaskmanagerApplication {
	
	//Initialize some data for the database
	@Autowired
	private DataLoader dataLoader;
	public static void main(String[] args) throws Exception {
		SpringApplication.run(TaskmanagerApplication.class, args);
	
	}
	

}
