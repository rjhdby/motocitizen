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

import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.accident.Accident;
import motocitizen.accident.Message;
import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.Content;
import motocitizen.database.StoreMessages;
import motocitizen.draw.MessageRow;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;
import motocitizen.user.User;
import motocitizen.utils.ToastUtils;
import motocitizen.utils.popups.MessagesPopup;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private ImageButton newMessageButton;


    private EditText  mcNewMessageText;
    private View      newMessageArea;
    private ViewGroup messagesTable;
    private boolean transaction = false;

    public DetailMessagesFragment() {
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
        mcNewMessageText = (EditText) viewMain.findViewById(R.id.new_message_text);
        messagesTable = (ViewGroup) viewMain.findViewById(R.id.details_messages_table);
        newMessageArea = viewMain.findViewById(R.id.new_message_area);

        newMessageButton.setEnabled(false);

        mcNewMessageText.addTextChangedListener(new NewMessageTextWatcher());
        newMessageButton.setOnClickListener(v -> {
            if (transaction) return;
            transaction = true;
            newMessageButton.setEnabled(false);
            new SendMessageRequest(new SendMessageCallback(), accidentID, mcNewMessageText.getText().toString());
        });
        update();

        return viewMain;
    }

    private void update() {
        messagesTable.removeAllViews();
        final Accident accident  = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();
        int            lastOwner = 1;
        int            nextOwner;

        Integer[] keys = accident.getMessages().sortedKeySet();
        if (keys.length > 0) {
            updateUnreadMessages(accident.getId(), Math.max(keys[ 0 ], keys[ keys.length - 1 ]));
        }
        for (int i = 0; i < keys.length; i++) {
            nextOwner = keys.length > i + 1 ? accident.getMessages().get(keys[ i + 1 ]).getOwnerId() : 0;
            final Message message = accident.getMessages().get(keys[ i ]);
            message.setRead();
            View row = new MessageRow(getActivity(), message, lastOwner, nextOwner);
            lastOwner = accident.getMessages().get(keys[ i ]).getOwnerId();
            row.setOnLongClickListener(new MessageRowLongClickListener(message, accident));
            messagesTable.addView(row);
        }
        setupAccess();
    }

    private void updateUnreadMessages(int accidentId, int messageId) {
        StoreMessages.setLast(accidentId, messageId);
    }

    private void setupAccess() {
        newMessageArea.setVisibility(User.getInstance().isStandard() ? View.VISIBLE : View.INVISIBLE);
    }

    private class SendMessageCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            if (result.has("error")) {
                try {
                    ToastUtils.show(getActivity(), result.getString("error"));
                } catch (JSONException e) {
                    ToastUtils.show(getActivity(), "Неизвестная ошибка" + result.toString());
                    e.printStackTrace();
                }
            } else {
                Content.getInstance().requestUpdate(new UpdateAccidentsCallback());
            }
            transaction = false;
            newMessageButton.setEnabled(true);
        }
    }

    private class UpdateAccidentsCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {
            mcNewMessageText.setText("");
            if (!result.has("error")) Content.getInstance().parseJSON(result);
            ((AccidentDetailsActivity) getActivity()).update();
            update();
        }
    }

    private class NewMessageTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (transaction) return;
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
    }

    private class MessageRowLongClickListener implements View.OnLongClickListener {
        private final Message  message;
        private final Accident accident;

        MessageRowLongClickListener(Message message, Accident accident) {
            this.message = message;
            this.accident = accident;
        }

        @Override
        public boolean onLongClick(View v) {
            PopupWindow popupWindow;
            popupWindow = (new MessagesPopup(getActivity(), message.getId(), accident.getId())).getPopupWindow(getActivity());
            int viewLocation[] = new int[ 2 ];
            v.getLocationOnScreen(viewLocation);
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[ 0 ], viewLocation[ 1 ]);
            return true;
        }
    }
}
