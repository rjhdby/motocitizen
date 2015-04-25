package motocitizen.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.app.mc.user.MCRole;
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
    //private List<MCMessage> records = new ArrayList();
    /* Список сообщений */
    //private ListView listView;
    /// Адаптер для отображения сообщений
    //private MessageListAdapter adapter;

    private View mcDetMessagesTable;

    public static DetailMessagesFragment newInstance(int param1) {
        DetailMessagesFragment fragment = new DetailMessagesFragment();
        Bundle args = new Bundle();
        args.putInt(ACCIDENT_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailMessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            accidentID = getArguments().getInt(ACCIDENT_ID);
        }
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
                    int currentId = MCAccidents.getCurrentPointID();
                    Map<String, String> post = new HashMap<>();
                    post.put("login", MCAccidents.auth.getLogin());
                    post.put("passhash", MCAccidents.auth.makePassHash());
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
        super.update();
//  ListAdapter
/*
        if(records.size() > 0) {
            records.clear();
        }
        for(Map.Entry<Integer, MCMessage> entry : currentPoint.messages.entrySet()) {
            MCMessage value = entry.getValue();
            records.add(value);
        }
*/
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        for (int i : currentPoint.getSortedMessagesKeys()) {
            messageView.addView(currentPoint.messages.get(i).createRow(getActivity()));
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void setupAccess() {
        if (MCRole.isStandart()) {
            newMessageArea.setVisibility(View.VISIBLE);
        } else {
            newMessageArea.setVisibility(View.INVISIBLE);
        }
    }
}
