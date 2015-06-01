package motocitizen.Activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private OnFragmentInteractionListener mListener;

    private ImageButton newMessageButton;
    private EditText mcNewMessageText;
    private String currentText;

    View newMessageArea;

    /// Сообщения
    //private List<AccidentMessage> records = new ArrayList();
    /* Список сообщений */
    //private ListView listView;
    /// Адаптер для отображения сообщений
    //private MessageListAdapter adapter;

    private View mcDetMessagesTable;

    public static DetailMessagesFragment newInstance(int accID, String userName) {
        DetailMessagesFragment fragment = new DetailMessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ACCIDENT_ID, accID);
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_messages, container, false);

        newMessageButton = (ImageButton) viewMain.findViewById(R.id.mc_new_message_send);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mcNewMessageText.getText().toString();
                new SendMessageRequest(new SendMessageCallback(), getActivity(), accidentID, text);
                newMessageButton.setEnabled(false);
            }
        });

        newMessageButton.setEnabled(false);

        mcNewMessageText = (EditText) viewMain.findViewById(R.id.mc_new_message_text);
        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

        mcDetMessagesTable = viewMain.findViewById(R.id.mc_det_messages_table);

        newMessageArea = viewMain.findViewById(R.id.mc_new_message_area);

        update();

//  ListAdapter
/*

        listView = (ListView)viewMain.findViewById(R.id.message_list);
        adapter = new MessageListAdapter(getActivity(), records);
        listView.setAdapter(adapter);
*/
        return viewMain;
    }

    protected void update() {
//  ListAdapter
/*
        if(records.size() > 0) {
            records.clear();
        }
        for(Map.Entry<Integer, AccidentMessage> entry : currentPoint.messages.entrySet()) {
            AccidentMessage value = entry.getValue();
            records.add(value);
        }
*/
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();

        for (int i : accident.getSortedMessagesKeys()) {
            messageView.addView(accident.messages.get(i).createRow(getActivity(), userName));
        }
        setupAccess();
    }

    public void notifyDataSetChanged() {
        update();
        mcNewMessageText.setText("");
    }

    private final TextWatcher mcNewMessageTextListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String temp = s.toString().replaceAll("\\s", "");
            if (temp.length() == 0) {
                newMessageButton.setEnabled(false);
            } else {
                newMessageButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void setupAccess() {
        if (Role.isStandart()) {
            newMessageArea.setVisibility(View.VISIBLE);
        } else {
            newMessageArea.setVisibility(View.INVISIBLE);
        }
    }

    private class SendMessageCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            Context context = getActivity();
            try {
                String response = result.getString("result");
                switch (response) {
                    case "OK":
                        new AccidentsRequest(new UpdateAccidentsCallback(), context);
                        return;
                    case "READONLY":
                        Toast.makeText(context, context.getString(R.string.not_have_rights_error), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (JSONException e) {
                try {
                    String response = result.getString("error");
                    if(response.equals("internet_not_avaible"))
                        Toast.makeText(context, context.getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, context.getString(R.string.error) + response, Toast.LENGTH_LONG).show();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.parse_error), Toast.LENGTH_LONG).show();
                }
            }
            newMessageButton.setEnabled(true);
        }
    }

    private class UpdateAccidentsCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            mcNewMessageText.setText("");
            try {
                AccidentsGeneral.points.update(result.getJSONArray("list"));
                ((AccidentDetailsActivity) getActivity()).update();
                update();
            } catch (JSONException e) {
                //TODO Нельзя наглухо ловить исключения.
            }
        }
    }
}
