import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The Great Sea Battle app
 */
public class Main extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	FieldCell [][]user;
	FieldCell [][]cpu;
	Scene mainScene;
	GridPane root;
	public void start(Stage mainStage){
		root = new GridPane();
		mainScene = new Scene(root, 630, 400);
		mainStage.setScene(mainScene);
		mainStage.setResizable(false);
		root.setGridLinesVisible(true);
		ColumnConstraints column = new ColumnConstraints(30);
		RowConstraints row = new RowConstraints(30);
		for (int i = 0; i < 21; i++) {
			root.getColumnConstraints().add(column);
		}
		for (int i = 0; i < 20; i++) {
			root.getRowConstraints().add(row);
		}

		Label label = new Label("sd");
		ClickHandler<ActionEvent> handler = new ClickHandler<>(label);
		root.add(label, 10, 1);
		user = new FieldCell[10][10];
		cpu = new FieldCell[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				user[i][j] = new FieldCell(i, j);
				user[i][j].setOnAction(handler);
				cpu[i][j] = new FieldCell(i, j);
				root.add(user[i][j], j, i);
				root.add(cpu[i][j], j + 11, i);
			}
		}



		mainStage.show();
	}
}

