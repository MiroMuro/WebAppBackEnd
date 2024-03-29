package com.Taskmanager.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Taskmanager.service.TaskService;
import com.Taskmanager.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Taskmanager.entity.Task;
import com.Taskmanager.entity.User;
import com.Taskmanager.repository.UserRepository;
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	
	private  TaskService taskservice;
	
	@Autowired
	public TaskController(TaskService taskservice) {
		this.taskservice = taskservice;
	}
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	// CRUD Create, read, update, Delete
	
	//Read all
	@GetMapping
	public ResponseEntity<List<Task>> getAllTasks(){
		List<Task> tasks =  taskservice.getAllTasks();
		return ResponseEntity.ok().body(tasks);
	}
	
	//Read one
	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
		return taskservice.getTaskById(id)
				.map(task -> ResponseEntity.ok().body(task))
				.orElse(ResponseEntity.notFound().build());
	}
	
	//Create
	@PostMapping
	public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam(name ="id") Long id) throws JsonProcessingException {
		
		
		User user = userService.findUserByID(id);
		
		Task xd = new Task(task.getTitle(),task.getDescription(),task.isCompleted(),user);
		
		task.setUser(user);
		
		List<Task> blogs = user.getTasklist();
		
		Task createdTask = taskservice.SaveTask(task);
		
		blogs.add(createdTask);
		System.out.println("USER FOUND HERE: "+user);
		user.setTaskList(blogs);
		
		String itemJson = new ObjectMapper().writeValueAsString(createdTask.getUser());
		
		userRepo.save(user);
		
		User user2 = userService.findUserByID(id);
		System.out.println(user2);
		System.out.println(itemJson);


		HttpHeaders responseHeaders = new HttpHeaders();
		//Add the url of the created task as an header to the response.
		responseHeaders.set("Location", "http://localhost:8080/api/tasks/"+task.getId()+"");

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.headers(responseHeaders)
				.body(createdTask);
	}
	//Update
	@PutMapping("/{id}")
	public ResponseEntity<String>updateTask(@RequestBody Task task, @PathVariable Long id) {
		task.setId(id);
		
		
		return taskservice.updateTask(task)
			   .map(returnedTask -> ResponseEntity.ok().body(returnedTask.toString()+" Updated succesfully!"))
			   .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task with given id: "+id+" not found."));
	}
	//Delete
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteTask(@PathVariable Long id) {
		//return ResponseEntity.ok().body("Task deleted succesfully!");
		
		if(taskservice.deleteTask(id)) {
			return ResponseEntity.ok().body("Task deleted succesfully!");
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("Couldn't delete task with id: "+id+".\nTask not found."));
		
		
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleException(Exception e){
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
}
