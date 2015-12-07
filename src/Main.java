import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
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

	FieldCell[] addShip;
	Label[] addShipCount;
	ToggleButton orientation;
	public void start(Stage mainStage){
		root = new GridPane();

		mainScene = new Scene(root, 630, 500);
		mainStage.setScene(mainScene);
		mainStage.setResizable(false);
		root.setGridLinesVisible(true);
		ColumnConstraints column = new ColumnConstraints(30);
		column.setHalignment(HPos.CENTER);
		RowConstraints row = new RowConstraints(30);
		row.setValignment(VPos.CENTER);
		for (int i = 0; i < 21; i++) {
			root.getColumnConstraints().add(column);
		}
		for (int i = 0; i < 20; i++) {
			root.getRowConstraints().add(row);
		}

		Label label = new Label("sd");
		ClickHandler<ActionEvent> handler = new ClickHandler<>(user, orientation);
		root.add(label, 10, 1);


		user = new FieldCell[10][10];
		cpu = new FieldCell[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				user[i][j] = new FieldCell(i, j);
				user[i][j].setOnAction(handler);// TODO: 08.12.2015 reconstruct this handler later
				cpu[i][j] = new FieldCell(i, j);
				root.add(user[i][j], j, i);
				root.add(cpu[i][j], j + 11, i);
			}
		}

		addShip = new FieldCell[4];
		addShipCount = new Label[4];
		for (int i = 0; i < 4; i++) {
			addShip[i] = new FieldCell(StatusEnum.unbroken, Integer.toString(i + 1));
			addShipCount[i] = new Label(Integer.toString(4 - i));

			root.add(addShip[i], 1, 11 + i);
			root.add(addShipCount[i], 2, 11 + i);
		}
		orientation = new ToggleButton("H");
		orientation.setPrefSize(30, 30);
		orientation.setOnAction(event -> {
			ToggleButton button = (ToggleButton) event.getSource();
			if (button.isSelected()){
				button.setText("V");
				return;
			}
			button.setText("H");
		});
		root.add(orientation, 3, 12);


		mainStage.show();
	}
}

