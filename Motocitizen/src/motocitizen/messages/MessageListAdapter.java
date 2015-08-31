package motocitizen.messages;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import motocitizen.accident.Message;
import motocitizen.main.R;
import motocitizen.utils.Const;

public class MessageListAdapter extends ArrayAdapter<Message> {

    /// the Android Activity owning the ListView
    private final Activity activity;

    /// a list of gasoline records for display
    private final List<Message> records;

    public MessageListAdapter(Activity activity, List<Message> records) {
        super(activity, R.layout.row_message_list, records);
        this.activity = activity;
        this.records = records;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // create a view for the row if it doesn't already exist
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.row_message_list, null);
        }

        // populate row widgets from record data
        Message record = records.get(position);

        // get widgets from the view
        //TextView columnDate = (TextView) view.findViewById(R.id.columnDate);
        //columnDate.setText(Const.TIME_FORMAT.format(record.time.getTime()));

        TextView info = (TextView) view.findViewById(R.id.info);
        info.setText(Const.TIME_FORMAT.format(record.getTime()) + " - " + record.getOwner());

        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(record.getText());
        return view;
    }
}
