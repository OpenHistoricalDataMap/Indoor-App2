package htw_berlin.de.mapmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import htw_berlin.de.mapmanager.graph.Graph;
import htw_berlin.de.mapmanager.ui.adapter.PoiListAdapter;

/**
 * Initial activity with the 2 buttons
 */
public class StartActivity extends AppCompatActivity {

    public static Graph graph;
    private Button aufnahmeButton;
    private Button navigationButton;

    static final Graph emptyGraph() {
        return new Graph();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initGUI();
    }


    /**
     * Initializes gui elements such as buttons, lists,... with onclick listeners, etc...
     */
    private void initGUI() {
        aufnahmeButton = (Button) findViewById(R.id.btnAufnahme);
        aufnahmeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMainActivity();
            }
        });


        navigationButton = (Button) findViewById(R.id.btnNavigation);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWhereAmIActivity();
            }
        });
    }

    /**
     * Creates and starts an intent and goes to the main activity
     */
    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Creates and starts an intent and goes to the where am I activity
     */
    private void gotoWhereAmIActivity() {
        Intent intent = new Intent(this, WhereAmIActivity.class);
        startActivity(intent);
    }
}
