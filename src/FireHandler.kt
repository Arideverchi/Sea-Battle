import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class FireHandler internal constructor(private var user: Array<Array<FieldCell>>, private var cpu: Array<Array<FieldCell>>, private var stage: Stage, private var main: Main) : EventHandler<ActionEvent> {
    private var countUserShips: Int = 10
    private var userShips: IntArray = intArrayOf(4, 3, 2, 1)
    private var countCpuShips: Int = 10
    private var firstSet: LinkedList<Point> = LinkedList()
    private var mode: Mode? = Mode.RECONNAISSANCE
    private var lastHit: Point = Point()
    override fun handle(event: ActionEvent) {
        val cell = event.source as FieldCell
        when (cell.trueStatus) {
            Status.clear -> {
                cell.showStatus = Status.missed
                cell.isDisable = true
                makeAMove()
            }
            Status.unbroken -> {
                cell.showStatus = (Status.injured)
                if (checkKilled(cell.x, cell.y, cpu)) {
                    countCpuShips--
                    killShip(cell.x, cell.y, cpu)
                }
                if (countCpuShips == 0) {
                    showResult("You won!")
                }
            }
        }
    }

    private fun makeAMove() {
        when (mode) {
            Mode.RECONNAISSANCE -> searchEnemy()
            Mode.EXECUTION -> execute()
        }
    }

    private fun searchEnemy() {
        if (countUserShips == 0) return
        if (countUserShips == 10) {
            val a = (Math.random() * firstSet.size).toInt()
            val point = firstSet[a]
            when (fire(point.x, point.y)) {
                Status.killed -> {
                    countUserShips--
                    searchEnemy()
                }
                Status.injured -> execute()
                Status.missed -> firstSet.removeAt(a)
            }
            return
        }
        val point = calculateSearch()
        when (fire(point.x, point.y)) {
            Status.killed -> searchEnemy()
            Status.injured -> execute()
        }
    }

    private fun execute() {
        var bottom: Int
        var top: Int
        var right: Int
        var left: Int
        val r = generateRMatrix()
        val pos = injuredLengthAndOrientation(lastHit.x, lastHit.y)
        var target = Point(-1, -1)
        if (pos[0] == 1) {
            var max = 0
            for (i in -1..1) {
                for (j in -1..1) {
                    val x = lastHit.x + i
                    val y = lastHit.y + j
                    if (isInField(x, y) && abs(i) != abs(j) && r[x][y] > max) {
                        max = r[x][y]
                        target = Point(x, y)
                    }
                }
            }
        } else {
            when (pos[1]) {
                0 -> {
                    val x = lastHit.x
                    run {
                        left = lastHit.y
                        right = left
                    }
                    while (left >= 0 && user[x][left].showStatus == Status.injured) {
                        left--
                    }
                    while (right < 10 && user[x][right].showStatus == Status.injured) {
                        right++
                    }
                    var max = 0
                    if (isInField(x, left) && r[x][left] > max) {
                        max = r[x][left]
                        target = Point(x, left)
                    }
                    if (isInField(x, right) && r[x][right] > max) {
                        target = Point(x, right)
                    }
                }
                1 -> {
                    val y = lastHit.y
                    run {
                        top = lastHit.x
                        bottom = top
                    }
                    while (top >= 0 && user[top][y].showStatus == Status.injured) {
                        top--
                    }
                    while (bottom < 10 && user[bottom][y].showStatus == Status.injured) {
                        bottom++
                    }
                    var max = 0
                    if (isInField(top, y) && r[top][y] > max) {// FIXME: 12.12.2015 wrong parameters
                        max = r[top][y]
                        target = Point(top, y)
                    }
                    if (isInField(bottom, y) && r[bottom][y] > max) {
                        target = Point(bottom, y)
                    }
                }
            }
        }
        when (fire(target.x, target.y)) {
            Status.injured -> execute()
            Status.killed -> {
                mode = Mode.RECONNAISSANCE
                searchEnemy()
            }
        }
    }

    private fun injuredLengthAndOrientation(x: Int, y: Int): IntArray {
        val result = intArrayOf(1, 0)
        var i = x
        while (isInField(++i, y) && user[i][y].showStatus == Status.injured) {
            result[0]++
            result[1] = 1
        }
        i = x
        while (isInField(--i, y) && user[i][y].showStatus == Status.injured) {
            result[0]++
            result[1] = 1
        }
        i = y
        while (isInField(x, ++i) && user[x][i].showStatus == Status.injured) {
            result[0]++
            result[1] = 0
        }
        i = y
        while (isInField(x, --i) && user[x][i].showStatus == Status.injured) {
            result[0]++
            result[1] = 0
        }
        return result
    }

    private fun fire(x: Int, y: Int): Status {
        when (user[x][y].showStatus) {
            Status.clear -> {
                user[x][y].showStatus = Status.missed
                return Status.missed
            }
            Status.unbroken -> {
                user[x][y].showStatus = (Status.injured)
                if (checkKilled(x, y, user)) {
                    userShips[killShip(x, y, user) - 1]--
                    countUserShips--
                    if (countUserShips == 0) {
                        for (i in 0..9) {
                            for (j in 0..9) {
                                cpu[i][j].showStatus = cpu[i][j].trueStatus!!
                            }
                        }
                        showResult("I won!")
                        return Status.clear
                    }
                    mode = Mode.RECONNAISSANCE
                    return Status.killed
                } else {
                    mode = Mode.EXECUTION
                    lastHit.x = x
                    lastHit.y = y
                    return Status.injured
                }
            }
        }
        println("if you see this message you have a bug")
        return Status.clear// carefully it might cause a bug
    }

    private fun calculateSearch(): Point {
        val r = generateRMatrix()
        var max = 0
        for (i in 0..9) {
            max = max(max, r[i].max() ?: -1)
        }
        val list = ArrayList<Point>()
        for (i in 0..9) {
            for (j in 0..9) {
                if (r[i][j] == max) {
                    list.add(Point(i, j))
                }
            }
        }
        val a = (Math.random() * list.size).toInt()
        return list[a]
    }

    private fun generateRMatrix(): Array<IntArray> {
        val r = Array(10) { IntArray(10) }
        for (i in 0..9) {
            for (j in 0..9) {
                for (orientation in 0..1) {
                    for (size in 0..3) {
                        for (k in 1..userShips[size]) {
                            if (size == 0 && orientation == 1) continue
                            if (trySet(i, j, orientation, size + 1)) {
                                place(i, j, orientation, size + 1, r)
                            }
                        }
                    }
                }
            }
        }
        return r
    }

    private fun place(x: Int, y: Int, orientation: Int, length: Int, r: Array<IntArray>) {
        when (orientation) {
            0 -> {
                for (i in 0 until length) {
                    r[x][y + i]++
                }
            }
            1 -> {
                for (i in 0 until length) {
                    r[x + i][y]++
                }
            }
        }
    }

    private fun trySet(x: Int, y: Int, orientation: Int, length: Int): Boolean {
        when (orientation) {
            0 -> {
                if (y + length > 10) return false
                for (i in 0 until length) {
                    if (!checkField(user[x][y + i])) return false
                }
            }
            1 -> {
                if (x + length > 10) return false
                for (i in 0 until length) {
                    if (!checkField(user[x + i][y])) return false
                }
            }
        }
        return true
    }

    private fun checkField(cell: FieldCell): Boolean {
        return cell.trueStatus != Status.missed && cell.trueStatus != Status.killed
    }

    private fun checkEmpty(cell: FieldCell): Boolean = cell.showStatus == Status.missed || cell.showStatus == Status.clear

    private fun checkKilled(x: Int, y: Int, field: Array<Array<FieldCell>>): Boolean {
        for (xi in -1..1) {
            for (yi in -1..1) {
                if (abs(xi) == abs(yi)) continue
                var xCurr = x
                var yCurr = y
                while (isInField(xCurr, yCurr)) {
                    if (field[xCurr][yCurr].trueStatus == Status.unbroken) {
                        return false
                    }
                    if (checkEmpty(field[xCurr][yCurr])) {
                        break
                    }
                    xCurr += xi
                    yCurr += yi
                }
            }
        }
        return true
    }

    private fun killShip(x: Int, y: Int, field: Array<Array<FieldCell>>): Int {
        killCell(x, y, field)
        var result = 1
        var i = x
        while (isInField(++i, y) && field[i][y].showStatus == Status.injured) {
            killCell(i, y, field)
            result++
        }
        i = x
        while (isInField(--i, y) && field[i][y].showStatus == Status.injured) {
            killCell(i, y, field)
            result++
        }
        i = y
        while (isInField(x, ++i) && field[x][i].showStatus == Status.injured) {
            killCell(x, i, field)
            result++
        }
        i = y
        while (isInField(x, --i) && field[x][i].showStatus == Status.injured) {
            killCell(x, i, field)
            result++
        }
        return result
    }

    private fun killCell(x: Int, y: Int, field: Array<Array<FieldCell>>) {
        field[x][y].showStatus = Status.killed
        for (i in -1..1) {
            for (j in -1..1) {
                if (i == 0 && j == 0) continue
                if (isInField(x + i, y + j) && field[x + i][y + j].showStatus == Status.clear) {
                    field[x + i][y + j].showStatus = Status.missed
                }
            }
        }
    }

    private fun showResult(message: String) {
        val gameResult = Alert(AlertType.CONFIRMATION)
        gameResult.title = message
        gameResult.headerText = null
        gameResult.contentText = "Do you want to continue?"
        if (gameResult.showAndWait().get() == ButtonType.OK) {
            main.start(stage)
        } else {
            stage.close()
        }
    }

    init {
        for (i in 0..9) {
            for (j in 0..9) {
                if ((i + j + 1) % 4 == 0) {
                    firstSet.add(Point(i, j))
                }
            }
        }
    }
}

internal enum class Mode { RECONNAISSANCE, EXECUTION }
internal data class Point(var x: Int = 0, var y: Int = 0)