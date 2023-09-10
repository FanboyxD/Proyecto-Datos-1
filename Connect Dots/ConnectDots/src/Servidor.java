import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {
    public static void main(String[] args) throws IOException {
        List<Integer> puertosDisponibles = new ArrayList<>();
        puertosDisponibles.add(12345);
        puertosDisponibles.add(12346);
        puertosDisponibles.add(12347);
        puertosDisponibles.add(12348);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        while (!puertosDisponibles.isEmpty()) {
            int puerto = puertosDisponibles.remove(0); // Tomar el primer puerto disponible

            try (ServerSocket serverSocket = new ServerSocket(puerto)) {
                System.out.println("Servidor escuchando en el puerto " + puerto);

                while (true) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Cliente conectado desde " + socket.getInetAddress().getHostAddress());

                    // Manejar la conexión en un hilo separado
                    executor.submit(() -> {
                        try {
                            // Esperar hasta que haya datos disponibles en el flujo de entrada del socket
                            while (socket.getInputStream().available() == 0) {
                                Thread.sleep(100); // Esperar 100 milisegundos antes de volver a verificar
                            }

                            // Leer un array JSON de coordenadas
                            ArrayNode receivedCoordinates = objectMapper.readValue(socket.getInputStream(), ArrayNode.class);

                            // Procesar las coordenadas
                            double x1 = receivedCoordinates.get(0).asDouble();
                            double y1 = receivedCoordinates.get(1).asDouble();
                            double x2 = receivedCoordinates.get(2).asDouble();
                            double y2 = receivedCoordinates.get(3).asDouble();

                            System.out.println("Coordenadas recibidas: (" + x1 + ", " + y1 + ") y (" + x2 + ", " + y2 + ")");

                            // Aquí puedes realizar las operaciones necesarias, como dibujar la línea o realizar otros cálculos.

                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
