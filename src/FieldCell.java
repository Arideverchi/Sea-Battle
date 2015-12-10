import javafx.scene.control.Button;

class FieldCell extends Button {
	int x,y;
	private Status showStatus;
	Status trueStatus;
	FieldCell(int x, int y){
		super();
		this.x = x;
		this.y = y;
		trueStatus = Status.clear;
		showStatus = Status.clear;
		setStatusStyle(showStatus);
		this.setPrefSize(30,30);
	}
	FieldCell(Status showStatus, String text){
		super(text);
		this.showStatus = showStatus;
		x = -1;
		y = -1;
		setStatusStyle(showStatus);
		this.setPrefSize(30,30);
	}

	public Status getShowStatus(){
		return showStatus;
	}
	public void setShowStatus(Status status){
		this.showStatus = status;
		trueStatus = status;
		setStatusStyle(status);
	}


	private void setStatusStyle(Status status){
		switch (status){
			case injured:
				setStyle("-fx-background-color: red;" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
				setDisable(true);
				break;
			case missed:
				setStyle("-fx-background-color: rgba(65, 64, 68, 0.91);" +
						"-fx-border-width: 1px;" +
						"-fx-border-color: black;");
				setDisable(true);
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