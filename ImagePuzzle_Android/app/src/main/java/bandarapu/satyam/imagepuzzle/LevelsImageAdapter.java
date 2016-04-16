package bandarapu.satyam.imagepuzzle;

import android.content.Context;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.GridView;
    import android.widget.ImageView;

public class LevelsImageAdapter extends BaseAdapter {
    private Context mContext;

    // Constructor
    public LevelsImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mImageIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, GridView.AUTO_FIT));
            imageView.setScaleType(ImageView.ScaleType.MATRIX);
            imageView.setPadding(5, 5, 5, 5);
            if(GameUtilities.GetGameLevel(mContext)==position)
            {
                imageView.setImageResource(R.drawable.selectedlevel);
            }
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setBackgroundResource(mImageIds[position]);
        return imageView;
    }

    // Keep all Images in array
    public Integer[] mImageIds = {
            R.drawable.level1, R.drawable.level2, R.drawable.level3,
            R.drawable.level4, R.drawable.level5, R.drawable.level6,
            R.drawable.level7, R.drawable.level8
    };
}