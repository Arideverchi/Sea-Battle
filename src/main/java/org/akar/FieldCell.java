package org.akar;

import javafx.scene.control.Button;

class FieldCell extends Button {
    int x;
    int y;
    private Status showStatus;
    Status trueStatus;

    FieldCell(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        trueStatus = Status.CLEAR;
        showStatus = Status.CLEAR;
        setStatusStyle(showStatus);
        this.setPrefSize(30, 30);
    }

    FieldCell(Status showStatus, String text) {
        super(text);
        this.showStatus = showStatus;
        x = -1;
        y = -1;
        setStatusStyle(showStatus);
        this.setPrefSize(30, 30);
    }

    public Status getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(Status status) {
        this.showStatus = status;
        trueStatus = status;
        setStatusStyle(status);
    }


    private void setStatusStyle(Status status) {
        switch (status) {
            case INJURED, KILLED:
                setStyle("-fx-background-color: #ff0000;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-color: black;");
                setOnAction(null);
                break;
            case MISSED:
                setStyle("-fx-background-color: gray;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-color: black;");
                setDisable(true);
                break;
            case UNBROKEN:
                setStyle("-fx-background-color: #00ff3b;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-color: black;");
                break;
            case CLEAR:
                setStyle("-fx-background-color: #0be4ff;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-color: black;");
                break;
        }
    }


}