import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Application {
    private static final int GRID_SIZE = 4;
    private static final double POINT_RADIUS = 10.0;
    private ObjectMapper objectMapper;
    private Pane pane;
    private Circle[] circles = new Circle[GRID_SIZE * GRID_SIZE];
    private Circle selectedCircle;
    private List<Line> lines = new ArrayList<>(); // Lista para mantener las líneas dibujadas

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        objectMapper = new ObjectMapper();
        pane = new Pane();
        createPointGrid();
        Scene scene = new Scene(pane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cliente");
        primaryStage.show();
        connectToServer();
    }

    private void createPointGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Circle circle = new Circle((col + 0.5) * 600 / GRID_SIZE, (row + 0.5) * 600 / GRID_SIZE, POINT_RADIUS, Color.BLACK);
                circles[row * GRID_SIZE + col] = circle;
                pane.getChildren().add(circle);

                circle.setOnMouseClicked(event -> {
                    if (selectedCircle == null) {
                        selectedCircle = circle;
                    } else if (selectedCircle != circle) {
                        drawLine(selectedCircle, circle);
                        selectedCircle = null; // Reiniciar la selección
                    }
                });
            }
        }
    }

    private void drawLine(Circle startCircle, Circle endCircle) {
        double startX = startCircle.getCenterX();
        double startY = startCircle.getCenterY();
        double endX = endCircle.getCenterX();
        double endY = endCircle.getCenterY();

        // Verificar si los puntos son adyacentes en sentido horizontal o vertical
        double gridSize = 600.0 / GRID_SIZE;
        if ((Math.abs(startX - endX) == gridSize && Math.abs(startY - endY) < 1) ||
            (Math.abs(startY - endY) == gridSize && Math.abs(startX - endX) < 1)) {
            Line line = new Line(startX, startY, endX, endY);
            lines.add(line); // Agregar la línea a la lista
            pane.getChildren().add(line); // Agregar la línea a la escena
            enviarCoordenadas(startX, startY, endX, endY);
        }
    }

    private void connectToServer() {
        String servidorHost = "localhost"; // Cambia esto al servidor real
        int servidorPuerto = 12345; // Cambia esto al puerto del servidor real
        try (Socket socket = new Socket(servidorHost, servidorPuerto)) {
            // No es necesario hacer nada aquí, solo conectarse al servidor.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarCoordenadas(double startX, double startY, double endX, double endY) {
        try (Socket socket = new Socket("localhost", 12345)) {
            // Crear un array JSON de coordenadas
            ArrayNode coordenadas = objectMapper.createArrayNode();
            coordenadas.add(startX);
            coordenadas.add(startY);
            coordenadas.add(endX);
            coordenadas.add(endY);
            // Enviar el array JSON al servidor
            objectMapper.writeValue(socket.getOutputStream(), coordenadas);
            System.out.println("Coordenadas enviadas al servidor: " + startX + ", " + startY + " y " + endX + ", " + endY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
