package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ExpandableListView;

public class ManagePOIConnectionsActivity extends AppCompatActivity {

        // more efficient than HashMap for mapping integers to objects
        SparseArray<Group> groups = new SparseArray<Group>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manage_poiconnections);

        /*
         * Get information about the connections
         */
            Intent intent = getIntent();
            String poiId = intent.getStringExtra(MainActivity.EXTRA_MESSAGE_POI_ID);
            // TODO: using the poiId, get the POI object from the list
            String poiName = "McDonald's";

            // TODO: get the ArrayList of images of the POI
            // TODO: get the edges and corresponding nodes (other POIs) that are directly connected to the current POI






            // TODO : get real data
            createData();
            ExpandableListView listView = (ExpandableListView) findViewById(R.id.expandableListView);
            MyExpandableListAdapter adapter = new MyExpandableListAdapter(this, groups);
            listView.setAdapter(adapter);
        }

        public void createData() {
            for (int j = 0; j < 5; j++) {
                Group group = new Group("Test " + j);
                for (int i = 0; i < 5; i++) {
                    group.children.add("Sub Item" + i);
                }
                groups.append(j, group);
            }
        }



    }