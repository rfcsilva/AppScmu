package com.agrosmart;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class DataGraph {

    private String titulo;
    private LineGraphSeries<DataPoint> series;

    public DataGraph(String titulo, LineGraphSeries<DataPoint> series) {
        this.titulo = titulo;
        this.series = series;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LineGraphSeries<DataPoint> getSeries() {
        return series;
    }

    public void setSeries(LineGraphSeries<DataPoint> series) {
        this.series = series;
    }
}
