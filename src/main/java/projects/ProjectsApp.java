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
		"1) Add a project"
	);
	// @formatter: on
	
	private ProjectService projectService = new ProjectService();
	
	private Scanner scanner = new Scanner(System.in);
	
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
	
	// TODO fix this. Just putting this in here so it compiles
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
