package course.labs.graphicslab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Vilagra on 28.03.2017.
 */

public class Data {
    static Bitmap[] bitmaps = new Bitmap[9];
    static public int mDisplayWidth, mDisplayHeight;
    static void initialize(Context ctx) {
        bitmaps[0] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.belik);
        bitmaps[1] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.roman);
        bitmaps[2] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.levenko);
        bitmaps[3] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.oleg);
        bitmaps[4] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.djonni);
        bitmaps[5] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.jana);
        bitmaps[6] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.sveta);
        bitmaps[7] = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.lena);
    }

    public static int getmDisplayWidth() {
        return mDisplayWidth;
    }

    public static void setmDisplayWidth(int mDisplayWidth) {
        Data.mDisplayWidth = mDisplayWidth;
    }

    public static int getmDisplayHeight() {
        return mDisplayHeight;
    }

    public static void setmDisplayHeight(int mDisplayHeight) {
        Data.mDisplayHeight = mDisplayHeight;
    }
}
