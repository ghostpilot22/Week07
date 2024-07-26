package projects;

import java.math.BigDecimal;
import java.util.*;

import projects.entity.Project;
import projects.exception.DbException;
//import projects.dao.DbConnection;
import projects.service.ProjectService;

public class ProjectsApp 
{
	
	// @formatter: off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project"
	);
	// @formatter: on
	
	private ProjectService projectService = new ProjectService();
	
	private Scanner scanner = new Scanner(System.in);
	
	private Project curProject;
	
	public static void main(String[] args) 
	{
		//DbConnection.getConnection();
		new ProjectsApp().processUserSelections();

	}

	private void processUserSelections() 
	{
		boolean done = false;
		while(!done)
		{
			try
			{
				int selection = getUserSelection();
				switch(selection)
				{
					case -1:
						done = exitMenu();
						break;
					case 1:
						createProject();
						break;
					case 2: 
						listProjects();
						break;
					case 3:
						selectProject();
						break;
					case 4:
						updateProjectDetails();
						break;
					case 5:
						deleteProject();
						break;
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
				}
			}
			catch(Exception e)
			{
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}
	
	private void deleteProject() 
	{
		listProjects();
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " has been deleted.");
		if(curProject.getProjectId() == projectId) curProject = null;
	}

	private void updateProjectDetails() 
	{
		if(Objects.isNull(curProject)) 
		{
			System.out.println("Please select a project."); 
			return;
		}
		Project project = new Project();
		
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
		
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
		
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
		
		project.setProjectId(curProject.getProjectId());
		projectService.modifyProjectDetails(project);
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	private void selectProject() 
	{
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		curProject = null; // Unselects current project
		curProject = projectService.fetchProjectById(projectId);
		// This throws exception if invalid project id entered
		if(Objects.isNull(curProject)) 
		{ System.out.println("Invalid project ID selected."); }
	}

	private void listProjects() 
	{
		List<Project> projects = projectService.fetchAllProjects();
		System.out.println("\nProjects:");
		projects.forEach(project -> 
			System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}

	private int getUserSelection()
	{
		printOperations();
		Integer input = getIntInput("Enter a menu selection");
		return Objects.isNull(input) ? -1 : input;
	}
	
	private void printOperations()
	{
		System.out.println("\nThese are the available selections. Press the Enter key to quit.");
		for(String op : operations)
			System.out.println("  " + op);
		if(Objects.isNull(curProject)) 
		{ System.out.println("\nYou are not working with a project."); }
		else
		{ System.out.println("\nYou are working with project: " + curProject); }
	}
	
	private Integer getIntInput(String prompt)
	{
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) return null;
		
		try
		{
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e)
		{
			throw new DbException(input + " is not a valid number.");
		}
	}
	
	private BigDecimal getDecimalInput(String prompt)
	{
		String input = getStringInput(prompt);
		if(Objects.isNull(input)) return null;
		
		try
		{
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e)
		{
			throw new DbException(input + " is not a valid decimal number.");
		}
	}
	
	private String getStringInput(String prompt)
	{
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		return input.isBlank() ? null : input.trim();
	}
	
	
	private boolean exitMenu()
	{
		System.out.println("Exiting the menu.");
		return true;
	}
	
	private void createProject()
	{
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");
		
		Project project = new Project();
		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}
	
}
