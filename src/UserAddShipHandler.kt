import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label

internal class UserAddShipHandler(private var main: Main) : EventHandler<ActionEvent> {
    var length: Int = 0
    var count: Label = Label("0")
    override fun handle(event: ActionEvent) {
        val button = event.source as FieldCell
        if (!trySet(button.x, button.y) || count.text == "0") return
        for (i in 0 until length) {
            if (main.orientation.text == "H") {
                main.user[button.x][button.y + i].showStatus = (Status.unbroken)
            } else {
                main.user[button.x + i][button.y].showStatus = Status.unbroken
            }
        }
        val k = Integer.parseInt(count.text) - 1
        count.text = k.toString()
        main.userShips++
    }

    private fun trySet(x: Int, y: Int): Boolean {
        return if (main.orientation.text == "H") {
            trySetHorizontal(x, y)
        } else trySetVertical(x, y)
    }

    private fun trySetHorizontal(x: Int, y: Int): Boolean {
        if (y + length > 10) return false
        for (i in 0 until length) {
            if (!checkFieldRange(x, y + i)) {
                return false
            }
        }
        return true
    }

    private fun trySetVertical(x: Int, y: Int): Boolean {
        if (x + length > 10) return false
        for (i in 0 until length) {
            if (!checkFieldRange(x + i, y)) {
                return false
            }
        }
        return true
    }

    private fun checkFieldRange(x: Int, y: Int): Boolean {
        for(i in -1..1) {
            for(j in -1..1) {
                if (!checkField(x + i, y + j)) return false
            }
        }
        return true
    }

    private fun checkField(x: Int, y: Int): Boolean {
        if (x !in 0..9 || y !in 0..9) {
            return true
        }
        return main.user[x][y].showStatus != Status.unbroken
    }
}