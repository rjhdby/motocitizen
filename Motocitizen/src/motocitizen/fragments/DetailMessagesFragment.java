package motocitizen.fragments;

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

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.MyApp;
import motocitizen.accident.Message;
import motocitizen.app.general.popups.MessagesPopup;
import motocitizen.database.StoreMessages;
import motocitizen.draw.Rows;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;
import motocitizen.utils.Sort;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private ImageButton newMessageButton;
    //TODO Вынести листенер в отдельный приватный класс
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
    private EditText mcNewMessageText;
    private View     newMessageArea;
    private View     mcDetMessagesTable;

    public DetailMessagesFragment() {
        // Required empty public constructor
    }

    public static DetailMessagesFragment newInstance(int accID, String userName) {
        DetailMessagesFragment fragment = new DetailMessagesFragment();
        Bundle                 args     = new Bundle();
        args.putInt(ACCIDENT_ID, accID);
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_detail_messages, container, false);

        newMessageButton = (ImageButton) viewMain.findViewById(R.id.new_message_send);
        //TODO Вынести листенер в отдельный приватный класс
        newMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mcNewMessageText.getText().toString();
                new SendMessageRequest(new SendMessageCallback(), accidentID, text);
                newMessageButton.setEnabled(false);
            }
        });

        newMessageButton.setEnabled(false);

        mcNewMessageText = (EditText) viewMain.findViewById(R.id.new_message_text);
        mcNewMessageText.addTextChangedListener(mcNewMessageTextListener);

        mcDetMessagesTable = viewMain.findViewById(R.id.details_messages_table);

        newMessageArea = viewMain.findViewById(R.id.new_message_area);

        update();

        return viewMain;
    }

    private void update() {
        ViewGroup messageView = (ViewGroup) mcDetMessagesTable;
        messageView.removeAllViews();
        final motocitizen.accident.Accident accident  = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();
        int                                 lastOwner = 1;
        int                                 nextOwner;

        Integer[] keys = Sort.getSortedMessagesKeys(accident.getMessages());
        if (keys.length > 0) {
            updateUnreadMessages(accident.getId(), Math.max(keys[0], keys[keys.length - 1]));
        }
        for (int i = 0; i < keys.length; i++) {
            if (i < keys.length - 1) {
                nextOwner = accident.getMessages().get(keys[i + 1]).getOwnerId();
            } else nextOwner = 0;
            final Message message = accident.getMessages().get(keys[i]);
            message.setRead();
            View row = Rows.getMessageRow(messageView, message, lastOwner, nextOwner);
            lastOwner = accident.getMessages().get(keys[i]).getOwnerId();
            //TODO Вынести листенер в отдельный приватный класс
            row.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    PopupWindow popupWindow;
                    popupWindow = (new MessagesPopup(message.getId(), accident.getId())).getPopupWindow();
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

    private void updateUnreadMessages(int accidentId, int messageId) {
        StoreMessages.setLast(accidentId, messageId);
    }

    private void setupAccess() {
        if (MyApp.getRole().isStandart()) {
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
                MyApp.getContent().update(new UpdateAccidentsCallback());
            }
            newMessageButton.setEnabled(true);
        }
    }

    private class UpdateAccidentsCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            mcNewMessageText.setText("");
            if (!result.has("error")) MyApp.getContent().parseJSON(result);
            ((AccidentDetailsActivity) getActivity()).update();
            update();
        }
    }
}
