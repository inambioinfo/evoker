import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.util.Locale;
import java.io.File;
import java.io.IOException;

public class PlotPanel extends JPanel {

    private JFreeChart jfc;
    private PlotData data;
    private String title, xlab, ylab;
    static NumberFormat nf = NumberFormat.getInstance(Locale.US);
    static {
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
    }


    PlotPanel(String title, PlotData pd){
        this.title = title;
        this.xlab = String.valueOf("X");
        this.ylab = String.valueOf("Y");
        this.data = pd;

        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(300,300));
        this.setMaximumSize(new Dimension(300,300));
    }

    void refresh(){
        this.removeAll();
        XYSeriesCollection xysc = data.generatePoints();
        if (xysc != null){
            add(generatePlot(xysc));
            add(generateInfo());
        }else{
            this.setBackground(Color.WHITE);
            add(Box.createVerticalGlue());
            JLabel l = new JLabel("No data found for "+title);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(l);
            add(Box.createVerticalGlue());
        }
    }

    void saveToFile(File f) throws IOException {
        ChartUtilities.saveChartAsPNG(f, jfc,400,400);
    }

    private JPanel generateInfo(){
        JPanel foo = new JPanel();
        foo.setBackground(Color.white);
        foo.add(new JLabel("MAF: " + nf.format(data.getMaf())));
        foo.add(new JLabel("GPC: " + nf.format(data.getGenopc())));
        foo.add(new JLabel("HWE pval: " + formatPValue(data.getHwpval())));

        return foo;
    }

    private ChartPanel generatePlot(XYSeriesCollection xysc) {

        jfc = ChartFactory.createScatterPlot(title, xlab, ylab, xysc,
                PlotOrientation.VERTICAL, false, false, false);

        XYPlot thePlot = jfc.getXYPlot();
        thePlot.setBackgroundPaint(Color.white);
        thePlot.setOutlineVisible(false);
        
        

        XYItemRenderer xyd = thePlot.getRenderer();
        Shape dot = new Ellipse2D.Double(-1.5,-1.5,3,3);
        xyd.setSeriesShape(0, dot);
        xyd.setSeriesShape(1, dot);
        xyd.setSeriesShape(2, dot);
        xyd.setSeriesShape(3, dot);
        xyd.setSeriesPaint(0, Color.BLUE);
        xyd.setSeriesPaint(1, new Color(180,180,180));
        xyd.setSeriesPaint(2, Color.GREEN);
        xyd.setSeriesPaint(3, Color.RED);

        xyd.setBaseToolTipGenerator(new ZitPlotToolTipGenerator());
        
        ChartPanel cp = new ChartPanel(jfc);
        cp.setDisplayToolTips(true);
        cp.setDismissDelay(10000);
        cp.setInitialDelay(0);
        cp.setReshowDelay(0);
        
        return cp;
    }

    public double getMaxDim(){
        double range = data.getMaxDim() - data.getMinDim();
        return data.getMaxDim() + 0.05*range;
    }

    public double getMinDim(){
        double range = data.getMaxDim() - data.getMinDim();
        return data.getMinDim() - 0.05*range;
    }

    public void setDimensions(double min, double max){
        if (jfc != null){
            jfc.getXYPlot().setRangeAxis(new LinkedAxis(ylab,min,max));
            jfc.getXYPlot().setDomainAxis(new LinkedAxis(xlab,min,max));
            jfc.getXYPlot().getDomainAxis().setRange(min,max);
            jfc.getXYPlot().getRangeAxis().setRange(min,max);
        }
    }

    public static String formatPValue(double pval){
         DecimalFormat df;
        //java truly sucks for simply restricting the number of sigfigs but still
        //using scientific notation when appropriate
        if (pval < 0.0001){
            df = new DecimalFormat("0.0E0", new DecimalFormatSymbols(Locale.US));
        }else{
            df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        }
        return df.format(pval, new StringBuffer(), new FieldPosition(NumberFormat.INTEGER_FIELD)).toString();
    }

    class ZitPlotToolTipGenerator extends StandardXYToolTipGenerator {

        public ZitPlotToolTipGenerator(){
            super();
        }

        public String generateToolTip(XYDataset dataset, int series, int item){
            return data.getIndInClass(series,item);
        }
    }
}
