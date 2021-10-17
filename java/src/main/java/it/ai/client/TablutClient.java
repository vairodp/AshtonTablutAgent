package it.ai.client;

import com.google.gson.Gson;
import it.ai.Configuration;
import it.ai.Mapper;
import it.ai.Player;
import it.ai.protocol.Action;
import it.ai.protocol.State;
import it.ai.protocol.Turn;
import it.ai.util.StreamUtils;
import lombok.SneakyThrows;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;


public class TablutClient implements Runnable {
    private final Logger logger = Logger.getLogger(TablutClient.class.getName());

    private Player player;
    private Mapper mapper;
    private Socket playerSocket;
    private DataInputStream in;
    private DataOutputStream out;
    private Gson gson;
    private State state;

    /**
     * Creates a new player initializing the sockets and the logger
     *
     * @param ipAddress The ipAddress of the server
     */
    public TablutClient(Player player, Mapper mapper, String ipAddress) throws IOException {
        this.player = player;
        this.mapper = mapper;
        int port = player.getTeam().equals(Turn.WHITE) ? Configuration.WHITE_PORT : Configuration.BLACK_PORT;
        this.playerSocket = new Socket(ipAddress, port);
        this.out = new DataOutputStream(playerSocket.getOutputStream());
        this.in = new DataInputStream(playerSocket.getInputStream());
        this.gson = new Gson();
    }

    /**
     * Creates a new player initializing the sockets and the logger. The server
     * is supposed to be communicating on the same machine of this player.
     */
    public TablutClient(Player player, Mapper mapper) throws IOException {
        this(player, mapper, "localhost");
    }

    @SneakyThrows
    @Override
    public void run() {
        this.send(this.player.getName());

        while (true) {
            updateState();

            if (state.getTurn().equals(player.getTeam())) {
                it.ai.game.State gameState = mapper.mapToGameState(state);
                it.ai.game.Action playerAction = player.getAction(gameState);
                Action action = mapper.mapToProtocolAction(gameState, playerAction);
                logger.info("Moving from " + action.getFrom() + " to " + action.getTo());
                send(action);
            } else if (state.getTurn().equals(Turn.DRAW)) {
                logger.info("DRAW!");
                System.exit(0);
            } else if (this.player.getTeam().equals(Turn.WHITE)) {
                if (this.state.getTurn().equals(Turn.WHITE_WIN)) {
                    logger.info("YOU WIN!");
                    System.exit(0);
                } else if (this.state.getTurn().equals(Turn.BLACK_WIN)) {
                    logger.info("YOU LOSE!");
                    System.exit(0);
                }
            } else {
                if (this.state.getTurn().equals(Turn.WHITE_WIN)) {
                    logger.info("YOU LOSE!");
                    System.exit(0);
                } else if (this.state.getTurn().equals(Turn.BLACK_WIN)) {
                    logger.info("YOU WIN!");
                    System.exit(0);
                }
                logger.info("Waiting for your opponent action ... ");
            }
        }

    }

    protected void send(Object obj) throws IOException {
        String json = this.gson.toJson(obj);
        StreamUtils.writeString(this.out, json);
    }

    protected <T> T read(Class<T> classOfT) throws IOException {
        String value = StreamUtils.readString(this.in);
        return this.gson.fromJson(value, classOfT);
    }

    protected void updateState() throws IOException, ClassNotFoundException {
        this.state = read(State.class);
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
