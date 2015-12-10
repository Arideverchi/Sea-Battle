import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

class UserAddShipHandler<T extends Event> implements EventHandler {
	Integer length;
	Label count;
	Main main;

	public void setLength(Integer length) {
		this.length = length;
	}

	public void setCount(Label count) {
		this.count = count;
	}

	UserAddShipHandler(Main main){
		this.main = main;
		this.length = 0;
		this.count = new Label("0");
	}
	@Override
	public void handle(Event event) {
		FieldCell button = ((FieldCell) event.getSource());
		if (!trySet(button.x, button.y) || count.getText().equals("0"))
			return;
		for (int i = 0; i < length; i++) {
			if(main.orientation.getText().equals("H")){
				main.user[button.x][button.y + i].setShowStatus(StatusEnum.unbroken);
			}else {
				main.user[button.x + i][button.y].setShowStatus(StatusEnum.unbroken);
			}
		}
		Integer k = Integer.parseInt( count.getText()) - 1;
		count.setText(k.toString());
		main.userShips++;
	}

	private Boolean trySet(int x, int y){
		if (main.orientation.getText().equals("H")){
			return trySetHorizontal(x, y);
		}
		return trySetVertical(x, y);
	}

	private Boolean trySetHorizontal(int x, int y){
		if (y + length > 10)
			return Boolean.FALSE;
		for (int i = 0; i < length; i++) {
			if (!checkField(x, y + i)) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}
	private Boolean trySetVertical(int x, int y){
		if (x + length > 10)
			return Boolean.FALSE;
		for (int i = 0; i < length; i++) {
			if (!checkField(x + i, y)) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	private Boolean checkField(int x, int y){
		Boolean result = Boolean.TRUE;
		if (x > 0)
			result = !main.user[x - 1][y].getShowStatus().equals(StatusEnum.unbroken);
		if (y > 0)
			result = !main.user[x][y - 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (x < 9)
			result = !main.user[x + 1][y].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (y < 9)
			result = !main.user[x][y + 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (x > 0 && y > 0)
			result = !main.user[x - 1][y - 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (x > 0 && y < 9)
			result = !main.user[x - 1][y + 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (x < 9 && y < 9)
			result = !main.user[x + 1][y + 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		if (x < 9 && y > 0)
			result = !main.user[x + 1][y - 1].getShowStatus().equals(StatusEnum.unbroken) && result;
		return result;
	}
}
