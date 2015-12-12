import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Arrays;

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
	Stage mainStage;
	GridPane root;
	Button startButton;
	Integer userShips = 0, cpuShips = 0;

	FieldCell[] addShip;
	Label[] addShipCount;
	Button orientation;
	Button clearField;
	UserAddShipHandler addShipHandler;


	public void start(Stage mainStage){
		prepareWindow(mainStage);
		createFields();
		createAddButtons();

		startButton = new Button("Start");
		startButton.setOnAction(event -> {
			if(userShips == 10){// TODO: 11.12.2015 fix this
				root.getChildren().removeAll(orientation, clearField, startButton);
				root.getChildren().removeAll(Arrays.asList(addShip));
				root.getChildren().removeAll(Arrays.asList(addShipCount));
				fillCpuField();
				FireHandler<ActionEvent> fireHandler = new FireHandler<>(user, cpu, mainStage, this);
				for (int i = 0; i < 10; i++) {
					for (int j = 0; j < 10; j++) {
						//cpu[i][j].setShowStatus(cpu[i][j].trueStatus);// TODO: 10.12.2015 hide this in the end
						cpu[i][j].setOnAction(fireHandler);
					}
				}
			}else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setContentText("Not enough ships on the field!");
				alert.showAndWait();
			}
		});
		root.add(startButton, 7, 11, 2, 1);

		Button restart = new Button("restart");
		restart.setOnAction(event -> {
			mainStage.close();
			start(mainStage);
		});
		root.add(restart, 9, 11, 2 ,1);

		mainStage.show();
	}

	private void clearUserField(){
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				user[i][j].setShowStatus(Status.clear);
			}
		}
		addShipHandler.setLength(0);
		addShipHandler.setCount(new Label("0"));
		for (int i = 0; i < 4; i++) {
			addShipCount[i].setText(Integer.toString(4 - i));
		}
		userShips = 0;
	}
	private void prepareWindow(Stage mainStage){
		mainStage.setTitle("Sea Battle");

		root = new GridPane();
		this.mainStage = mainStage;
		mainScene = new Scene(root, 630, 450);
		mainStage.setScene(mainScene);
		mainStage.setResizable(false);
		//root.setGridLinesVisible(true);
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

		userShips = 0;
		cpuShips = 0;
	}
	private void createFields(){
		orientation = new Button("H");
		user = new FieldCell[10][10];
		cpu = new FieldCell[10][10];
		addShipHandler = new UserAddShipHandler<>(this);


		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				user[i][j] = new FieldCell(i, j);
				user[i][j].setOnAction(addShipHandler);
				cpu[i][j] = new FieldCell(i, j);
				root.add(user[i][j], j, i);
				root.add(cpu[i][j], j + 11, i);
			}
		}
	}
	private void createAddButtons(){
		addShip = new FieldCell[4];
		addShipCount = new Label[4];
		for (int i = 0; i < 4; i++) {
			addShip[i] = new FieldCell(Status.unbroken, Integer.toString(i + 1));
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

		clearField = new Button("Clear");
		clearField.setOnAction(event -> Main.this.clearUserField());
		root.add(clearField, 5 , 11, 2, 1);
	}
	private void fillCpuField(){
		boolean flag;
		for (int i = 4; i >= 2 ; i--) {
			for (int j = 0; j < 4 - i + 1; j++) {
				if (i == 2 && j == 2)
					continue;
				do {
					int orientation = (int) (Math.random() * 2.0);
					int side = (int) (Math.random() * 2.0);
					int pos = (int)(Math.random() *(11.0 - i));
					int x = pos * orientation + 9 * side * Math.abs(orientation - 1);
					int y = pos * Math.abs(orientation - 1) + 9 * side * orientation;
					flag = trySet(x, y, orientation, i );
					if (!flag)
						continue;
					place(x, y, orientation, i);
				}while (!flag);
			}
		}
		do {
			int orientation = (int) (Math.random() * 2.0);
			int x = (int) (Math.random() * 8.0) + 1;
			int y = (int) (Math.random() * 8.0) + 1;
			flag = trySet(x, y, orientation, 2);
			if (!flag)
				continue;
			place(x, y, orientation, 2);
		}while (!flag);

		for (int i = 0; i < 4; i++) {
			do {
				int x = (int) (Math.random() * 8.0) + 1;
				int y = (int) (Math.random() * 8.0) + 1;
				flag = trySet(x, y, 0, 2);
				if (!flag)
					continue;
				place(x, y, 0, 1);
			}while (!flag);
		}
		cpuShips = 10;
	}
	private void place(int x, int y,int orientation, int length){
		switch (orientation){
			case 0:
				for (int i = 0; i <length ; i++) {
					cpu[x][y + i].trueStatus = Status.unbroken;
				}
				break;
			case 1:
				for (int i = 0; i < length; i++) {
					cpu[x + i][y].trueStatus = Status.unbroken;
				}
				break;
		}
	}
	private Boolean trySet(Integer x, Integer y, Integer orientation, Integer length){
		switch (orientation){
			case 0:
				if (y + length > 10)
					return Boolean.FALSE;
				for (int i = 0; i <length ; i++) {
					if( !checkField(x, y + i))
						return Boolean.FALSE;
				}
				break;
			case 1:
				if (x + length > 10)
					return Boolean.FALSE;
				for (int i = 0; i < length; i++) {
					if(!checkField(x + i, y)){
						return Boolean.FALSE;
					}
				}
				break;
		}
		return Boolean.TRUE;
	}
	private Boolean checkField(int x, int y){
		Boolean result = Boolean.TRUE;
		if (x > 0)
			result = !cpu[x - 1][y].trueStatus.equals(Status.unbroken);
		if (y > 0)
			result = !cpu[x][y - 1].trueStatus.equals(Status.unbroken) && result;
		if (x < 9)
			result = !cpu[x + 1][y].trueStatus.equals(Status.unbroken) && result;
		if (y < 9)
			result = !cpu[x][y + 1].trueStatus.equals(Status.unbroken) && result;
		if (x > 0 && y > 0)
			result = !cpu[x - 1][y - 1].trueStatus.equals(Status.unbroken) && result;
		if (x > 0 && y < 9)
			result = !cpu[x - 1][y + 1].trueStatus.equals(Status.unbroken) && result;
		if (x < 9 && y < 9)
			result = !cpu[x + 1][y + 1].trueStatus.equals(Status.unbroken) && result;
		if (x < 9 && y > 0)
			result = !cpu[x + 1][y - 1].trueStatus.equals(Status.unbroken) && result;
		result = !cpu[x][y].trueStatus.equals(Status.missed) && result;
		return result;
	}
}
