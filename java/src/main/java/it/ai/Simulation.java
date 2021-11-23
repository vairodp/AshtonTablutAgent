package it.ai;

import it.ai.game.Action;
import it.ai.game.tablut.Board;
import it.ai.game.tablut.TablutState;
import it.ai.game.tablut.ashton.AshtonTablutGame;
import it.ai.util.RandomUtils;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class Simulation {
    public static void main(String[] args) throws IOException {
        AshtonTablutGame game = new AshtonTablutGame(0);

        int matches = 1000000;
        int batchSize = 5000;

        long startTime = System.currentTimeMillis();
        for (int i=0; i<matches/batchSize; i++){
            simulate(game, batchSize, i);
        }
        System.out.println("running time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    @SneakyThrows
    private static void simulate(AshtonTablutGame game, int batchSize, int batch) {
        List<Board> boards = new ArrayList<>();
        List<Integer> turns = new ArrayList<>();
        List<Integer> winners = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        List<Integer> nactions = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            TablutState state = (TablutState) game.start();
            Optional<Integer> winner = game.getWinner(state);

            boards.add(state.getBoard());
            turns.add(state.getTurn());

            int count = 1;
            while (!winner.isPresent()) {
                List<Action> validActions = game.getValidActions(state);
                Action action = RandomUtils.choice(validActions);
                nactions.add(validActions.size());
                state = (TablutState) game.nextState(state, action);
                winner = game.getWinner(state);

                boards.add(state.getBoard());
                turns.add(state.getTurn());

                count++;
            }

            for (int j = 0; j < count; j++)
                winners.add(winner.get());
            counts.add(count);
        }

        System.out.println("avg states:" + mean(counts));
        System.out.println("avg actions:" + mean(nactions));

        BufferedWriter br = new BufferedWriter(new FileWriter("simulation_" + batch + ".csv"));
        StringBuilder sb = new StringBuilder("board,turns,winner\n");
        for (int i = 0; i < boards.size(); i++) {
            addBoard(sb, boards.get(i));
            sb.append(',');
            sb.append(turns.get(i));
            sb.append(',');
            sb.append(winners.get(i));
            sb.append('\n');
        }

        br.write(sb.toString());
        br.flush();
        br.close();
    }

    private static double mean(List<Integer> values) {
        int total = 0;
        for (int v : values)
            total += v;
        return total / (double) values.size();
    }

    private static void addBoard(StringBuilder sb, Board board) {
//        sb.append('"');
        sb.append('[');
        for (int i = 0; i < board.numberOfRows(); i++) {
            sb.append('[');
            for (int j = 0; j < board.numberOfColumns(); j++) {
                sb.append(String.format("%4d", board.get(i, j)));
            }
            sb.append(']');
            if (i < board.numberOfRows() - 1)
                sb.append("\n ");
        }
        sb.append(']');
//        sb.append('"');
    }
}
