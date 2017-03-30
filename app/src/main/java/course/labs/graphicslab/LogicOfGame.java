package course.labs.graphicslab;

/**
 * Created by Vilagra on 29.03.2017.
 */

public class LogicOfGame {
    private static LogicOfGame logicOfGame = new LogicOfGame();
    private int currentLevel =1;
    private int score;
    private int missed;
    private int scoreForNextLevel=50;
    private int missedForLose=3;
    private Result result;
    int speed;


    enum Result{
        WIN,LOSE
    }
    private LogicOfGame(){

    }
    public void resetForNewGame(){
        resetForNextLevel();
        currentLevel=1;
    }

    public void resetForNextLevel(){
        score=0;
        missed=0;
        result=null;
    }
    public void increaseLevel(){
        currentLevel++;
    }

    public int getSpeed() {
        return (currentLevel-1)*50;
    }

    public void increaseScore(int i){
        score+=i;
        if(score>=scoreForNextLevel){
            result=Result.WIN;
        }
    }
    public void increaseMissed(){
        missed++;
        if(missed>=missedForLose){
            result=Result.LOSE;
        }
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public static LogicOfGame getLogicOfGame() {
        return logicOfGame;
    }

    public static void setLogicOfGame(LogicOfGame logicOfGame) {
        LogicOfGame.logicOfGame = logicOfGame;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public Result getResult() {
        return result;
    }
}
