package org.blockjump.server.objects;

public class User {

    private String name, email;
    private long   score, userId;

    public User(String name, String email, long score, long userId) {
        this.name = name;
        this.email = email;
        this.score = score;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

}
