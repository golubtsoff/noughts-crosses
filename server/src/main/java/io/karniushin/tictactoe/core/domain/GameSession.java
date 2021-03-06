package io.karniushin.tictactoe.core.domain;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class GameSession {

    public static final int BOARD_DIMENSION = 10;

    private final UUID id;

    private String name;

    private final short[][] board;

    private final UUID ownerId;

    private UUID opponentId;

    private int ownerTurnCount;

    private int opponentTurnCount;

    private GameStatus status = GameStatus.WAITING_FOR_OPPONENT;

    private WinResult win;

    private int threshold;

    private long lastTurnDate;

    private boolean isOwnerX = true;

    public GameSession(UUID ownerId) {
        this(ownerId, UUID.randomUUID().toString(), new short[BOARD_DIMENSION][BOARD_DIMENSION]);
    }

    public GameSession(UUID ownerId, String name, int threshold) {
        this(ownerId, name, new short[BOARD_DIMENSION][BOARD_DIMENSION], threshold);
    }

    public GameSession(UUID ownerId, String name, short[][] board) {
        this(ownerId, name, board, BOARD_DIMENSION);
    }

    public GameSession(UUID ownerId, String name, short[][] board, int threshold) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ownerId = ownerId;
        this.board = board;
        this.threshold = Math.min(threshold, BOARD_DIMENSION);
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public UUID getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(UUID opponentId) {
        if (opponentId != null) {
            this.opponentId = opponentId;
            status = GameStatus.IN_PROGRESS;
        }
    }

    public boolean isOwner(UUID playerId) {
        return Objects.equals(ownerId, playerId);
    }

    public UUID getId() {
        return id;
    }

    public short[][] getBoard() {
        return board;
    }

    public void set(UUID playerId, int x, int y) {
        if (isFree(x, y) && isInRange(x, y)) {
            board[x][y] = isOwner(playerId) ? (short) 1 : -1;
            lastTurnDate = new Date().getTime();
        }
        incTurnCounter(playerId);
    }

    public GameStatus getStatus() {
        return status;
    }

    public WinResult getWin() {
        return win;
    }

    public boolean isWaiting() {
        return status == GameStatus.WAITING_FOR_OPPONENT;
    }

    public boolean isFinished() {
        return status == GameStatus.FINISHED;
    }

    public boolean isInProgress() {
        return status == GameStatus.IN_PROGRESS;
    }

    protected void incTurnCounter(UUID playerId) {
        if (isOwner(playerId)) {
            this.ownerTurnCount++;
        } else {
            this.opponentTurnCount++;
        }
    }

    public boolean canMakeTurn(UUID playerId) {
        return isOwner(playerId) ? ownerTurnCount - opponentTurnCount < 1 : opponentTurnCount - ownerTurnCount < 1;
    }

    public void setStatus(final GameStatus status) {
        this.status = status;
    }

    public void setWin(final WinResult win) {
        if (!isFinished()) {
            this.status = GameStatus.FINISHED;
        }
        this.win = win;
    }

    public boolean isFree(int x, int y) {
        return board[x][y] == 0;
    }

    public boolean isInRange(int x, int y) {
        return x >= 0 && x < BOARD_DIMENSION && y >= 0 && y < BOARD_DIMENSION;
    }

    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getOwnerTurnCount() {
        return ownerTurnCount;
    }

    public int getOpponentTurnCount() {
        return opponentTurnCount;
    }

    public long getLastTurnDate() {
        return lastTurnDate;
    }

    public boolean isOwnerX() {
        return isOwnerX;
    }

    public void setOwnerX(final boolean ownerX) {
        isOwnerX = ownerX;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GameSession that = (GameSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public final static class GameSessionBuilder {

        private final short[][] board;
        private final UUID ownerId;
        private String name;
        private Integer threshold;
        private UUID opponentId;
        private GameStatus status;
        private int ownerTurnCount;
        private int opponentTurnCount;

        public GameSessionBuilder(UUID ownerId) {
            this.ownerId = ownerId;
            this.name = UUID.randomUUID().toString();
            this.board = new short[BOARD_DIMENSION][BOARD_DIMENSION];
        }

        public GameSessionBuilder(UUID ownerId, short[][] board) {
            this.ownerId = ownerId;
            this.name = UUID.randomUUID().toString();
            this.board = board;
        }

        public GameSessionBuilder withOpponentId(UUID opponentId) {
            this.opponentId = opponentId;
            return this;
        }

        public GameSessionBuilder withStatus(GameStatus status) {
            this.status = status;
            return this;
        }

        public GameSessionBuilder withThreshold(int threshold) {
            this.threshold = threshold;
            return this;
        }

        public GameSessionBuilder withOwnerTurnCount(int ownerTurnCount) {
            this.ownerTurnCount = ownerTurnCount;
            return this;
        }

        public GameSessionBuilder withOpponentTurnCount(int opponentTurnCount) {
            this.opponentTurnCount = opponentTurnCount;
            return this;
        }

        public GameSessionBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public GameSession build() {
            GameSession session = new GameSession(ownerId, name, board);
            session.setOpponentId(opponentId);
            if (status != null) {
                session.setStatus(status);
            }
            if (threshold != null) {
                session.setThreshold(threshold);
            }
            session.name = name;
            session.ownerTurnCount = ownerTurnCount;
            session.opponentTurnCount = opponentTurnCount;
            return session;
        }
    }
}
