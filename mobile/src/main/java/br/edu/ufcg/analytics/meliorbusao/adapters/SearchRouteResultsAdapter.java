package br.edu.ufcg.analytics.meliorbusao.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.ufcg.analytics.meliorbusao.R;

public class SearchRouteResultsAdapter extends SimpleCursorAdapter {

    private static final String[] defaultColumns = new String[]{"_id", "short_name", "long_name", "color", "line_name", "main_stops"};

    public SearchRouteResultsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public SearchRouteResultsAdapter(Context context, int layout, Cursor c, int[] to, int flags) {
        super(context, layout, c, defaultColumns, to, flags);
    }

    /**
     * Monta a view do Top Busão das Rotas (pesquisa de rotas)
     *
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView routeNameTxtView = (TextView) view.findViewById(R.id.route_name_text_view);
        routeNameTxtView.setText(cursor.getString(1));

        TextView routeLongNameTxtView = (TextView) view.findViewById(R.id.route_long_name_text_view);

        try {
            String[] routeMainStops = cursor.getString(5).split(" - ");
            String ida = routeMainStops[0];
            String volta = routeMainStops[1];

            routeLongNameTxtView.setText(ida);
            routeLongNameTxtView = (TextView) view.findViewById(R.id.route_long_name_text_view_volta);
            routeLongNameTxtView.setText(volta);

            ImageView busIcon = (ImageView) view.findViewById(R.id.suggestion_bus_icon);
            busIcon.setImageResource(R.drawable.ic_melior_busao);

            ImageView circle = (ImageView) view.findViewById(R.id.circle);
            circle.setColorFilter(Color.parseColor("#" + cursor.getString(3)));
            circle.setAlpha(160);

            ImageView rightArrow = (ImageView) view.findViewById(R.id.right_arrow);
            rightArrow.setColorFilter(Color.parseColor("#" + cursor.getString(3)));


        } catch (Exception e) {
            Log.e("SearchRouteResultsAdapter", e.getMessage());
        }


    }
}