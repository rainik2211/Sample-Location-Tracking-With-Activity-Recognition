package rainiksoni.com.locationtracksample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rainiksoni on 14/02/17.
 */

public class CustomMarkerText {

    private final Activity context;

    private final ViewGroup container;

    private final TextView textView;

    private final View contentView;


    public CustomMarkerText(Activity context){
        this.context = context;

        container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.text_container, null);

        contentView = textView = (TextView) container.findViewById(R.id.text_view);

    }


    public TextView getTextView(){
        return textView;
    }

    public Bitmap makeIcon(String status){
        if (status != null){
            textView.setText(status);

        }
        return makeIcon();
    }

    private Bitmap makeIcon(){

        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        container.measure(measureSpec, measureSpec);

        int measuredWidth = container.getMeasuredWidth();
        int measuredHeight = container.getMeasuredHeight();

        container.layout(0, 0, measuredWidth, measuredHeight);

        Bitmap r = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        r.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(r);

        container.draw(canvas);
        return r;

    }




}
