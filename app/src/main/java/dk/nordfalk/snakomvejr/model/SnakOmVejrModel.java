package dk.nordfalk.snakomvejr.model;

public class SnakOmVejrModel {
    private static SnakOmVejrModel instance = null;



    public static SnakOmVejrModel getInstance() {
        if (instance == null) instance = new SnakOmVejrModel();
        return instance;
    }
}
