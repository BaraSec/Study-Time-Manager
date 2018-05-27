package Classes;

public class Project implements Comparable<Project>
{
	// Data fields
	private String name;
	private int hoursNeeded, weight;
	private double density;

	// Constructors
	public Project(String name, int hoursNeeded, int weight)
	{
		this.name = name;
		this.hoursNeeded = hoursNeeded;
		this.weight = weight;

		density = (double)weight / hoursNeeded;
	}

	// Getters and setters
	public String getName()
	{
		return name;
	}

	public int getHoursNeeded()
	{
		return hoursNeeded;
	}

	public int getWeight()
	{
		return weight;
	}

	public double getDensity()
	{
		return density;
	}

	// Comparator depending on density
	@Override
	public int compareTo(Project o)
	{
		return (int)Math.floor(o.getDensity() - this.density);
	}
}
