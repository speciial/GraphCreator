/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jgraph;

import java.io.Serializable;
import java.util.function.Supplier;
import jgraph.JGraphAdapter.MyEdge;

/**
 *
 * @author lennaertn
 */
//hilfsklasse für Kantengewichte an ZufallsGraphen
public class MyEdgeSupplier {
 
    public static <T> Supplier<T> createSupplier(Class<? extends T> clazz)
    {
        return (Supplier<T> & Serializable) () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                throw new RuntimeException("Supplier failed", ex);
            }
        };
    }
    public static Supplier<MyEdge> createDefaultWeightedEdgeSupplier()
    {
        return createSupplier(MyEdge.class);
    }
}
