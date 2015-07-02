package motocitizen.app.general;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import motocitizen.MyApp;
import motocitizen.network.requests.AccidentsRequest;
import motocitizen.network.requests.AsyncTaskCompleteListener;
import motocitizen.startup.MyPreferences;

//import motocitizen.startup.Startup;

@SuppressLint("UseSparseArrays")
public class Accidents {
    /*
    private static final int NORMAL = R.drawable.accident_row_gradient;
    private static final int HIDE   = R.drawable.accident_row_gradient_hide;
    private static final int ENDED  = R.drawable.accident_row_gradient_ended;
    */
    private static final int NORMAL = 0xff808080;
    private static final int HIDE = 0xff202020;
    private static final int ENDED = 0xff606060;
    private final String readMsgFilename = "readMsg.json";

    public final String error;
    private Map<Integer, Accident> points;
    private final MyPreferences prefs;
    private final Context context;

    public enum Sort {
        FORWARD, BACKWARD
    }

    public Accidents(Context context) {
        error = "ok";
        if (points == null) {
            points = new HashMap<>();
        }
        this.context = context;
        prefs = ((MyApp) context.getApplicationContext()).getPreferences();
    }

    public static String getFileContents(final File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();
        boolean done = false;

        while (!done) {
            final String line = reader.readLine();
            done = (line == null);

            if (line != null) {
                stringBuilder.append(line);
            }
        }

        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }

    public boolean containsKey(int id) {
        return points.containsKey(id);
    }

    public Accident getPoint(int id) {
        return points.get(id);
    }

    public Set<Integer> keySet() {
        return points.keySet();
    }

    public void load() {
        new AccidentsRequest(context, new AccidentsRequestCallback());
    }


    public void update(JSONArray data) {
        try {
            parseJSON(data);
            loadReadenMsg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJSON(JSONArray json) throws JSONException {
        if (((JSONObject) json.get(0)).has("error")) return;
        for (int i = 0; i < json.length(); i++) {
            JSONObject acc = json.getJSONObject(i);
            try {
                Accident current = new Accident(acc, context);
                if (points.containsKey(current.getId())) {
                    current.messages.putAll(points.get(current.getId()).messages);
                }
                addPoint(current);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addPoint(Accident point) {
        points.put(point.getId(), point);
    }

    static int getBackground(String status) {
        if (status.equals("acc_status_end")) {
            return ENDED;
        }
        if (status.equals("acc_status_hide")) {
            return HIDE;
        }
        return NORMAL;
    }

    public String toString(int id) {
        return points.get(id).getTextToCopy();
    }

    private class AccidentsRequestCallback implements AsyncTaskCompleteListener {
        @Override
        public void onTaskComplete(JSONObject result) {

            if (result.has("error")) {
                try {
                    Toast.makeText(context, result.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(context, "Неизвестная ошибка" + result.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                try {
                    parseJSON(result.getJSONArray("list"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Map<Integer, Accident> getVisibleAccidents() {
        Map<Integer, Accident> out = new HashMap<>();
        for (int i : points.keySet()) {
            Accident point = points.get(i);
            if (point.isInvisible()) continue;
            if (point.getHoursAgo() >= prefs.getHoursAgo()) continue;
            out.put(i, point);
        }
        return out;
    }

    public Integer[] sort(Map<Integer, Accident> in, Sort FLAG) {
        List<Integer> list = new ArrayList<>();
        list.addAll(in.keySet());
        Integer[] out = new Integer[list.size()];
        switch (FLAG) {
            case FORWARD:
                list.toArray(out);
                Arrays.sort(out);
                break;
            case BACKWARD:
                list.toArray(out);
                Arrays.sort(out, Collections.reverseOrder());
                break;
            default:
                list.toArray(out);
        }
        return out;
    }

    private boolean loadReadenMsg() {
        File file = new File(context.getFilesDir(), readMsgFilename);
        if (file.exists()) {
            try {
                String content = getFileContents(file);
                if (!content.isEmpty()) {
                    JSONArray accArray = new JSONArray(content);

                    for (int i = 0; i < accArray.length(); i++) {
                        JSONObject acc = accArray.getJSONObject(i);
                        int accId = acc.getInt("accId");

                        JSONArray msgArray = acc.getJSONArray("msg");
                        for (int j = 0; j < msgArray.length(); j++) {
                            int msgId = msgArray.getInt(j);

                            Accident point = points.get(accId);
                            if (point != null) {
                                AccidentMessage msg = point.messages.get(msgId);
                                if (msg != null) {
                                    msg.unread = false;
                                }
                            }
                        }
                    }
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void saveReadenMsg() {
        JSONArray result = new JSONArray();

        for (int i : points.keySet()) {
            Accident point = points.get(i);
            JSONArray msgArray = new JSONArray();

            for (int j : point.messages.keySet()) {
                AccidentMessage message = point.messages.get(j);
                if (!message.unread) {
                    msgArray.put(message.id);
                }
            }
            if (msgArray.length() > 0) {
                JSONObject acc = new JSONObject();
                try {
                    acc.put("accId", point.getId());
                    acc.put("msg", msgArray);
                    result.put(acc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (result.length() > 0) {
            File file = new File(context.getFilesDir(), readMsgFilename);
            FileOutputStream fop = null;

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                String content = result.toString();
                if (content.length() > 0) {
                    fop = new FileOutputStream(file);
                    byte[] contentInBytes = content.getBytes();
                    fop.write(contentInBytes);
                    fop.flush();
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fop != null) {
                        fop.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
