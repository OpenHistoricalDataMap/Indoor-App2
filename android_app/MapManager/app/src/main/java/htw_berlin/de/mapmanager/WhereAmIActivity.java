package htw_berlin.de.mapmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import htw_berlin.de.mapmanager.graph.Node;

import htw_berlin.de.mapmanager.permissions.PermissionManager;

public class WhereAmIActivity extends AppCompatActivity implements View.OnClickListener {

    private Button findPos;
    private Node currentNode;
    private Button findWay;
    private TextView currentPos;
    private TextView errorMsg;
    private PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_am__i);

        setTitle("Start Navigation");
        currentNode = null;
        initPermissions();

        findPos = (Button) this.findViewById(R.id.btnWhereAmI_FindCurrent);
        findWay = (Button) this.findViewById(R.id.btnWhereAmI_FindWay);
        currentPos = (TextView) this.findViewById(R.id.tvWhereAmI_CurrentPos);
        errorMsg = (TextView) this.findViewById(R.id.tvWhereAmI_ErrorMessage);

        findPos.setOnClickListener(this);
        findWay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.equals(findPos))
        {

        }
        else{
            if(currentNode == null)
            {
                this.errorMsg.setText("Your current Position couldn't be detected");
            }

        }
    }

    private void initPermissions(){
        permissionManager = new PermissionManager(this);
        permissionManager.checkWifiPermissions();
    }

}
