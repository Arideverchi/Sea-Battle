import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

class ClickHandler<T extends Event> implements EventHandler {
	FieldCell[][] fieldCells;
	ToggleButton orientation;
	ClickHandler(FieldCell [][] fieldCells, ToggleButton toggle){
		this.fieldCells = fieldCells;
		this.orientation = toggle;
	}
	@Override
	public void handle(Event event) {
		FieldCell button = ((FieldCell) event.getSource());

	}
}
