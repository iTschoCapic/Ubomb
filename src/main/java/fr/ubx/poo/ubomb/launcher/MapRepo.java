package fr.ubx.poo.ubomb.launcher;

public interface MapRepo {

    MapLevel load(String string);

    String export(MapLevel mapLevel);

    MapLevel loadnoc(String string);

    String exportnoc(MapLevel mapLevel);
}
