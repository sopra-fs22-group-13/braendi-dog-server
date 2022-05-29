/*
    dog-server is the server component of an online web implementation of dog.
    Copyright (C) 2022  Luca Zwahlen, Simona Borghi, Sandro Vonlanthen, Anton Crazzolara, Shitao Zeng

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package ch.uzh.ifi.hase.soprafs22.game.gameInstance.data;

import ch.uzh.ifi.hase.soprafs22.game.constants.COLOR;

import java.util.ArrayList;
import java.util.Map;

public class BoardData {
    private ArrayList<String> board;

    private ArrayList<String> redGoal;
    private ArrayList<String> greenGoal;
    private ArrayList<String> blueGoal;
    private ArrayList<String> yellowGoal;

    private int redBase;
    private int greenBase;
    private int blueBase;
    private int yellowBase;

    private Map<Long, COLOR> colorMapping;

    private String lastPlayedCard;

    public BoardData(ArrayList<String> board, ArrayList<String> redGoal, ArrayList<String> greenGoal, ArrayList<String> blueGoal, ArrayList<String> yellowGoal, int redBase, int greenBase, int blueBase, int yellowBase) {
        this.board = new ArrayList<>(board);
        this.redGoal = new ArrayList<>(redGoal);
        this.greenGoal = new ArrayList<>(greenGoal);
        this.blueGoal = new ArrayList<>(blueGoal);
        this.yellowGoal = new ArrayList<>(yellowGoal);
        this.redBase = redBase;
        this.greenBase = greenBase;
        this.blueBase = blueBase;
        this.yellowBase = yellowBase;
    }

    public void setColorMapping(Map<Long, COLOR> colorMapping) {
        this.colorMapping = colorMapping;
    }

    public void setLastPlayedCard(String lastPlayedCard) {
        this.lastPlayedCard = lastPlayedCard;
    }

    public ArrayList<String> getBoard() {
        return new ArrayList<>(board);
    }

    public ArrayList<String> getRedGoal() {
        return new ArrayList<>(redGoal);
    }

    public ArrayList<String> getGreenGoal() {
        return new ArrayList<>(greenGoal);
    }

    public ArrayList<String> getBlueGoal() {
        return new ArrayList<>(blueGoal);
    }

    public ArrayList<String> getYellowGoal() {
        return new ArrayList<>(yellowGoal);
    }

    public int getRedBase() {
        return redBase;
    }

    public int getGreenBase() {
        return greenBase;
    }

    public int getBlueBase() {
        return blueBase;
    }

    public int getYellowBase() {
        return yellowBase;
    }

    public Map<Long, COLOR> getColorMapping() {
        return colorMapping;
    }

    public String getLastPlayedCard() {
        return lastPlayedCard;
    }
}
