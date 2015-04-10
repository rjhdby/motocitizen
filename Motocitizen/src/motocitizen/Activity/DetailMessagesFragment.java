package motocitizen.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import motocitizen.app.mc.MCAccidents;
import motocitizen.main.R;
import motocitizen.network.JsonRequest;
import motocitizen.network.SendMessageRequest;
import motocitizen.startup.Startup;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailMessagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailMessagesFragment extends Fragment implements XmlClickable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button newMessageButton;
    private EditText mcNewMessageText;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailMessagesFragment newInstance(String param1, String param2) {
        DetailMessagesFragment fragment = new DetailMessagesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //newMessageButton = (Button) getActivity().findViewById(R.id.mc_new_message_send);

        //mcNewMessageText = (EditText) getActivity().findViewById(R.id.mc_new_message_text);
        //mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail_messages, container, false);
        newMessageButton = (Button) view.findViewById(R.id.mc_new_message_send);
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Startup.isOnline()) {
                    /*
                    String text = mcNewMessageText.getText().toString();
                    int currentId = MCAccidents.currentPoint.id;
                    Map<String, String> post = new HashMap<>();
                    post.put("login", MCAccidents.auth.getLogin());
                    post.put("passhash", MCAccidents.auth.makePassHash());
                    post.put("id", String.valueOf(currentId));
                    post.put("text", text);
                    JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
                    if (request != null) {
                        (new SendMessageRequest(this, currentId)).execute(request);
                    }*/
                } else {
                    //Toast.makeText(this, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
                }

            }
        });

        mcNewMessageText = (EditText) view.findViewById(R.id.mc_new_message_text);
        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);


//        newMessageButton = (Button) findViewById(R.id.mc_new_message_send);
//        newMessageButton.setOnClickListener(this);

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_detail_messages, container, false);
        return view;
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

/*        View view = inflater.inflate(R.layout.fragment_detail_messages, container, false);
        view.findViewById(R.id.mc_acc_details_general).setOnLongClickListener(detLongClick);
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        for (int i : currentPoint.getSortedMessagesKeys()) {
            messageView.addView(currentPoint.messages.get(i).createRow(this));
        }
*/
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

    public void myClickMethod(View v) {
            /*

        if (Startup.isOnline()) {
            String text = mcNewMessageText.getText().toString();
            int currentId = MCAccidents.currentPoint.id;
            Map<String, String> post = new HashMap<>();
            post.put("login", MCAccidents.auth.getLogin());
            post.put("passhash", MCAccidents.auth.makePassHash());
            post.put("id", String.valueOf(currentId));
            post.put("text", text);
            JsonRequest request = new JsonRequest("mcaccidents", "message", post, "", true);
            if (request != null) {
                (new SendMessageRequest(this, currentId)).execute(request);
            }
        } else {
            Toast.makeText(this, Startup.context.getString(R.string.inet_not_avaible), Toast.LENGTH_LONG).show();
        }
        */
    }
}
