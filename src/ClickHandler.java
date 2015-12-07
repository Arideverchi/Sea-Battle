import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;

class ClickHandler<T extends ActionEvent> implements EventHandler {
	Label label;
	ClickHandler(Label label){
		this.label = label;
	}
	@Override
	public void handle(Event event) {
		FieldCell button = ((FieldCell) event.getSource());
		label.setText(button.x + " " + button.y);
	}
}
