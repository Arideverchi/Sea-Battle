package org.akar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class FireHandler implements EventHandler<ActionEvent> {
    public static final Random RANDOM = new Random();
    FieldCell[][] user;
    FieldCell[][] cpu;
    Integer countUserShips = 10;
    int[] userShips = {4, 3, 2, 1};
    Integer countCpuShips = 10;
    Stage stage;
    Main main;
    LinkedList<Point> firstSet;

    Mode mode = Mode.RECONNAISSANCE;
    Point lastHit = new Point();

    FireHandler(FieldCell[][] user, FieldCell[][] cpu, Stage stage, Main main) {
        this.user = user;
        this.cpu = cpu;
        this.stage = stage;
        this.main = main;
        firstSet = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if ((i + j + 1) % 4 == 0) {
                    firstSet.add(new Point(i, j));
                }
            }
        }
    }

    @Override
    public void handle(ActionEvent event) {
        FieldCell cell = (FieldCell) event.getSource();
        switch (cell.trueStatus) {
            case CLEAR:
                cell.setShowStatus(Status.MISSED);
                cell.setDisable(true);
                makeAMove();
                break;
            case UNBROKEN:
                cell.setShowStatus(Status.INJURED);
                if (checkKilled(cell.x, cell.y, cpu)) {
                    countCpuShips--;
                    killShip(cell.x, cell.y, cpu);
                }
                if (countCpuShips == 0) {
                    showResult("You won!");
                }
        }
    }

    private void makeAMove() {
        if (Objects.requireNonNull(mode) == Mode.RECONNAISSANCE) {
            searchEnemy();
        } else if (mode == Mode.DENYING) {
            deny();
        }
    }

    private void searchEnemy() {
        if (countUserShips.equals(0)) {
            return;
        }
        if (countUserShips.equals(10)) {
            int a =  RANDOM.nextInt(firstSet.size());
            Point point = firstSet.get(a);
            switch (fire(point.x, point.y)) {
                case KILLED:
                    countUserShips--;
                    searchEnemy();
                    break;
                case INJURED:
                    deny();
                    break;
                case MISSED:
                    firstSet.remove(a);
                    break;
            }
            return;
        }
        Point point = calculateSearch();
        switch (fire(point.x, point.y)) {
            case KILLED:
                searchEnemy();
                break;
            case INJURED:
                deny();
                break;
        }
    }

    private void deny() {
        int bottom;
        int top;
        int right;
        int left;
        int[][] r = generateRMatrix();
        int[] pos = injuredLengthAndOrientation(lastHit.x, lastHit.y);
        Point target = new Point(-1, -1);
        if (pos[0] == 1) {
            int max = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int x = lastHit.x + i;
                    int y = lastHit.y + j;
                    if (isInField(x, y) && Math.abs(i) != Math.abs(j) && r[x][y] > max) {
                        max = r[x][y];
                        target = new Point(x, y);
                    }
                }
            }
        } else {
            switch (pos[1]) {
                case 0:
                    int x = lastHit.x;
                    right = left = lastHit.y;
                    while (left >= 0 && user[x][left].getShowStatus().equals(Status.INJURED)) {
                        left--;
                    }
                    while (right < 10 && user[x][right].getShowStatus().equals(Status.INJURED)) {
                        right++;
                    }
                    int max = 0;

                    if (isInField(x, left) && r[x][left] > max) {
                        max = r[x][left];
                        target = new Point(x, left);
                    }
                    if (isInField(x, right) && r[x][right] > max) {
                        max = r[x][right];
                        target = new Point(x, right);
                    }
                    break;
                case 1:

                    int y = lastHit.y;
                    bottom = top = lastHit.x;
                    while (top >= 0 && user[top][y].getShowStatus().equals(Status.INJURED)) {
                        top--;
                    }
                    while (bottom < 10 && user[bottom][y].getShowStatus().equals(Status.INJURED)) {
                        bottom++;
                    }
                    max = 0;

                    if (isInField(top, y) && r[top][y] > max) {// FIXME: 12.12.2015 wrong parameters
                        max = r[top][y];
                        target = new Point(top, y);
                    }
                    if (isInField(bottom, y) && r[bottom][y] > max) {
                        max = r[bottom][y];
                        target = new Point(bottom, y);
                    }
                    break;
            }
        }
        switch (fire(target.x, target.y)) {
            case INJURED:
                deny();
                break;
            case KILLED:
                mode = Mode.RECONNAISSANCE;
                searchEnemy();
                break;
        }
    }

    private int[] injuredLengthAndOrientation(int x, int y) {
        int[] result = {1, 0};
        int i = x;
        while (isInField(++i, y) && user[i][y].getShowStatus().equals(Status.INJURED)) {
            result[0]++;
            result[1] = 1;
        }
        i = x;
        while (isInField(--i, y) && user[i][y].getShowStatus().equals(Status.INJURED)) {
            result[0]++;
            result[1] = 1;
        }
        i = y;
        while (isInField(x, ++i) && user[x][i].getShowStatus().equals(Status.INJURED)) {
            result[0]++;
            result[1] = 0;
        }
        i = y;
        while (isInField(x, --i) && user[x][i].getShowStatus().equals(Status.INJURED)) {
            result[0]++;
            result[1] = 0;
        }
        return result;
    }

    private Status fire(int x, int y) {
        //System.out.println(x + " " + y);
        try {
            switch (user[x][y].getShowStatus()) {

                case CLEAR:
                    user[x][y].setShowStatus(Status.MISSED);
                    return Status.MISSED;
                case UNBROKEN:
                    user[x][y].setShowStatus(Status.INJURED);
                    if (checkKilled(x, y, user)) {
                        userShips[killShip(x, y, user) - 1]--;
                        countUserShips--;
                        if (countUserShips == 0) {
                            for (int i = 0; i < 10; i++) {
                                for (int j = 0; j < 10; j++) {
                                    cpu[i][j].setShowStatus(cpu[i][j].trueStatus);
                                }
                            }
                            showResult("I won!");
                            return Status.CLEAR;
                        }
                        //System.out.println(userShips);
                        mode = Mode.RECONNAISSANCE;
                        return Status.KILLED;
                    } else {
                        mode = Mode.DENYING;
                        lastHit.x = x;
                        lastHit.y = y;
                        return Status.INJURED;
                    }
            }

            System.out.println("if you see this message you have a bug");
            return Status.CLEAR;// carefully it might cause a bug
        } catch (Exception e) {
            throw e;
        }
    }

    private Point calculateSearch() {
        int[][] r = generateRMatrix();
        int max = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                max = Math.max(max, r[i][j]);
            }
        }

        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (r[i][j] == max) {
                    list.add(new Point(i, j));
                }
            }
        }
        int a = RANDOM.nextInt(list.size());
        return list.get(a);
    }

    private int[][] generateRMatrix() {
        int[][] r = new int[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int orientation = 0; orientation < 2; orientation++) {
                    for (int size = 0; size < 4; size++) {
                        for (int k = 1; k < userShips[size] + 1; k++) {
                            if (size == 0 && orientation == 1) {
                                continue;
                            }
                            if (trySet(i, j, orientation, size + 1)) {
                                place(i, j, orientation, size + 1, r);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                //System.out.printf("%3d", r[i][j]);
            }
            //System.out.println();
        }
        return r;
    }

    private void place(int x, int y, int orientation, int length, int[][] r) {
        switch (orientation) {
            case 0:
                for (int i = 0; i < length; i++) {
                    r[x][y + i]++;
                }
                break;
            case 1:
                for (int i = 0; i < length; i++) {
                    r[x + i][y]++;
                }
                break;

        }
    }

    private Boolean trySet(Integer x, Integer y, Integer orientation, Integer length) {
        switch (orientation) {
            case 0:
                if (y + length >= 11) {
                    return Boolean.FALSE;
                }
                for (int i = 0; i < length; i++) {
                    if (!checkField(x, y + i)) {
                        return Boolean.FALSE;
                    }
                }
                break;
            case 1:
                if (x + length > 10) {
                    return Boolean.FALSE;
                }
                for (int i = 0; i < length; i++) {
                    if (!checkField(x + i, y)) {
                        return Boolean.FALSE;
                    }
                }
                break;
        }
        return Boolean.TRUE;
    }

    private boolean checkField(int x, int y) {
        return !user[x][y].trueStatus.equals(Status.MISSED) && !user[x][y].trueStatus.equals(Status.KILLED);
    }

    private boolean checkKilled(int x, int y, FieldCell[][] field) {
        int i = x;
        while (i < 10) {
            if (field[i][y].trueStatus.equals(Status.UNBROKEN)) {
                return false;
            }
            if (field[i][y].getShowStatus().equals(Status.MISSED) || field[i][y].getShowStatus().equals(Status.CLEAR)) {
                break;

            }
            ++i;
        }
        i = x;
        while (i >= 0) {
            if (field[i][y].trueStatus.equals(Status.UNBROKEN)) {
                return false;
            }
            if (field[i][y].getShowStatus().equals(Status.MISSED) || field[i][y].getShowStatus().equals(Status.CLEAR)) {
                break;
            }
            --i;
        }
        i = y;
        while (i <= 9) {
            if (field[x][i].trueStatus.equals(Status.UNBROKEN)) {
                return false;
            }
            if (field[x][i].getShowStatus().equals(Status.MISSED) || field[x][i].getShowStatus().equals(Status.CLEAR)) {
                break;
            }
            ++i;
        }
        i = y;
        while (i > -1) {
            if (field[x][i].trueStatus.equals(Status.UNBROKEN)) {
                return false;
            }
            if (field[x][i].getShowStatus().equals(Status.MISSED) || field[x][i].getShowStatus().equals(Status.CLEAR)) {
                break;
            }
            --i;
        }
        return true;
    }

    private int killShip(int x, int y, FieldCell[][] field) {
        killCell(x, y, field);
        int result = 1;
        int i = x;
        while (isInField(++i, y) && field[i][y].getShowStatus().equals(Status.INJURED)) {
            killCell(i, y, field);
            result++;
        }
        i = x;
        while (isInField(--i, y) && field[i][y].getShowStatus().equals(Status.INJURED)) {
            killCell(i, y, field);
            result++;
        }
        i = y;
        while (isInField(x, ++i) && field[x][i].getShowStatus().equals(Status.INJURED)) {
            killCell(x, i, field);
            result++;
        }
        i = y;
        while (isInField(x, --i) && field[x][i].getShowStatus().equals(Status.INJURED)) {
            killCell(x, i, field);
            result++;
        }
        return result;
    }

    private void killCell(int x, int y, FieldCell[][] field) {
        field[x][y].setShowStatus(Status.KILLED);
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (isInField(x + i, y + j) && field[x + i][y + j].getShowStatus().equals(Status.CLEAR)) {
                    field[x + i][y + j].setShowStatus(Status.MISSED);
                }
            }
        }
    }

    private boolean isInField(int x, int y) {
        return (x < 10 && x >= 0 && y < 10 && y >= 0);
    }

    private void showResult(String message) {
        Alert gameResult = new Alert(Alert.AlertType.CONFIRMATION);
        gameResult.setTitle(message);
        gameResult.setHeaderText(null);
        gameResult.setContentText("Do you want to continue?");
        if (gameResult.showAndWait().get() == ButtonType.OK) {
            main.start(stage);
        } else {
            stage.close();
        }
    }
}

enum Mode {
    RECONNAISSANCE, DENYING
}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Point() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;

        return x == point.x && y == point.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}