package htw_berlin.de.mapmanager;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class PoiListAdapter extends ArrayAdapter<Node> {

    private List<Node> dataSet;
    private int lastPosition = -1;

    private static final int LAYOUT_LIST_ITEM = R.layout.poi_list_item;

    // View lookup cache
    private static class ViewHolder {
        TextView poiName;
        //ImageView imageView;
    }

    public PoiListAdapter(List<Node> data, Context context) {
        super(context, LAYOUT_LIST_ITEM, data);
        this.dataSet = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Node node = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(LAYOUT_LIST_ITEM, parent, false);

            viewHolder.poiName = (TextView) convertView.findViewById(R.id.lv_textView);
            //viewHolder.imageView = (ImageView) convertView.findViewById(R.id.lv_imageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.poiName.setText(MainActivity.graph.getNodeAsText(node));

        // alternative
        // viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath(), 500, 250));
        /* OLD, image representation in list
        File nodeImageFile = PersistenceManager.getNodeImageFile(node.id);
        if(!nodeImageFile.exists()){
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Uri nodeImageUri = Uri.fromFile(nodeImageFile);
            viewHolder.imageView.setImageURI(nodeImageUri);
        }
        */

        // Return the completed view to render on screen
        return convertView;
    }
}