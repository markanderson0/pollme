package com.example.mark.pollmeads;

public class CreatePoll {
    private String category, password, longitude, latitude, question, answer1, answer2, answer3, answer4, answer5, deviceId, privacy, pollId;

    public CreatePoll() {
    }

    public CreatePoll(String question, String password, String latitude,
                      String longitude, String category, String answer1, String answer2,
                      String answer3, String answer4, String answer5, String deviceId,
                      String pollId, String privacy) {
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.answer5 = answer5;
        this.category = category;
        this.deviceId = deviceId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.password = password;
        this.privacy = privacy;
        this.pollId = pollId;
        this.question = question;

    }

    public String getCategory() {
        return category;
    }

    public String getPassword() {
        return password;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public String getAnswer5() {
        return answer5;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPollId() {
        return pollId;
    }

    public String getPrivacy() { return privacy; }
}
