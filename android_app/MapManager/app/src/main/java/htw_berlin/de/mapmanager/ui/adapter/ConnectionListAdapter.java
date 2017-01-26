package htw_berlin.de.mapmanager.ui.adapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import htw_berlin.de.mapmanager.StartActivity;
import htw_berlin.de.mapmanager.compass.DefineEdgeActivity;
import htw_berlin.de.mapmanager.MainActivity;
import htw_berlin.de.mapmanager.R;
import htw_berlin.de.mapmanager.compass.Edge;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;


public class ConnectionListAdapter extends ArrayAdapter<Node> implements CompoundButton.OnCheckedChangeListener{

    private static final String LOG_TAG = "ConntectionListAdapter";

    private final Node parentNode;
    private ArrayList<Node> dataSet;
    private int lastPosition = -1;


    // View lookup cache
    private static class ViewHolder {
        TextView poiName;
        ImageView imageView; //OLD
        CheckBox cbReachable;
        //CheckBox cbBarrierefrei;
        Button defineEdge;
    }

    public ConnectionListAdapter(Node parentNode, ArrayList<Node> data, Context context) {
        super(context, R.layout.list_item_connection, data);
        this.parentNode = parentNode;
        this.dataSet = data;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Node selectedNode = getSelectedItem(buttonView);
        System.out.println("buttonView.getId()" + buttonView.getId());
        System.out.println("reachable" + R.id.cb_reachable);

        Edge edgeBetween = parentNode.getEdge(selectedNode);

        switch (buttonView.getId())
        {
            case R.id.cb_reachable:
                // if checked and no connection between the two exists yet -> add
                if(isChecked && (edgeBetween == null)/*selectedNode.getEdges().containsKey(parentNode) &! parentNode.getEdges().containsKey(selectedNode)*/){
                    boolean added = StartActivity.graph.addEdge(parentNode, selectedNode);
                    Log.d(LOG_TAG, "Added? " + added);
                    /*
                    Old graph
                    int weight = 1;
                    selectedNode.getEdges().put(parentNode, weight);
                    parentNode.getEdges().put(selectedNode, weight);
                    */
                }
                // if unchecked and a connection exists -> remove
                else if(!isChecked && (edgeBetween != null)/*selectedNode.getEdges().containsKey(parentNode) && parentNode.getEdges().containsKey(selectedNode)*/){
                    Edge edgeRemoved = StartActivity.graph.removeEdge(edgeBetween);
                    String text = "Removed null";
                    if(edgeRemoved != null){
                        text = "Removed " + edgeRemoved.toString();
                    }
                }
                break;
            case R.id.cb_barrierefrei:
                String s = "not impl";
                //throw new UnsupportedOperationException("TODO: implement");
                break;
            default:

                break;
        }
    }

    public Node getSelectedItem(View v) {
        int position=(Integer) v.getTag();
        return getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Node nodeAtPosition = getItem(position);
        Edge edgeBetween = parentNode.getEdge(nodeAtPosition);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_connection, parent, false);

            viewHolder.poiName = (TextView) convertView.findViewById(R.id.poiName);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_connection_list);
            viewHolder.cbReachable = (CheckBox) convertView.findViewById(R.id.cb_reachable);
            //viewHolder.cbBarrierefrei = (CheckBox) convertView.findViewById(R.id.cb_barrierefrei);
            viewHolder.defineEdge = (Button) convertView.findViewById(R.id.btnDefineEdge);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.poiName.setTag(position);
        viewHolder.imageView.setTag(position);
        viewHolder.cbReachable.setTag(position);
        //viewHolder.cbBarrierefrei.setTag(position);

        viewHolder.poiName.setText(nodeAtPosition.getId());


        File nodeImageFile = PersistenceManager.getNodeImageFile(nodeAtPosition.getId());
        if(!nodeImageFile.exists()){
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {

            Bitmap bitmap = loadLightweightBitmapFromFile(nodeImageFile);
            viewHolder.imageView.setImageBitmap(bitmap);


        }

        boolean childReachable = edgeBetween != null;
        viewHolder.cbReachable.setChecked(childReachable);
        viewHolder.cbReachable.setOnCheckedChangeListener(this);


        // define edge button action
        final Intent intent = new Intent(getContext(), DefineEdgeActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE_POI_ID,  parentNode.getId());
        //Give the DefineEdgeActivity the ID of the destination NodeInterface
        intent.putExtra(DefineEdgeActivity.POI_ID_DESTINATION, nodeAtPosition.getId());
        viewHolder.defineEdge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(intent);
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
    private Bitmap loadLightweightBitmapFromFile(File file){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(file.getPath(),options);
    }
}