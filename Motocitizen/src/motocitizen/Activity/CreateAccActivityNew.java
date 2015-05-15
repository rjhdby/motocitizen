package motocitizen.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.main.R;

public class CreateAccActivityNew extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc_activity_new);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_acc_activity_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseResponse(JSONObject json) {
        if (json.has("result")) {
            try {
                String result = json.getString("result");
                if (result.contains("ID")) {
                    Toast.makeText(this, this.getString(R.string.send_success), Toast.LENGTH_LONG).show();
                    finish();
                } else if (result.equals("READONLY")) {
                    Toast.makeText(this, this.getString(R.string.not_have_rights_error), Toast.LENGTH_LONG).show();
                } else if (result.equals("PROBABLY SPAM")) {
                    Toast.makeText(this, this.getString(R.string.too_often_acts), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, this.getString(R.string.parse_error), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, this.getString(R.string.send_error), Toast.LENGTH_LONG).show();
        }
    }
}
