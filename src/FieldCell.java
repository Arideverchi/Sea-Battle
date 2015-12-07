import javafx.scene.control.Button;

class FieldCell extends Button {
	int x,y;
	StatusEnum showStatus, trueStatus;
	FieldCell(int x, int y){
		super();
		this.x = x;
		this.y = y;

		showStatus = StatusEnum.clear;
		setStatusStyle(showStatus);
		this.setPrefSize(30,30);
	}
	FieldCell(StatusEnum showStatus, String text){
		super(text);
		this.showStatus = showStatus;
		x = -1;
		y = -1;
		setStatusStyle(showStatus);
		this.setPrefSize(30,30);
	}

	public void changeShowStatus(StatusEnum status){
		this.showStatus = status;
		setStatusStyle(status);
	}

	private void setStatusStyle(StatusEnum status){
		switch (status){
			case injured:
				setStyle("-fx-background-color: #ff000a;" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
				break;
			case missed:
				setStyle("-fx-background-color: rgba(65, 64, 68, 0.91);" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
				break;
			case unbroken:
				setStyle("-fx-background-color: #00ff3b;" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
				break;
			case clear:
				setStyle("-fx-background-color: #0be4ff;" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
		}
	}


}