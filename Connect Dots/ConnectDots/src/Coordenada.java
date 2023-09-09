import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordenada {
    @JsonProperty("x")
    private double x;
    
    @JsonProperty("y")
    private double y;

    // Constructor sin argumentos
    public Coordenada() {
        // Deja los valores en 0.0 por defecto
        this.x = 0.0;
        this.y = 0.0;
    }

    // Constructor con argumentos
    public Coordenada(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Getters y setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}