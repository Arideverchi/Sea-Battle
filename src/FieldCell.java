import javafx.scene.control.Button;

class FieldCell extends Button {
	int x,y;
	private StatusEnum showStatus;
	StatusEnum trueStatus;
	FieldCell(int x, int y){
		super();
		this.x = x;
		this.y = y;
		trueStatus = StatusEnum.clear;
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

	public StatusEnum getShowStatus(){
		return showStatus;
	}
	public void setShowStatus(StatusEnum status){
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