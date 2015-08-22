package motocitizen.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.accident.Message;
import motocitizen.app.general.popups.MessagesPopup;
import motocitizen.app.general.user.Role;
import motocitizen.content.Content;
import motocitizen.draw.Rows;
import motocitizen.draw.Sort;
import motocitizen.main.R;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private OnFragmentInteractionListener mListener;

    private ImageButton newMessageButton;
    private EditText    mcNewMessageText;
    private View        newMessageArea;
    private View        mcDetMessagesTable;

    public static DetailMessagesFragment newInstance(int accID, String userName) {
        DetailMessagesFragment fragment = new DetailMessagesFragment();
        Bundle                 args     = new Bundle();
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

        return viewMain;
    }

    private void update() {
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        final motocitizen.accident.Accident accident  = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();
        int                                  lastOwner = 1;
        int                                  nextOwner = 0;

        Integer[] keys = Sort.getSortedMessagesKeys(accident.getMessages());
        for (int i = 0; i < keys.length; i++) {
            if (i < keys.length - 1) {
                nextOwner = accident.getMessages().get(keys[i + 1]).getOwnerId();
            } else nextOwner = 0;
            final Message message = accident.getMessages().get(keys[i]);
            message.setUnread(false);
            View row = Rows.getMessageRow(getActivity(), messageView, message, lastOwner, nextOwner);
            lastOwner = accident.getMessages().get(keys[i]).getOwnerId();
            row.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    PopupWindow popupWindow;
                    popupWindow = (new MessagesPopup(getActivity(), message.getId(), accident.getId())).getPopupWindow();
                    int viewLocation[] = new int[2];
                    v.getLocationOnScreen(viewLocation);
                    popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
                    return true;
                }
            });
            messageView.addView(row);
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

    private void setupAccess() {
        if (Role.isStandart()) {
            newMessageArea.setVisibility(View.VISIBLE);
        } else {
            newMessageArea.setVisibility(View.INVISIBLE);
        }
    }
    private void message(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
    private class SendMessageCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    message(result.getString("error"));
                } catch (JSONException e) {
                    message("Неизвестная ошибка" + result.toString());
                    e.printStackTrace();
                }
            } else {
                //new AccidentsRequest(getActivity(), new UpdateAccidentsCallback());
                Content.update(getActivity(), new UpdateAccidentsCallback());
            }
            newMessageButton.setEnabled(true);
        }
    }

    private class UpdateAccidentsCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            mcNewMessageText.setText("");
            if (!result.has("error")) Content.parseJSON(getActivity(), result);
            ((AccidentDetailsActivity) getActivity()).update();
            update();
        }
    }

}
