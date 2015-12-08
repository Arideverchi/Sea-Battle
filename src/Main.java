import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
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

	FieldCell[] addShip;
	Label[] addShipCount;
	Button orientation;
	UserAddShipHandler addShipHandler;
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
		orientation = new Button("H");
		user = new FieldCell[10][10];
		cpu = new FieldCell[10][10];
		addShipHandler = new UserAddShipHandler<>(user, orientation);
		root.add(label, 10, 1);

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				user[i][j] = new FieldCell(i, j);
				user[i][j].setOnAction(addShipHandler);
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
			Integer fi = i;
			addShip[i].setOnAction(event -> {
				Button button = (Button) event.getSource();
				addShipHandler.setLength(Integer.parseInt(button.getText()));
				addShipHandler.setCount(addShipCount[fi]);
			});
			root.add(addShip[i], 1, 11 + i);
			root.add(addShipCount[i], 2, 11 + i);
		}

		orientation.setPrefSize(30, 30);
		orientation.setOnAction(event -> {
			Button button = (Button) event.getSource();
			if (button.getText().equals("H")){
				button.setText("V");
				return;
			}
			button.setText("H");
		});
		root.add(orientation, 3, 12);

		mainStage.show();
	}
}

