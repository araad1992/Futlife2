package com.ideamosweb.futlife.EventBus;

import com.squareup.otto.Bus;

/**
 * Creado por Deimer Villa on 3/04/17.
 * Función:
 */
public class StationBus {

    private static Bus bus = new Bus();

    public static Bus getBus() {
        return bus;
    }

}
