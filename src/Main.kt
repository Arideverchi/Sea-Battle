import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import javafx.stage.Stage
import kotlin.math.abs

/**
 * The Great Sea Battle app
 */
class Main : Application() {
    internal lateinit var user: Array<Array<FieldCell>>
    private lateinit var cpu: Array<Array<FieldCell>>
    private lateinit var mainScene: Scene
    private lateinit var mainStage: Stage
    private lateinit var root: GridPane
    private var toolBar: ToolBar? = null
    private lateinit var help: Button
    private lateinit var about: Button
    private lateinit var startButton: Button
    internal var userShips: Int = 0
    private var cpuShips: Int = 0
    private lateinit var addShip: Array<FieldCell>
    private lateinit var addShipCount: Array<Label>
    internal lateinit var orientation: Button
    private lateinit var clearField: Button
    private lateinit var addShipHandler: UserAddShipHandler
    override fun start(mainStage: Stage) {
        prepareWindow(mainStage)
        createFields()
        createAddButtons()
        startButton = Button("Start")
        startButton.onAction = EventHandler {
            if (userShips == 10) {// TODO: 11.12.2015 fix this

                root.children.removeAll(orientation, clearField, startButton)
                root.children.removeAll(addShip.toList())
                root.children.removeAll(addShipCount.toList())
                fillCpuField()
                val fireHandler = FireHandler(user, cpu, mainStage, this)
                for (i in 0..9) {
                    for (j in 0..9) {
                        cpu[i][j].onAction = fireHandler
                    }
                }
            } else {
                val alert = Alert(AlertType.INFORMATION)
                alert.headerText = null
                alert.contentText = "Not enough ships on the field!"
                alert.showAndWait()
            }
        }
        root.add(startButton, 7, 12, 2, 1)
        val restart = Button("Restart")
        restart.onAction = EventHandler {
            mainStage.close()
            start(mainStage)
        }
        root.add(restart, 9, 12, 3, 1)
        mainStage.show()
    }

    private fun clearUserField() {
        for (i in 0..9) {
            for (j in 0..9) {
                user[i][j].showStatus = Status.clear
            }
        }
        addShipHandler.length = 0
        addShipHandler.count = Label("0")
        for (i in 0..3) {
            addShipCount[i].text = (4 - i).toString()
        }
        userShips = 0
    }

    private fun prepareWindow(mainStage: Stage) {
        mainStage.title = "Sea Battle"
        root = GridPane()
        this.mainStage = mainStage
        mainScene = Scene(root, 630.0, 480.0)
        mainStage.isResizable = false
        //root.setGridLinesVisible(true);


        val column = ColumnConstraints(30.0)
        column.halignment = HPos.CENTER
        val row = RowConstraints(30.0)
        row.valignment = VPos.CENTER
        for (i in 0..20) {
            root.columnConstraints.add(column)
        }
        for (i in 0..19) {
            root.rowConstraints.add(row)
        }
        createMenus()
        userShips = 0
        cpuShips = 0
        mainStage.scene = mainScene
    }

    private fun createMenus() {
        help = Button("Help")
        val helpHandler: EventHandler<ActionEvent?> = EventHandler {
            val message = Alert(AlertType.INFORMATION)
            message.title = "Help"
            message.headerText = null
            message.contentText = ("Press green button with digit to place the ship. Number on the button indicates length of the ship. " +
                    "Number near the button indicates amount of ships of this length.\n" +
                    "To change orientation press the button. V  means vertical orientation, H means horizontal orientation.\n" +
                    "Press Clear to clear your field.\n" +
                    "Press Start to start the game.\n" +
                    "Press Restart to start new game.")
            message.showAndWait()
        }
        help.onAction = helpHandler
        about = Button("About")
        about.onAction = EventHandler {
            val message = Alert(AlertType.INFORMATION)
            message.title = "About"
            message.headerText = null
            message.contentText = "Developed by Alexander Karpovich BrSU 2015"
            message.showAndWait()
        }
        toolBar = ToolBar(help, about)
        toolBar!!.prefWidthProperty().bind(mainStage.widthProperty())
        (mainScene.root as GridPane).add(toolBar, 0, 0, 21, 1)
    }

