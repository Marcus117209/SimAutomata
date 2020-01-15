/*
 * Copyright (C) 2020 victo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Principal;

import Automata.AFD;
import Automata.AFND;
import Automata.TransicionAFD;
import Automata.TransicionAFND;
import Automata.TransicionL;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author victo
 */
public class Grafica extends javax.swing.JPanel {

    private mxGraph graph = new mxGraph();
    Object parent;
    mxStylesheet stylesheet;
    ArrayList<String> estados = new ArrayList<>();
    ArrayList<Object> objEstados = new ArrayList<>();
    
    Object q0;

    /**
     * Creates new form Grafica
     */
    public Grafica() {
        initComponents();

        parent = graph.getDefaultParent();

        stylesheet = graph.getStylesheet();

        Hashtable<String, Object> estiloEstado = new Hashtable<>();
        estiloEstado.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        estiloEstado.put(mxConstants.STYLE_FONTSIZE, 20);

        Hashtable<String, Object> estiloEFinal = new Hashtable<String, Object>();
        estiloEFinal.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_DOUBLE_ELLIPSE);
        estiloEFinal.put(mxConstants.STYLE_FONTSIZE, 20);

        Map<String, Object> edgeStyle = new HashMap<String, Object>();
        edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#000000");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#000000");
        stylesheet.setDefaultEdgeStyle(edgeStyle);

        stylesheet.putCellStyle("ESTADOFINAL", estiloEFinal);
        stylesheet.putCellStyle("ESTADO", estiloEstado);
        graph.setStylesheet(stylesheet);

        graph.getModel().beginUpdate();
        graph.setCellsLocked(true);
        graph.setVertexLabelsMovable(false);
        graph.setEdgeLabelsMovable(false);
    }

    //TODO excepciones
    private void generarEstados(HashSet<String> cjtoEstados, String estadoI, HashSet<String> estadosF) throws Exception{
        estados = new ArrayList<>(cjtoEstados);
        
        //Añadir los estados al grafo
        try {
            for (String estado : estados) {
                if(estadosF.contains(estado))
                     objEstados.add(graph.insertVertex(parent, null, estado, 100, 200, 50, 50, "ESTADOFINAL"));
                else 
                    objEstados.add(graph.insertVertex(parent, null, estado, 100, 200, 50, 50, "ESTADO"));

            }
            
            q0 = graph.insertVertex(parent, null, "", 100, 100, 50, 50, "opacity=0"); //incial
            if(estadoI.equals(""))
                throw new Exception("Error: estado inicial no definido!");
            else
                graph.insertEdge(parent, null, "", q0, objEstados.get(estados.indexOf(estadoI))); //flechita inicial
            

        } finally {
            graph.getModel().endUpdate();

        }
    }

    public mxGraphComponent generarAFD(AFD automata, HashSet<String> cjtoEstados) throws Exception{

        try {
            generarEstados(cjtoEstados, automata.getEstadoInicial(), automata.getEstadosFinales());
            
            if (!automata.getTransiciones().isEmpty()) {
                for (TransicionAFD t : automata.getTransiciones()) {
                    graph.insertEdge(parent, null, t.getSimbolo(), objEstados.get(estados.indexOf(t.getEstadoO())), objEstados.get(estados.indexOf(t.getEstadoD())), "rounded=1");
                }
            }
            

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

            layout.setInterRankCellSpacing(50.0);
            layout.setIntraCellSpacing(50.0);
            layout.setDisableEdgeStyle(false);
            layout.execute(graph.getDefaultParent());

        } finally {
            graph.getModel().endUpdate();
        }

        return new mxGraphComponent(graph);
    }

        public mxGraphComponent generarAFND(AFND automata, HashSet<String> cjtoEstados) throws Exception{

        try {
            generarEstados(cjtoEstados, automata.getEstadoInicial(), automata.getEstadosFinales());
            
            //Añadimos las transiciones que consumen simbolo
            if (!automata.getTransiciones().isEmpty()) {
                for (TransicionAFND t : automata.getTransiciones()) {
                    for(String estadoDestino : t.getDestinos())
                        graph.insertEdge(parent, null, t.getSimbolo(), objEstados.get(estados.indexOf(t.getOrigen())), objEstados.get(estados.indexOf(estadoDestino)), "rounded=1");
                }
            }

            //Añadimos las transiciones lambda
            if(!automata.getTransicionesL().isEmpty()) {
                for (TransicionL tl : automata.getTransicionesL()) {    //Por cada transicion lambda
                    for(String estadoDestino : tl.getDestinos())        //y por cada destino de esa T-L
                        graph.insertEdge(parent, null, "L", objEstados.get(estados.indexOf(tl.getOrigen())), objEstados.get(estados.indexOf(estadoDestino)), "rounded=1");
                }
            }
            
            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);

            layout.setInterRankCellSpacing(50.0);
            layout.setIntraCellSpacing(50.0);
            layout.setDisableEdgeStyle(false);
            layout.execute(graph.getDefaultParent());

        } finally {
            graph.getModel().endUpdate();
        }

        return new mxGraphComponent(graph);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
