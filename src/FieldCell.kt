import javafx.scene.control.Button

internal class FieldCell : Button {
    var x: Int
    var y: Int
    var showStatus: Status
        set(value) {
            field = value
            trueStatus = value
            setStatusStyle(value)
        }

    var trueStatus: Status? = null

    constructor(x: Int, y: Int) : super() {
        this.x = x
        this.y = y
        trueStatus = Status.clear
        showStatus = Status.clear
        setStatusStyle(showStatus)
        this.setPrefSize(30.0, 30.0)
    }

    constructor(showStatus: Status, text: String?) : super(text) {
        this.showStatus = showStatus
        x = -1
        y = -1
        setStatusStyle(showStatus)
        this.setPrefSize(30.0, 30.0)
    }

    private fun setStatusStyle(status: Status) {
        val borderStyle = "-fx-border-width: 1px;-fx-border-color: black;"
        when (status) {
            Status.injured, Status.killed -> {
                style = "-fx-background-color: #ff0000;$borderStyle"
                onAction = null
            }
            Status.missed -> {
                style = "-fx-background-color: gray;$borderStyle"
                isDisable = true
            }
            Status.unbroken -> style = "-fx-background-color: #00ff3b;$borderStyle"
            Status.clear -> style = "-fx-background-color: #0be4ff;$borderStyle"
        }
    }
}