package co.edu.unal.tictactoe.Multiplayer;

public class Match {


    private String owner;
    private String guest;

    private boolean ownerPlaying;
    private int turn;

    private int lastmove;
    private boolean available;

    private String refID;

    public Match() {
    }

    public Match(String owner, String guest, boolean ownerPlaying, int turn, int lastmove, boolean available, String refID) {
        this.owner = owner;
        this.guest = guest;
        this.ownerPlaying = ownerPlaying;
        this.turn = turn;
        this.lastmove = lastmove;
        this.available = available;
        this.refID = refID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGuest() {
        return guest;
    }

    public void setGuest(String guest) {
        this.guest = guest;
    }

    public boolean isOwnerPlaying() {
        return ownerPlaying;
    }

    public void setOwnerPlaying(boolean ownerPlaying) {
        this.ownerPlaying = ownerPlaying;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getLastmove() {
        return lastmove;
    }

    public void setLastmove(int lastmove) {
        this.lastmove = lastmove;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getRefID() {
        return refID;
    }

    public void setRefID(String refID) {
        this.refID = refID;
    }

}
