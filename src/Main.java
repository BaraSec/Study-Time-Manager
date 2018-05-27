import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application
{
	@Override
	public void start(Stage stage) throws Exception
	{
		Parent root = FXMLLoader.load(getClass().getResource("Resources/UI/MainUI.fxml"));

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Study Time Manager");
		stage.getIcons().add(new Image("file:src/resources/Images/clock.gif"));
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
