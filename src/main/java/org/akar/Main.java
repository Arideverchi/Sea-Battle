package org.akar;

import java.util.Arrays;
import java.util.Random;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

/**
 * The Great Sea Battle app
 */
public class Main extends Application {

    public static final Random RANDOM = new Random();

    public static void main(String[] args) {
        launch(args);
    }

    FieldCell[][] user;
    FieldCell[][] cpu;
    Scene mainScene;
    Stage mainStage;
    private GridPane root;
    private ToolBar toolBar;
    Button help;
    Button about;
    Button startButton;
    Integer userShips = 0, cpuShips = 0;

    FieldCell[] addShip;
    Label[] addShipCount;
    Button orientation;
    Button clearField;
    UserAddShipHandler addShipHandler;


    public void start(Stage mainStage) {


        prepareWindow(mainStage);
        createFields();
        createAddButtons();

        startButton = new Button("Start");
        startButton.setOnAction(event -> {
            if (userShips == 10) {// TODO: 11.12.2015 fix this
                root.getChildren().removeAll(orientation, clearField, startButton);
                root.getChildren().removeAll(Arrays.asList(addShip));
                root.getChildren().removeAll(Arrays.asList(addShipCount));
                fillCpuField();
                FireHandler fireHandler = new FireHandler(user, cpu, mainStage, this);
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        //cpu[i][j].setShowStatus(cpu[i][j].trueStatus);// TODO: 10.12.2015 hide this in the end
                        cpu[i][j].setOnAction(fireHandler);
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Not enough ships on the field!");
                alert.showAndWait();
            }
        });
        root.add(startButton, 7, 12, 2, 1);

        Button restart = new Button("Restart");
        restart.setOnAction(event -> {
            mainStage.close();
            start(mainStage);
        });
        root.add(restart, 9, 12, 2, 1);

        mainStage.show();
    }

