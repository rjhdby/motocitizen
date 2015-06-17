package motocitizen.Activity;

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
            if (result.has("error")) {
                try {
                    Toast.makeText(getActivity(), result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Неизвестная ошибка" + result.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                new AccidentsRequest(new UpdateAccidentsCallback(), getActivity());
            }
            newMessageButton.setEnabled(true);
        }
    }

    private class UpdateAccidentsCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            mcNewMessageText.setText("");

            if (result.has("error")) {
                String error;
                try {
                    error = result.getString("error");
                } catch (JSONException e) {
                    error = "Неизвестная ошибка";
                }
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            } else {
                try {
                    AccidentsGeneral.points.update(result.getJSONArray("list"));
                    ((AccidentDetailsActivity) getActivity()).update();
                    update();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
