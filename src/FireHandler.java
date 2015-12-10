import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.LinkedList;

public class FireHandler<T extends Event> implements EventHandler {
	FieldCell[][] user, cpu;
	Integer userShips = 10;
	Integer cpuShips = 10;
	Stage stage;
	Main main;
	LinkedList<Point> firstSet;

	Mode mode = Mode.reconnaissance;
	Point lastHit;
	FireHandler(FieldCell user[][], FieldCell cpu[][], Stage stage, Main main){
		this.user = user;
		this.cpu = cpu;
		this.stage = stage;
		this.main = main;
		firstSet = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if ((i + j + 1) % 4 == 0){
					firstSet.add(new Point(i, j));
				}
			}
		}
	}
	@Override
	public void handle(Event event) {
		FieldCell cell = (FieldCell) event.getSource();
		switch (cell.trueStatus){
			case clear:
				cell.setShowStatus(Status.missed);
				cell.setDisable(true);//// TODO: 10.12.2015 add cpu shooting

				break;
			case unbroken:
				cell.setShowStatus(Status.injured);
				cell.setDisable(true);
				if (checkKilled(cell.x, cell.y, cpu)){
					cpuShips--;
					killShip(cell.x, cell.y, cpu);
				}
				if (cpuShips == 0){
					Alert gameResult = new Alert(Alert.AlertType.CONFIRMATION);
					gameResult.setTitle("You won!");
					gameResult.setHeaderText(null);
					gameResult.setContentText("Do you want to continue?");
					if (gameResult.showAndWait().get() == ButtonType.OK){
						main.start(stage);
					}else {
						stage.close();
					}
				}

		}
	}
	private void makeAMove(){
		switch (mode){
			case reconnaissance:
				searchEnemy();

				break;
		}
	}
	private void searchEnemy(){
		if (userShips.equals(10)){
			int a =(int) (Math.random() * firstSet.size());
			Point point = firstSet.get(a);

		}
	}

	private Status fire(int x, int y){
		switch (user[x][y].getShowStatus()){
			case clear:
				user[x][y].setShowStatus(Status.missed);
				return Status.missed;
			case unbroken:
				user[x][y].setShowStatus(Status.injured);
				if (checkKilled(x, y, user)){
					killShip(x, y, user);
					mode = Mode.reconnaissance;
					return Status.killed;
				}else {
					mode = Mode.denying;
					return Status.injured;
				}
		}
		return Status.clear;// carefully it might cause a bug
	}

	private Boolean checkKilled(int x, int y, FieldCell [][]field){
		int i = x;
		while (i < 10){
			if (cpu[i][y].trueStatus.equals(Status.unbroken)){
				return false;
			}
			if (cpu[i][y].getShowStatus().equals(Status.missed) || cpu[i][y].getShowStatus().equals(Status.clear)){
				break;

			}
			++i;
		}
		i = x;
		while (i >= 0){
			if (field[i][y].trueStatus.equals(Status.unbroken)){
				return false;
			}
			if (field[i][y].getShowStatus().equals(Status.missed) || field[i][y].getShowStatus().equals(Status.clear)){
				break;
			}
			--i;
		}
		i = y;
		while (i <= 9){
			if (field[x][i].trueStatus.equals(Status.unbroken)){
				return false;
			}
			if (field[x][i].getShowStatus().equals(Status.missed) || field[x][i].getShowStatus().equals(Status.clear)){
				break;
			}
			++i;
		}
		i = y;
		while (i > -1){
			if (field[x][i].trueStatus.equals(Status.unbroken)){
				return false;
			}
			if (field[x][i].getShowStatus().equals(Status.missed) || field[x][i].getShowStatus().equals(Status.clear)){
				break;
			}
			--i;
		}
		return true;
	}
	private void killShip(int x, int y, FieldCell [][]field){
		killCell(x, y, field);
		int i = x;
		while (isInField(++i, y) && field[i][y].getShowStatus().equals(Status.injured)){
			killCell(i, y, field);
		}
		i = x;
		while (isInField(--i, y) && field[i][y].getShowStatus().equals(Status.injured)){
			killCell(i, y, field);
		}
		i = y;
		while (isInField(x, ++i) && field[x][i].getShowStatus().equals(Status.injured)){
			killCell(x, i, field);
		}
		i = y;
		while (isInField(x, --i) && field[x][i].getShowStatus().equals(Status.injured)){
			killCell(x, i, field);
		}


	}
	private void killCell(int x, int y, FieldCell [][]field){
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i == 0 && j ==0)
					continue;
				if (isInField(x + i, y + j) && !field[x + i][y + j].getShowStatus().equals(Status.injured)){
					field[x + i][y + j].setShowStatus(Status.missed);
				}
			}
		}
	}

	private boolean isInField(int x, int y){
		return (x < 10 && x >= 0 && y < 10 && y >= 0);
	}
}
enum Mode{
	reconnaissance, denying
}
class Point{
	int x, y;
	Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Point point = (Point) o;

		return x == point.x && y == point.y;

	}
	
	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
}