    private fun createFields() {
        orientation = Button("H")
        addShipHandler = UserAddShipHandler(this)
        user = Array(10) { i ->
            Array(10) { j ->
                val result = FieldCell(i, j)
                result.onAction = addShipHandler
                root.add(result, j, i + 1)
                result
            }
        }
        cpu = Array(10) { i ->
            Array(10) { j ->
                val result = FieldCell(i, j)
                root.add(result, j + 11, i + 1)
                result
            }
        }
    }

    private fun createAddButtons() {
        addShip = Array(4) { FieldCell(Status.unbroken, (it + 1).toString()) }
        addShipCount = Array(4) { Label() }
        for (i in 0..3) {
            addShip[i] = FieldCell(Status.unbroken, (i + 1).toString())
            addShip[i].setPrefSize(25.0, 25.0)
            addShipCount[i] = Label((4 - i).toString())
            val fi: Int = i
            addShip[i].onAction = EventHandler { event: ActionEvent? ->
                val button: Button? = event!!.source as Button?
                addShipHandler.length = (Integer.parseInt(button!!.text))
                addShipHandler.count = (addShipCount[(fi)])
            }
            root.add(addShip[i], 1, 12 + i)
            root.add(addShipCount[i], 2, 12 + i)
        }
        orientation.setPrefSize(30.0, 30.0)
        orientation.onAction = EventHandler setOnAction@{ event: ActionEvent? ->
            val button: Button = event!!.source as Button
            if ((button.text == "H")) {
                button.text = "V"
                return@setOnAction
            }
            button.text = "H"

        }
        root.add(orientation, 3, 12)
        clearField = Button("Clear")
        clearField.onAction = EventHandler { event: ActionEvent? -> clearUserField() }
        root.add(clearField, 5, 12, 2, 1)
    }

    private fun fillCpuField() {
        var flag: Boolean
        for (i in 4 downTo 2) {
            for (j in 0 until 4 - i + 1) {
                if (i == 2 && j == 2) continue
                do {
                    val orientation = (Math.random() * 2.0).toInt()
                    val side = (Math.random() * 2.0).toInt()
                    val pos = (Math.random() * (11.0 - i)).toInt()
                    val x = pos * orientation + 9 * side * abs(orientation - 1)
                    val y = pos * abs(orientation - 1) + 9 * side * orientation
                    flag = (trySet(x, y, orientation, i))
                    if (!flag) continue
                    place(x, y, orientation, i)
                } while (!flag)
            }
        }
        do {
            val orientation = (Math.random() * 2.0).toInt()
            val x = (Math.random() * 8.0).toInt() + 1
            val y = (Math.random() * 8.0).toInt() + 1
            flag = (trySet(x, y, orientation, 2))
            if (!flag) continue
            place(x, y, orientation, 2)
        } while (!flag)
        for (i in 0..3) {
            do {
                val x = (Math.random() * 9.0).toInt()
                val y = (Math.random() * 9.0).toInt()
                flag = trySet(x, y, 0, 2)
                if (!flag) continue
                place(x, y, 0, 1)
            } while (!flag)
        }
        cpuShips = 10
    }

    private fun place(x: Int, y: Int, orientation: Int, length: Int) {
        when (orientation) {
            0 -> {
                for (i in 0 until length) {
                    cpu[x][y + i].trueStatus = Status.unbroken
                }
            }
            1 -> {
                for (i in 0 until length) {
                    cpu[x + i][y].trueStatus = Status.unbroken
                }
            }
        }
    }

    private fun trySet(x: Int, y: Int, orientation: Int, length: Int): Boolean {
        when (orientation) {
            0 -> {
                if (y + (length) > 10) return false
                var i = 0
                while (i < (length)) {
                    if (!checkField(x, y + i)) return false
                    i++
                }
            }
            1 -> {
                if (x + (length) > 10) return false
                var i = 0
                while (i < (length)) {
                    if (!checkField(x + i, y)) {
                        return false
                    }
                    i++
                }
            }
        }
        return true
    }

    private fun checkField(x: Int, y: Int): Boolean {
        var result = true
        for (i in -1..1) {
            for (j in -1..1) {
                if ((i != 0 || j != 0) && isInField(x + i, y + j)) {
                    result = cpu[x + i][y + j].trueStatus != Status.unbroken && result
                }
            }
        }
        return result
    }


    fun main() {
        launch()
    }

}

fun isInField(x: Int, y: Int): Boolean {
    return x in 0..9 && y in 0..9
}

fun main() {
    Main().main()
}