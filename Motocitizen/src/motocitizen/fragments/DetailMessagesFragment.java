package motocitizen.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;

import motocitizen.Activity.AccidentDetailsActivity;
import motocitizen.accident.Accident;
import motocitizen.accident.Message;
import motocitizen.utils.popups.MessagesPopup;
import motocitizen.user.Auth;
import motocitizen.content.Content;
import motocitizen.database.StoreMessages;
import motocitizen.draw.MessageRow;
import motocitizen.main.R;
import motocitizen.network.AsyncTaskCompleteListener;
import motocitizen.network.requests.SendMessageRequest;

public class DetailMessagesFragment extends AccidentDetailsFragments {

    private ImageButton newMessageButton;
    private ImageButton newPhotoButton;


    private EditText  mcNewMessageText;
    private View      newMessageArea;
    private ViewGroup messagesTable;

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
        newPhotoButton = (ImageButton) viewMain.findViewById(R.id.new_photo);
        mcNewMessageText = (EditText) viewMain.findViewById(R.id.new_message_text);
        messagesTable = (ViewGroup) viewMain.findViewById(R.id.details_messages_table);
        newMessageArea = viewMain.findViewById(R.id.new_message_area);

        newMessageButton.setEnabled(false);

        mcNewMessageText.addTextChangedListener(new NewMessageTextWatcher());
        newMessageButton.setOnClickListener(new SendMessageClickListener());
        newPhotoButton.setOnClickListener(new TakePhotoClickListener());
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
            updateUnreadMessages(accident.getId(), Math.max(keys[0], keys[keys.length - 1]));
        }
        for (int i = 0; i < keys.length; i++) {
            nextOwner = keys.length > i + 1 ? accident.getMessages().get(keys[i + 1]).getOwnerId() : 0;
            final Message message = accident.getMessages().get(keys[i]);
            message.setRead();
            //View row = Rows.getMessageRow(messagesTable, message, lastOwner, nextOwner);
            View row = new MessageRow(getActivity(), message, lastOwner, nextOwner);
            lastOwner = accident.getMessages().get(keys[i]).getOwnerId();
            row.setOnLongClickListener(new MessageRowLongClickListener(message, accident));
            messagesTable.addView(row);
        }
        setupAccess();
    }

    private void updateUnreadMessages(int accidentId, int messageId) {
        StoreMessages.setLast(accidentId, messageId);
    }

    private void setupAccess() {
        newMessageArea.setVisibility(Auth.getInstance().getRole().isStandard() ? View.VISIBLE : View.INVISIBLE);
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
                Content.getInstance().requestUpdate(new UpdateAccidentsCallback());
            }
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

    private class SendMessageClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String text = mcNewMessageText.getText().toString();
            new SendMessageRequest(new SendMessageCallback(), accidentID, text);
            newMessageButton.setEnabled(false);
        }
    }

    private class MessageRowLongClickListener implements View.OnLongClickListener {
        private final Message  message;
        private final Accident accident;

        public MessageRowLongClickListener(Message message, Accident accident) {
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

    private class TakePhotoClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            final String appDirectoryName = "motoDTP";
            final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);
            if(!imageRoot.isDirectory()){
                imageRoot.mkdir();
            }

            String fileName = (int) Math.random()*10000 + ".jpg";
            File newFile = new File(fileName);
            while (newFile.isFile()){
                fileName = (int) Math.random()*10000 + ".jpg";
                newFile = new File(fileName);
            }
            try {
                newFile.createNewFile();
            } catch (IOException e) {}

            Uri outputFileUri = Uri.fromFile(newFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, 0);
        }
    }
}
