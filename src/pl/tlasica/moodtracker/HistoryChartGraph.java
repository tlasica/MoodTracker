package pl.tlasica.moodtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by tomek on 04.03.14.
 */
public class HistoryChartGraph {

    DateFormat dayFormat = new SimpleDateFormat("MMM d");

    Calendar now = Calendar.getInstance();

    private static final int NUM_DAYS_BACK = 14;

    public Intent getBarChartPerDayIntent(Context aContext) {
        // load all entries from the database
        DatabaseHelper db = DatabaseHelper.getInstance( aContext.getApplicationContext());
        assert db!=null;

        List<MoodEntry> entries = db.getAll();
        if (entries.isEmpty()) {
            Log.d("BARCHART", "no entries loaded");
            return null;
        }
        // create data series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        Map<Mood, Integer[]> data = new HashMap<Mood, Integer[]>();

        // zbudowanie tabeli z datami od dzisiaj wstecz na 14 dni
        // przyjmujemy, że [0] to NUM_DAYS_BACK-1 dni wstecz,  past ---> now
        List<String> labels = new ArrayList<String>();
        int[] dayNums = new int[NUM_DAYS_BACK];
        for(int i=0; i<NUM_DAYS_BACK; ++i) {
            Calendar d = (Calendar)now.clone();
            d.add(Calendar.DAY_OF_YEAR, -i);
            // add label
            String dayLabel = dayFormat.format(d.getTime());
            labels.add(0, dayLabel );
            // zapamiętanie daynum
            int daynum = calToNum(d);
            dayNums[NUM_DAYS_BACK-i-1] = daynum;
        }
        Log.d("BARCHART", Arrays.toString( labels.toArray() ));

        // Fill data with array of zeros for each mood
        for(Mood m: Mood.values()) {
            Integer[] tab = new Integer[NUM_DAYS_BACK];
            Arrays.fill(tab, 0);
            data.put(m, tab);
        }

        // fill series with 0 for 14 days back from now
        // and set relevant index

        // Zbudowanie mapy z danymi
        for(MoodEntry e: entries) {
            int daynum = dateToNum(e.tstamp);
            // liczymy index w tablicy
            int idx = -1;
            for(int i=0; idx<0 && i<NUM_DAYS_BACK; i++) {
                if (dayNums[i]==daynum) idx=i;
            }
            // i dodajemy do danych
            if (idx>=0) {
                Integer[] values = data.get(e.mood);
                values[idx]++;
            }
            Log.d("BARCHAR", "entry mood:"+e.mood.toString()+ " date:"+ daynum + " idx:"+idx);
        }
        Log.d("BARCHART", "data map created");

        // Create 4 series for each mood
        for(Mood m: Mood.values()) {
            XYSeries s = new XYSeries(m.toString());
            Integer[] values  = data.get(m);
            for(int i=0; i<NUM_DAYS_BACK; ++i) {
                s.add(i, values[i]);
            }
            dataset.addSeries(s);
        }
        Log.d("BARCHART", "data series created");

        // create renderers
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        for(Mood m: Mood.values()) {
            int color = Color.parseColor(m.colorRGB());
            XYSeriesRenderer r = createRenderer( color );
            multiRenderer.addSeriesRenderer( r );
        }

        // set labels
        for(int i=0; i<NUM_DAYS_BACK; ++i) {
            multiRenderer.addXTextLabel(i, labels.get(i));
        }

        multiRenderer.setChartTitle("Mood history for last " + NUM_DAYS_BACK + " days");
        multiRenderer.setYTitle("Records");
        multiRenderer.setXTitle("Date");
        multiRenderer.setShowGridY(true);
        multiRenderer.setZoomButtonsVisible(true);
        multiRenderer.setZoomEnabled(true);
        multiRenderer.setPanEnabled(true, true);
        multiRenderer.setBarSpacing(.2);

        Intent intent = ChartFactory.getBarChartIntent(aContext, dataset, multiRenderer, BarChart.Type.DEFAULT);
        return intent;
    }


     private XYSeriesRenderer createRenderer(int color) {
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setLineWidth(1f);
        r.setFillPoints(true);
        r.setPointStyle(PointStyle.SQUARE);
        r.setColor(color);
        return r;

    }

    private static int dateToNum(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return calToNum(cal);
    }


    private static int calToNum(Calendar cal) {
        int acc = 0;
        acc += cal.get(Calendar.YEAR) * 10000;
        acc += (cal.get(Calendar.MONTH)+1) * 100;
        acc += cal.get(Calendar.DAY_OF_MONTH);
        return acc;
    }

}
