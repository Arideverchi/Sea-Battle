import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.LinkedList;

public class FireHandler<T extends Event> implements EventHandler {
	FieldCell[][] user, cpu;
	Integer countUserShips = 10;
	int [] userShips = {4, 3 , 2 , 1};
	Integer countCpuShips = 10;
	Stage stage;
	Main main;
	LinkedList<Point> firstSet;

	Mode mode = Mode.reconnaissance;
	Point lastHit = new Point();
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
				makeAMove();
				break;
			case unbroken:
				cell.setShowStatus(Status.injured);
				cell.setDisable(true);
				if (checkKilled(cell.x, cell.y, cpu)){
					countCpuShips--;
					killShip(cell.x, cell.y, cpu);
				}
				if (countCpuShips == 0){
					showResult("You won!");
				}
		}
	}
	private void makeAMove(){
		switch (mode){
			case reconnaissance:
				searchEnemy();
				break;
			case denying:
				deny();
				break;
		}
	}
	private void searchEnemy(){
		if (countUserShips.equals(0))
			return;
		if (countUserShips.equals(10)){
			int a =(int) (Math.random() * firstSet.size());
			Point point = firstSet.get(a);
			switch (fire(point.x, point.y)){
				case killed:
					countUserShips--;
					searchEnemy();
					break;
				case injured:
					deny();
					break;
				case missed:
					firstSet.remove(a);
					break;
			}
			return;
		}
		Point point = calculateSearch();
		switch (fire(point.x, point.y)){
			case killed:
				searchEnemy();
				break;
			case injured:
				deny();
				break;
		}
	}

	private void deny(){
		int bottom;
		int top;
		int right;
		int left;
		int r[][] = generateRMatrix();
		int pos[] = InjuredLengthAndOrientation(lastHit.x, lastHit.y);
		Point target = new Point(-1, -1);
		if (pos[0] == 1){
			int max = 0;
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1 ; j++) {
					int x = lastHit.x + i;
					int y = lastHit.y + j;
					if (isInField(x, y) && Math.abs(i) != Math.abs(j) && r[x][y] > max){
						max = r[x][y];
						target = new Point(x, y);
					}
				}
			}
		}else {
			switch (pos[1]){
				case 0:
					int x = lastHit.x;
					right = left = lastHit.y;
					while (left >= 0 && user[x][left].getShowStatus().equals(Status.injured)){
						left--;
					}
					while (right < 10 && user[x][right].getShowStatus().equals(Status.injured)){
						right++;
					}
					int max = 0;

					if (isInField(x, left) && r[x][left] > max){
						max = r[x][left];
						target = new Point(x, left);
					}
					if (isInField(x, right) && r[x][right] > max){
						max = r[x][right];
						target = new Point(x, right);
					}
					break;
				case 1:

					int y = lastHit.y;
					bottom = top = lastHit.x;
					while (top >= 0 && user[top][y].getShowStatus().equals(Status.injured)){
						top--;
					}
					while (bottom < 10 && user[bottom][y].getShowStatus().equals(Status.injured)){
						bottom++;
					}
					max = 0;

					if (isInField(top, y) && r[top][y] > max){// FIXME: 12.12.2015 wrong parameters
						max = r[top][y];
						target = new Point(top, y);
					}
					if (isInField(bottom, y) && r[bottom][y] > max){
						max = r[bottom][y];
						target = new Point(bottom, y);
					}
					break;
			}
		}
		switch (fire(target.x, target.y)){
			case injured:
				deny();
				break;
			case killed:
				mode = Mode.reconnaissance;
				searchEnemy();
				break;
		}
	}

	private int[] InjuredLengthAndOrientation(int x, int y){
		int result[] = {1, 0};
		int i = x;
		while (isInField(++i, y) && user[i][y].getShowStatus().equals(Status.injured)){
			result[0]++;
			result[1] = 1;
		}
		i = x;
		while (isInField(--i, y) && user[i][y].getShowStatus().equals(Status.injured)){
			result[0]++;
			result[1] = 1;
		}
		i = y;
		while (isInField(x, ++i) && user[x][i].getShowStatus().equals(Status.injured)){
			result[0]++;
			result[1] = 0;
		}
		i = y;
		while (isInField(x, --i) && user[x][i].getShowStatus().equals(Status.injured)){
			result[0]++;
			result[1] = 0;
		}
		return result;
	}

	private Status fire(int x, int y){
		System.out.println(x + " " + y);
		try {
			switch (user[x][y].getShowStatus()) {

				case clear:
					user[x][y].setShowStatus(Status.missed);
					return Status.missed;
				case unbroken:
					user[x][y].setShowStatus(Status.injured);
					if (checkKilled(x, y, user)) {
						userShips[killShip(x, y, user) - 1]--;
						countUserShips--;
						if (countUserShips == 0) {
							for (int i = 0; i < 10; i++) {
								for (int j = 0; j < 10; j++) {
									cpu[i][j].setShowStatus(cpu[i][j].trueStatus);
								}
							}
							showResult("I won!");
							return Status.clear;
						}
						//System.out.println(userShips);
						mode = Mode.reconnaissance;
						return Status.killed;
					} else {
						mode = Mode.denying;
						lastHit.x = x;
						lastHit.y = y;
						return Status.injured;
					}
			}

			System.out.println("if you see this message you have a bug");
			return Status.clear;// carefully it might cause a bug
		}catch (Exception e){
			throw  e;
		}
	}

	private Point calculateSearch(){
		int r[][] = generateRMatrix();
		int max = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				max = Math.max(max, r[i][j]);
			}
		}

		ArrayList<Point> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (r[i][j] == max){
					list.add(new Point(i, j));
				}
			}
		}
		int a = (int) (Math.random() * list.size());
		return list.get(a);
	}
	private int [][] generateRMatrix(){
		int r[][] = new int[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				for (int orientation = 0; orientation < 2; orientation++) {
					for (int size = 0; size < 4; size++) {
						for (int k = 1; k < userShips[size] + 1; k++) {
							if (size == 0 && orientation == 1)
								continue;
							if ( trySet(i, j, orientation, size + 1)){
								place(i, j, orientation, size + 1, r);
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				//System.out.printf("%3d", r[i][j]);
			}
			//System.out.println();
		}
		return r;
	}

	private void place(int x, int y, int orientation, int length, int [][]r){
		switch (orientation){
			case 0:
				for (int i = 0; i < length; i++) {
					r[x][y + i]++;
				}
				break;
			case 1:
				for (int i = 0; i < length; i++) {
					r[x + i][y]++;
				}
				break;

		}
	}
	private Boolean trySet(Integer x, Integer y, Integer orientation, Integer length){
		switch (orientation){
			case 0:
				if (y + length >= 11)
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
		return !user[x][y].trueStatus.equals(Status.missed) && !user[x][y].trueStatus.equals(Status.killed);
	}

	private Boolean checkKilled(int x, int y, FieldCell [][]field){
		int i = x;
		while (i < 10){
			if (field[i][y].trueStatus.equals(Status.unbroken)){
				return false;
			}
			if (field[i][y].getShowStatus().equals(Status.missed) || field[i][y].getShowStatus().equals(Status.clear)){
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
	private int killShip(int x, int y, FieldCell [][]field){
		killCell(x, y, field);
		int result = 1;
		int i = x;
		while (isInField(++i, y) && field[i][y].getShowStatus().equals(Status.injured)){
			killCell(i, y, field);
			result++;
		}
		i = x;
		while (isInField(--i, y) && field[i][y].getShowStatus().equals(Status.injured)){
			killCell(i, y, field);
			result++;
		}
		i = y;
		while (isInField(x, ++i) && field[x][i].getShowStatus().equals(Status.injured)){
			killCell(x, i, field);
			result++;
		}
		i = y;
		while (isInField(x, --i) && field[x][i].getShowStatus().equals(Status.injured)){
			killCell(x, i, field);
			result++;
		}
		return result;
	}
	private void killCell(int x, int y, FieldCell [][]field){
		field[x][y].setShowStatus(Status.killed);
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (i == 0 && j ==0)
					continue;
				if (isInField(x + i, y + j) && field[x + i][y + j].getShowStatus().equals(Status.clear)){
					field[x + i][y + j].setShowStatus(Status.missed);
				}
			}
		}
	}

	private boolean isInField(int x, int y){
		return (x < 10 && x >= 0 && y < 10 && y >= 0);
	}
	private void showResult(String message){
		Alert gameResult = new Alert(Alert.AlertType.CONFIRMATION);
		gameResult.setTitle(message);
		gameResult.setHeaderText(null);
		gameResult.setContentText("Do you want to continue?");
		if (gameResult.showAndWait().get() == ButtonType.OK){
			main.start(stage);
		}else {
			stage.close();
		}
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
	Point(){}
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