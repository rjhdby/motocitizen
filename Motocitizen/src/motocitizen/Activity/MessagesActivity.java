package motocitizen.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.MCMessage;
import motocitizen.app.mc.MCPoint;
import motocitizen.main.R;
import motocitizen.messages.MessageListAdapter;

public class MessagesActivity extends ActionBarActivity {

    /// Сообщения
    private List<MCMessage> records = new ArrayList();

    /* Список сообщений */
    private ListView listView;

    /// Адаптер для отображения сообщений
    private MessageListAdapter adapter;

    private MCPoint currentPoint;

    public MessagesActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Bundle b = getIntent().getExtras();
        int messageID = b.getInt("messageID");
        currentPoint = MCAccidents.points.getPoint(messageID);

        for(Map.Entry<Integer, MCMessage> entry : currentPoint.messages.entrySet()) {
            MCMessage value = entry.getValue();
            records.add(value);
        }
        // zz
        /*
        listView = (ListView)findViewById(R.id.message_list);
        adapter = new MessageListAdapter(this,records);
        listView.setAdapter(adapter);
        */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
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
}
