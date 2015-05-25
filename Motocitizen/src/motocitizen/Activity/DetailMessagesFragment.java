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

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.general.Accident;
import motocitizen.app.general.AccidentsGeneral;
import motocitizen.app.general.user.Role;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.SendMessageRequest;
import motocitizen.startup.Startup;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private OnFragmentInteractionListener mListener;

    private ImageButton newMessageButton;
    private EditText mcNewMessageText;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_messages, container, false);

        newMessageButton = (ImageButton) viewMain.findViewById(R.id.mc_new_message_send);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Startup.isOnline()) {
                    String text = mcNewMessageText.getText().toString();
                    int currentId = AccidentsGeneral.getCurrentPointID();
                    Map<String, String> post = new HashMap<>();
                    post.put("login", AccidentsGeneral.auth.getLogin());
                    post.put("passhash", AccidentsGeneral.auth.makePassHash());
                    post.put("id", String.valueOf(currentId));
                    post.put("text", text);
                    JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
                    if (request != null) {
                        (new SendMessageRequest((AccidentDetailsActivity) getActivity(), currentId)).execute(request);
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.inet_not_available), Toast.LENGTH_LONG).show();
                }
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
        Accident accident = ((AccidentDetailsActivity)getActivity()).getCurrentPoint();

        for (int i : accident.getSortedMessagesKeys()) {
            messageView.addView(accident.messages.get(i).createRow(getActivity(), userName));
        }
        setupAccess();
    }

    public void notifyDataSetChanged() {
        update();
//  ListAdapter
//        adapter.notifyDataSetChanged();
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
}
