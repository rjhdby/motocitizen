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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.accident.Accident;
import motocitizen.content.message.Message;
import motocitizen.database.StoreMessages;
import motocitizen.dictionary.Content;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;
import motocitizen.network2.ApiRequest.RequestResultCallback;
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
        final Accident accident = ((AccidentDetailsActivity) getActivity()).getCurrentPoint();

        Integer preLast = 0;
        Integer last    = 0;
        for (Integer key : accident.getMessages().keySet()) {
            if (last != 0) processMessage(accident, last, preLast, key);
            preLast = last;
            last = key;
        }
        if (accident.getMessages().size() > 0) {
            updateUnreadMessages(accident.getId(), last);
            processMessage(accident, last, preLast, 0);
        }
        setupAccess();
    }

    private void processMessage(Accident accident, Integer current, Integer last, Integer next) {
        Message message = accident.getMessages().get(current);
        View    row     = message.getRow(getActivity(), last, next);
        row.setOnLongClickListener(new MessageRowLongClickListener(message, accident));
        messagesTable.addView(row);
    }

    private void updateUnreadMessages(int accidentId, int messageId) {
        StoreMessages.setLast(accidentId, messageId);
    }

    private void setupAccess() {
        newMessageArea.setVisibility(User.getInstance(getActivity()).isStandard() ? View.VISIBLE : View.INVISIBLE);
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
                Content.getInstance().requestUpdate(new RequestResultCallback() {
                    @Override
                    public void call(@NotNull JSONObject response) {
                        getActivity().runOnUiThread(() -> {
                            mcNewMessageText.setText("");
                            if (!result.has("error")) Content.getInstance().parseJSON(result);
                            ((AccidentDetailsActivity) getActivity()).update();
                            update();
                        });
                    }
                });
            }
            transaction = false;
            newMessageButton.setEnabled(true);
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
            int viewLocation[] = new int[2];
            v.getLocationOnScreen(viewLocation);
            popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
            return true;
        }
    }
}
