package com.example.mark.pollmeads;

public class Results {
    private String pollId;
    private int result1, result2, result3, result4, result5;

    public Results(){
    }

    public Results(int ans1, int ans2, int ans3, int ans4, int ans5) {
        this.result1 = ans1;
        this.result2 = ans2;
        this.result3 = ans3;
        this.result4 = ans4;
        this.result5 = ans5;
    }

    public String getPollId() {
        return pollId;
    }

    public int getResult1() {
        return result1;
    }

    public int getResult2() {
        return result2;
    }

    public int getResult3() {
        return result3;
    }

    public int getResult4() {
        return result4;
    }

    public int getResult5() {
        return result5;
    }
}
