package motocitizen.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import motocitizen.activity.AccidentDetailsActivity;
import motocitizen.content.NewContent;
import motocitizen.content.accident.Accident;
import motocitizen.content.message.Message;
import motocitizen.database.StoreMessages;
import motocitizen.dictionary.Content;
import motocitizen.main.R;
import motocitizen.network.ApiRequest;
import motocitizen.network.requests.SendMessageRequest;
import motocitizen.user.User;
import motocitizen.utils.ToastUtils;
import motocitizen.utils.popups.MessagesPopup;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private ImageButton newMessageButton;

    private ScrollView activityDetailsMessagesScroll;
    private EditText   mcNewMessageText;
    private View       newMessageArea;
    private ViewGroup  messagesTable;

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

        activityDetailsMessagesScroll = (ScrollView) viewMain.findViewById(R.id.activity__details_messages_scroll);

        //mcNewMessageText.addTextChangedListener(new NewMessageTextWatcher());
        newMessageButton.setOnClickListener(v -> {
            String temp = mcNewMessageText.getText().toString().replaceAll("\\s", "");
            if (temp.length() > 0) {
                new SendMessageRequest(mcNewMessageText.getText().toString(), accidentID, new SendMessageCallback());
                mcNewMessageText.setText("");

            }
        });
        update();

        return viewMain;
    }

    /**
     * Обновление сообщений в списке
     */
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

    private class SendMessageCallback implements ApiRequest.RequestResultCallback {
        @Override
        public void call(@NotNull JSONObject result) {
            if (result.has("error")) {
                getActivity().runOnUiThread(() -> {
                    try {
                        ToastUtils.show(getActivity(), result.getString("error"));
                    } catch (JSONException e) {
                        ToastUtils.show(getActivity(), "Неизвестная ошибка" + result.toString());
                        e.printStackTrace();
                    }
                });
            }
            NewContent.INSTANCE.requestUpdate(response -> getActivity().runOnUiThread(() -> {
//            Content.getInstance().requestUpdate(response -> getActivity().runOnUiThread(() -> {
                if (!response.has("error")) {
                    NewContent.INSTANCE.parseJSON(response, accidentID);
//                    Content.getInstance().parseJSON(response, accidentID);
                }
                ((AccidentDetailsActivity) getActivity()).update();
                update();
                activityDetailsMessagesScroll.post(() -> activityDetailsMessagesScroll.fullScroll(ScrollView.FOCUS_DOWN));
            }));
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