    private void clearUserField() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                user[i][j].setShowStatus(Status.CLEAR);
            }
        }
        addShipHandler.setLength(0);
        addShipHandler.setCount(new Label("0"));
        for (int i = 0; i < 4; i++) {
            addShipCount[i].setText(Integer.toString(4 - i));
        }
        userShips = 0;
    }

    private void prepareWindow(Stage mainStage) {
        mainStage.setTitle("Sea Battle");
        root = new GridPane();
        this.mainStage = mainStage;
        mainScene = new Scene(root, 630, 480);

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
        createMenus();
        userShips = 0;
        cpuShips = 0;
        mainStage.setScene(mainScene);
    }

    private void createMenus() {
        help = new Button("Help");
        EventHandler<ActionEvent> helpHandler = event -> {
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            message.setTitle("Help");
            message.setHeaderText(null);
            message.setContentText("""
                Press green button with digit to place the ship. Number on the button indicates length of the ship.
                "Number near the button indicates amount of ships of this length.
                "To change orientation press the button. V  means vertical orientation, H means horizontal orientation.
                "Press Clear to clear your field.
                "Press Start to start the game.
                "Press Restart to start new game.""");
            message.showAndWait();
        };

        help.setOnAction(helpHandler);
        about = new Button("About");
        about.setOnAction(event -> {
            Alert message = new Alert(Alert.AlertType.INFORMATION);
            message.setTitle("About");
            message.setHeaderText(null);
            message.setContentText("Developed by Alexander Karpovich BrSU 2015");
            message.showAndWait();
        });
        toolBar = new ToolBar(help, about);
        toolBar.prefWidthProperty().bind(mainStage.widthProperty());
        ((GridPane) mainScene.getRoot()).add(toolBar, 0, 0, 21, 1);
    }

    private void createFields() {
        orientation = new Button("H");
        user = new FieldCell[10][10];
        cpu = new FieldCell[10][10];
        addShipHandler = new UserAddShipHandler(this);


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                user[i][j] = new FieldCell(i, j);
                user[i][j].setOnAction(addShipHandler);
                cpu[i][j] = new FieldCell(i, j);
                root.add(user[i][j], j, i + 1);
                root.add(cpu[i][j], j + 11, i + 1);
            }
        }
    }

    private void createAddButtons() {
        addShip = new FieldCell[4];
        addShipCount = new Label[4];
        for (int i = 0; i < 4; i++) {
            addShip[i] = new FieldCell(Status.UNBROKEN, Integer.toString(i + 1));
            addShip[i].setPrefSize(25, 25);
            addShipCount[i] = new Label(Integer.toString(4 - i));
            int fi = i;
            addShip[i].setOnAction(event -> {
                Button button = (Button) event.getSource();
                addShipHandler.setLength(Integer.parseInt(button.getText()));
                addShipHandler.setCount(addShipCount[fi]);
            });
            root.add(addShip[i], 1, 12 + i);
            root.add(addShipCount[i], 2, 12 + i);
        }

        orientation.setPrefSize(30, 30);
        orientation.setOnAction(event -> {
            Button button = (Button) event.getSource();
            if (button.getText().equals("H")) {
                button.setText("V");
                return;
            }
            button.setText("H");
        });
        root.add(orientation, 3, 12);

        clearField = new Button("Clear");
        clearField.setOnAction(event -> Main.this.clearUserField());
        root.add(clearField, 5, 12, 2, 1);
    }

    private void fillCpuField() {
        boolean flag;
        for (int i = 4; i >= 2; i--) {
            for (int j = 0; j < 4 - i + 1; j++) {
				if (i == 2 && j == 2) {
					continue;
				}
                do {
                    int orientation =  RANDOM.nextInt(2);
                    int side = RANDOM.nextInt(2);
                    int pos =  RANDOM.nextInt(11 - i);
                    int x = pos * orientation + 9 * side * Math.abs(orientation - 1);
                    int y = pos * Math.abs(orientation - 1) + 9 * side * orientation;
                    flag = trySet(x, y, orientation, i);
					if (!flag) {
						continue;
					}
                    place(x, y, orientation, i);
                } while (!flag);
            }
        }
        do {
            int orientation = RANDOM.nextInt(2);
            int x = (int) (Math.random() * 8.0) + 1;
            int y = (int) (Math.random() * 8.0) + 1;
            flag = trySet(x, y, orientation, 2);
			if (!flag) {
				continue;
			}
            place(x, y, orientation, 2);
        } while (!flag);

        for (int i = 0; i < 4; i++) {
            do {
                int x = (int) (Math.random() * 9.0);
                int y = (int) (Math.random() * 9.0);
                flag = trySet(x, y, 0, 2);
				if (!flag) {
					continue;
				}
                place(x, y, 0, 1);
            } while (!flag);
        }
        cpuShips = 10;
    }

    private void place(int x, int y, int orientation, int length) {
        switch (orientation) {
            case 0:
                for (int i = 0; i < length; i++) {
                    cpu[x][y + i].trueStatus = Status.UNBROKEN;
                }
                break;
            case 1:
                for (int i = 0; i < length; i++) {
                    cpu[x + i][y].trueStatus = Status.UNBROKEN;
                }
                break;
        }
    }

    private Boolean trySet(Integer x, Integer y, Integer orientation, Integer length) {
        switch (orientation) {
            case 0:
				if (y + length > 10) {
					return Boolean.FALSE;
				}
                for (int i = 0; i < length; i++) {
					if (!checkField(x, y + i)) {
						return Boolean.FALSE;
					}
                }
                break;
            case 1:
				if (x + length > 10) {
					return Boolean.FALSE;
				}
                for (int i = 0; i < length; i++) {
                    if (!checkField(x + i, y)) {
                        return Boolean.FALSE;
                    }
                }
                break;
        }
        return Boolean.TRUE;
    }

    private boolean checkField(int x, int y) {
        boolean result = Boolean.TRUE;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i != 0 || j != 0) && isInField(x + i, y + j)) {
                    result = !cpu[x + i][y + j].trueStatus.equals(Status.UNBROKEN) && result;
                }
            }
        }

        return result;
    }

    private boolean isInField(int x, int y) {
        return (x < 10 && x >= 0 && y < 10 && y >= 0);
    }
}
