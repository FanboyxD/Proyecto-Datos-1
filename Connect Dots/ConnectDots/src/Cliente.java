import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;


public class Cliente extends Application {
    private static final int GRID_SIZE = 4;
    private static final double POINT_RADIUS = 10.0;
    private ObjectMapper objectMapper;
    private GridPane gridPane;
    private double[] selectedCoordinates = new double[4]; // Para almacenar las coordenadas de los puntos seleccionados
    private Line line;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        objectMapper = new ObjectMapper();
        gridPane = new GridPane();
        createPointGrid();
        gridPane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(gridPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cliente");
        primaryStage.show();
        connectToServer();
    }

    private void createPointGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Circle circle = new Circle(POINT_RADIUS, Color.BLACK);
                // Agregar un margen a cada círculo
                GridPane.setMargin(circle, new Insets(70, 30, 30, 70)); // Ajusta los valores según tus necesidades
                gridPane.add(circle, col, row);
                int finalRow = row; // Declarar finalRow como efectivamente final
                int finalCol = col; // Declarar finalCol como efectivamente final
                circle.setOnMouseClicked(event -> {
                    double x = finalCol; // Multiplica por 2 para aumentar la distancia
                    double y = finalRow; // Multiplica por 2 para aumentar la distancia
                    // Almacenar las coordenadas de los puntos seleccionados
                    if (selectedCoordinates[0] == 0) {
                        selectedCoordinates[0] = x;
                        selectedCoordinates[1] = y;
                    } else if (selectedCoordinates[2] == 0) {
                        selectedCoordinates[2] = x;
                        selectedCoordinates[3] = y;

                        // Crear la línea y agregarla a la escena
                        enviarCoordenadas(selectedCoordinates);
                        drawLine(selectedCoordinates);
                        selectedCoordinates = new double[4]; // Reiniciar el arreglo
                    }
                    System.out.println("Coordenada en [" + finalRow + "][" + finalCol + "]: X=" + x + ", Y=" + y);
                });
            }
        }
    }

    private void drawLine(double[] coordinates) {
        if (line != null) {
            gridPane.getChildren().remove(line); // Eliminar la línea anterior
        }

        // Convertir las coordenadas de la matriz a las coordenadas de la interfaz
        double x1 = coordinates[0] * 60 + 70;
        double y1 = coordinates[1] * 60 + 70;
        double x2 = coordinates[2] * 60 + 70;
        double y2 = coordinates[3] * 60 + 70;

        // Crear la línea
        line = new Line(x1, y1, x2, y2);
        gridPane.getChildren().add(line);
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

    private void enviarCoordenadas(double[] coordinates) {
        try (Socket socket = new Socket("localhost", 12345)) {
            // Crear un array JSON de coordenadas
            ArrayNode coordenadas = objectMapper.createArrayNode();
            coordenadas.add(coordinates[0]);
            coordenadas.add(coordinates[1]);
            coordenadas.add(coordinates[2]);
            coordenadas.add(coordinates[3]);
            // Enviar el array JSON al servidor
            objectMapper.writeValue(socket.getOutputStream(), coordenadas);
            System.out.println("Coordenadas enviadas al servidor: " + coordinates[0] + ", " + coordinates[1] + " y " + coordinates[2] + ", " + coordinates[3]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}