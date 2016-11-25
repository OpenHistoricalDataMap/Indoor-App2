package htw_berlin.de.mapmanager;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import htw_berlin.de.mapmanager.graph.Edge;
import htw_berlin.de.mapmanager.graph.Node;
import htw_berlin.de.mapmanager.persistence.PersistenceManager;

public class ConnectionListAdapter extends ArrayAdapter<Node> implements CompoundButton.OnCheckedChangeListener{

    private final Node parentNode;
    private ArrayList<Node> dataSet;
    private int lastPosition = -1;



    // View lookup cache
    private static class ViewHolder {
        TextView poiName;
        //ImageView imageView; OLD
        CheckBox cbReachable;
        CheckBox cbBarrierefrei;
    }

    public ConnectionListAdapter(Node parentNode, ArrayList<Node> data, Context context) {
        super(context, R.layout.connection_list_item, data);
        this.parentNode = parentNode;
        this.dataSet = data;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Node node = getSelectedItem(buttonView);
        System.out.println("buttonView.getId()" + buttonView.getId());
        System.out.println("reachable" + R.id.cb_reachable);

        switch (buttonView.getId())
        {
            case R.id.cb_reachable:
                if(isChecked &! node.hasEdgeToNode(parentNode.id) &! parentNode.hasEdgeToNode(node.id)){
                    System.out.println("Added");
                    int weight = 1;
                    node.addEdge(parentNode, weight);
                    parentNode.addEdge(node, weight);
                }
                else if(!isChecked && node.hasEdgeToNode(parentNode.id) && parentNode.hasEdgeToNode(node.id)){
                    System.out.println("Removed");
                    node.removeEdgeToNode(parentNode.id);
                    parentNode.removeEdgeToNode(node.id);
                }
                break;
            case R.id.cb_barrierefrei:
                System.out.println("TODO IMPLEMENT cb_barrierefrei WHEN CHECKBOX CHANGES");
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
        Node node = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.connection_list_item, parent, false);

            viewHolder.poiName = (TextView) convertView.findViewById(R.id.poiName);
            //viewHolder.imageView = (ImageView) convertView.findViewById(R.id.listRowDetailsImage);
            viewHolder.cbReachable = (CheckBox) convertView.findViewById(R.id.cb_reachable);
            viewHolder.cbBarrierefrei = (CheckBox) convertView.findViewById(R.id.cb_barrierefrei);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.poiName.setTag(position);
        //viewHolder.imageView.setTag(position);
        viewHolder.cbReachable.setTag(position);
        viewHolder.cbBarrierefrei.setTag(position);

        viewHolder.poiName.setText(MainActivity.graph.getNodeAsText(node));

        // alternative
        // viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath(), 500, 250));
        /* OLD
        File nodeImageFile = PersistenceManager.getNodeImageFile(node.id);
        if(!nodeImageFile.exists()){
            viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Uri nodeImageUri = Uri.fromFile(nodeImageFile);
            viewHolder.imageView.setImageURI(nodeImageUri);
        }
        */

        Edge edgeToChild = parentNode.getEdgeToNode(node.id);
        boolean childReachable = edgeToChild != null;
        viewHolder.cbReachable.setChecked(childReachable);
        viewHolder.cbReachable.setOnCheckedChangeListener(this);

        if(childReachable) // connection exists
            viewHolder.cbBarrierefrei.setChecked(edgeToChild.isBarrierefrei());

        // Return the completed view to render on screen
        return convertView;
    }
}