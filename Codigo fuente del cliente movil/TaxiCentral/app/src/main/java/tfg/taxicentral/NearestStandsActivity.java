package tfg.taxicentral;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.logging.Logger;

public class NearestStandsActivity extends ListActivity {

    private NearestStandsTask mNearestStandsTask = null;
    private NumTaxisStandTask mNumTaxisStandTask = null;
    String[] stand;
    String numTaxis;
    int nearestStandsTaskFlag = 0, numTaxisStandTaskFlag = 0;
    int notEmptyStandsFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mNearestStandsTask != null) {
            return;
        }
        mNearestStandsTask = new NearestStandsTask(getSharedPreferences("credentials", getApplicationContext().MODE_PRIVATE).getLong("taxiId", 0));
        mNearestStandsTask.execute((Void) null);

        while (nearestStandsTaskFlag == 0) {

        }
        if (notEmptyStandsFlag == 0) {
            Toast.makeText(getApplicationContext(), "No stands", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            int i=0;
            for (i=0; i<stand.length; i++) {
                numTaxisStandTaskFlag = 0;
                int pos = stand[i].indexOf("-");
                mNumTaxisStandTask = new NumTaxisStandTask(new Long(stand[i].substring(0,pos-1)));
                mNumTaxisStandTask.execute((Void) null);
                while (numTaxisStandTaskFlag == 0) {
                }
                if (numTaxis.compareTo("")==0) {
                    numTaxis = "0";
                }
                stand[i]+= " - " + numTaxis + " taxis";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, stand);
            setListAdapter(adapter);
        }
    }

    public class NearestStandsTask extends AsyncTask<Void, Void, Boolean> {

        private final Long mTaxiId;

        NearestStandsTask(Long taxiId) {
            mTaxiId = taxiId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpGet get = new HttpGet(getString(R.string.ip) + "taxis/" + mTaxiId + "/stands");
            get.setHeader("content-type", "application/json");
            try {
                HttpResponse resp = new DefaultHttpClient().execute(get);
                String respStr = EntityUtils.toString(resp.getEntity());
                JSONArray respJSON = new JSONArray(respStr);
                stand = new String[respJSON.length()];
                for (int i = 0; i < respJSON.length(); i++) {
                    JSONObject obj = respJSON.getJSONObject(i);
                    //TODO mirar si lo ideal seria que teniendo un array de ids (manteniendo la misma i que para stand apareciera ya en view taxi stands el numero de taxis en parada
                    stand[i] = obj.getLong("standId") + " - '" + obj.getString("name") + "'";
                    notEmptyStandsFlag = 1;
                }
                nearestStandsTaskFlag = 1;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                return false;
            }
            return true;
        }

    }

    public class NumTaxisStandTask extends AsyncTask<Void, Void, Boolean> {

        private final Long mStandId;

        NumTaxisStandTask(Long standId) {
            mStandId = standId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpGet get = new HttpGet(getString(R.string.ip) + "stands/" + mStandId + "/numtaxis");
            get.setHeader("content-type", "application/json");
            try {
                HttpResponse resp = new DefaultHttpClient().execute(get);
                numTaxis = EntityUtils.toString(resp.getEntity());
                numTaxisStandTaskFlag = 1;
            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
                return false;
            }
            return true;
        }

    }
}
