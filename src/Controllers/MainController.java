package Controllers;

import Classes.Project;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class MainController
{
	// Data fields
	@FXML
	private TextField status;

	@FXML
	private JFXButton solve;

	@FXML
	private TableView<ObservableList> table;

	@FXML
	private JFXTextField maxH;

	@FXML
	private JFXTextField hrsNeed;

	@FXML
	private JFXTextField oPerc;

	private int totalHours;
	private ArrayList<Project> projects;
	private ObservableList<ObservableList> data;

	// A method to initialize the UI objects
	public void initialize()
	{
		solve.setDisable(true);
		initialize_table();
	}

	// Initialize the table with the appropriate columns
	private void initialize_table()
	{
		TableColumn<ObservableList, String> name = new TableColumn<>("Name");
		name.setMinWidth(80);
		name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0).toString()));

		TableColumn<ObservableList, String> hNeeded = new TableColumn<>("H. needed");
		hNeeded.setMinWidth(20);
		hNeeded.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1).toString()));

		TableColumn<ObservableList, String> perc = new TableColumn<>("Percentage");
		perc.setMinWidth(20);
		perc.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(2).toString()));

		TableColumn<ObservableList, String> dens = new TableColumn<>("Density");
		dens.setMinWidth(10);
		dens.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(3).toString()));

		TableColumn<ObservableList, String> hSpent = new TableColumn<>("H. spent");
		hSpent.setMinWidth(10);
		hSpent.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(4).toString()));

		TableColumn<ObservableList, String> percAch = new TableColumn<>("Percentage achieved");
		percAch.setMinWidth(140);
		percAch.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(5).toString()));

		table.getColumns().addAll(name, hNeeded, perc, dens, hSpent, percAch);
	}

	// A method to choose the project file
	public void open()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Projects File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		File file1 = fileChooser.showOpenDialog(table.getScene().getWindow());

		if(file1 != null)
			readFile(String.valueOf(file1));
	}

	// A method to read the file's contents
	public void readFile(String fileName)
	{
		projects = new ArrayList<>();
		File file = new File(fileName);
		Scanner input = null;
		int totalHrsNeeded = 0;
		String str = null;

		try
		{
			input = new Scanner(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}

		try
		{
			str = input.nextLine();
			input.nextLine();
			totalHours = Integer.parseInt(str.split(": ")[1].split(" ")[0]);
			maxH.setText(totalHours + " hour(s)");
		}
		catch(java.lang.ArrayIndexOutOfBoundsException ex)
		{
			maxH.setText("");
			hrsNeed.setText("");
			oPerc.setText("");
			status.setText("File is invalid!");
			table.getItems().clear();
			return;
		}
		catch(java.util.NoSuchElementException ex)
		{
			maxH.setText("");
			hrsNeed.setText("");
			oPerc.setText("");
			status.setText("File is invalid!");
			table.getItems().clear();
			return;
		}

		while(input.hasNextLine())
		{
			try
			{
				String name;
				int hours, weight;

				str = input.nextLine();
				String[] data = str.split(" - ");

				name = data[0];
				hours = Integer.parseInt(data[1].split(" ")[0]);
				weight = Integer.parseInt(data[2].split("%")[0]);

				totalHrsNeeded += hours;

				projects.add(new Project(name, hours, weight));
			}
			catch(java.lang.ArrayIndexOutOfBoundsException ex)
			{
			}
		}

		input.close();

		oPerc.setText("");
		hrsNeed.setText(totalHrsNeeded + " hour(s)");
		solve.setDisable(false);
		initializeTableData();
		status.setText("File loaded successfully!");
	}

	// Initialize the table with initial data
	public void initializeTableData()
	{
		table.getItems().clear();

		//Collections.sort(projects);
		data = FXCollections.observableArrayList();
		NumberFormat nf = NumberFormat.getInstance();
		ObservableList<String> row = null;

		nf.setMaximumFractionDigits(4);

		for(int i = 0; i < projects.size(); i++)
		{
			row = FXCollections.observableArrayList();

			row.add(projects.get(i).getName());
			row.add(projects.get(i).getHoursNeeded() + "");
			row.add(projects.get(i).getWeight() + "%");
			row.add(nf.format(projects.get(i).getDensity()) + "");
			row.add("");
			row.add("");

			data.add(row);
		}

		table.setItems(data);
	}

	// A method to solve the problem
	public void solve()
	{
		data = FXCollections.observableArrayList();
		NumberFormat nf = NumberFormat.getInstance();
		ObservableList<String> row = null;
		nf.setMaximumFractionDigits(4);

		int totalNeededHours = 0, totalWeight = 0;

		for (int i = 0; i < projects.size(); i++)
		{
			totalNeededHours += projects.get(i).getHoursNeeded();
			totalWeight += projects.get(i).getWeight();
		}

		double[][] res = solver(totalNeededHours, totalWeight);

		if(totalHours < 1)
			oPerc.setText(0 + "%");
		else if(totalHours >= totalNeededHours)
			oPerc.setText(100 + "%");
		else
			oPerc.setText(nf.format(res[projects.size()][totalHours] / totalWeight * 100)  + "%");

		int[] traceback = traceBack(res);

		// Write the result to the TableView
		for(int i = 0; i < projects.size(); i++)
		{
			row = FXCollections.observableArrayList();

			row.add(projects.get(i).getName());
			row.add(projects.get(i).getHoursNeeded() + "");
			row.add(projects.get(i).getWeight() + "%");
			row.add(nf.format(projects.get(i).getDensity()) + "");
			row.add(traceback[i] + "");
			row.add(nf.format(projects.get(i).getWeight() * (double)traceback[i] / projects.get(i).getHoursNeeded()) + "%");

			data.add(row);
		}

		table.getItems().clear();
		table.setItems(data);

		solve.setDisable(true);
		status.setText("Problem solved successfully!");
	}

	// Dynamic-based peoblem solving method
	private double[][] solver(int totalNeededHours, int totalWeight)
	{
		double[][] res = new double[projects.size() + 1][totalHours + 1];

		for (int i = 0; i < projects.size(); i++)
		{
			totalNeededHours += projects.get(i).getHoursNeeded();
			totalWeight += projects.get(i).getWeight();
		}

		for (int i = 1; i < res.length; i++)
			for (int j = 1; j < res[i].length; j++)
				for (int k = 0; k <= j; k++)
					res[i][j] = Math.max(res[i][j], res[i - 1][j - k] + f(projects.get(i - 1), k));

		System.out.print("\t\t  ");
		for(int i = 1; i < res.length; i++)
			System.out.printf("%4s\t  ", "Proj" + i);
		System.out.println();

		for(int j = 1; j < totalHours + 1; j++)
		{
			System.out.print("Hr." + j + "\t");
			for (int k = 1; k < res.length; k++)
				System.out.printf("%4g\t", res[k][j]);
			System.out.println();
		}

		return res;
	}

	private double f(Project p, int hours)
	{
		if (hours > p.getHoursNeeded())
			return p.getWeight();
		else
			return p.getDensity() * hours;
	}

	// Tracing back the table
	private int[] traceBack(double[][] res)
	{
		int[] hours = new int[projects.size()];
		int i = projects.size(), j = totalHours;

		while (i > 0 && j > 0)
			if (res[i][j] != res[i - 1][j])
			{
				hours[i-1]++;
				j--;
			}
			else
				i--;

		return hours;
	}

	// Closing the application
	public void close()
	{
		table.getScene().getWindow().hide();
	}
}
