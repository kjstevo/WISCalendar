package net.kjmaster.wiscalendarbeta.wis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.kjmaster.wiscalendar.wis.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class WisBrowser extends ActionBarActivity {

    static Context myApp;
    ProgressDialog progressDialog;
    private PlaceholderFragment mFragment;
    private int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wis_browser);
        mFragment = new PlaceholderFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
            myApp = this;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.wis_browser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_parse) {
            updateCalendar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateCalendar() {
//        Thread thread = new Thread(new Runnable() {
//            public void run() {

        CalendarManager calendarManager = new CalendarManager(getContentResolver());

        Document doc = Jsoup.parse(PlaceholderFragment.getHtmlData());

        Elements elements = doc.getElementsByTag("font");

        // Elements elementsLinks = doc.select("a[href]");


        ArrayList<WisLocation> locations = new ArrayList<>();

        //createProgressDialog(elements);
        performUpdate(calendarManager, elements, locations);


    }


    public int getProgress() {
        return mProgress;
    }

//    private void createProgressDialog(Elements elements) {
//        progressDialog = (ProgressDialog) new ProgressDialog(myApp);
//        progressDialog.setTitle(getString(R.string.please_wait));
//        progressDialog.setIndeterminate(false);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMax(elements.size());
//        progressDialog.show();
//    }
    // progressDialog.hide();
    //}

    public void performUpdate(CalendarManager calendarManager, Elements elements, ArrayList<WisLocation> locations) {

        for (int i = 0; i < elements.size(); i++) {
            mProgress = i + 1;
            Object element = elements.get(i);
            if (element.getClass().toString().contains("Element")) {
                Element element1 = (Element) element;

                if (element1.childNodes().size() == 7) {
                    String location = element1.childNodes().get(0).toString();
                    String time = element1.childNode(2).toString();
                    String meetTime = element1.childNode(4).toString();
                    //Element mapElement = elementsLinks.get(mapIndex);
                    // String map = mapElement.toString();
                    locations.add(getWisLocation(location, time, meetTime));
                    Log.d(myApp.getPackageName() + ":Adding location:", element1.toString());

                }
            }

            try {
                progressDialog.setProgress(getProgress());
            } catch (Exception e) {
                Log.e("wis", "Error setting progress dialog.");
            }


        }
        try {
            progressDialog.setMax(locations.size());
        } catch (Exception e) {
            Log.e("wis", "Error setMax on Progress dialog");
        }
        for (int i = 0; i < locations.size() - 1; i++) {
            WisLocation wisLocation = locations.get(i);
            if (wisLocation != null) {
                publishEvent(wisLocation, calendarManager);
                Log.d(myApp.getPackageName(), wisLocation.toString());
            }

            try {
                progressDialog.setProgress(i + 1);
            } catch (Exception e) {
                Log.e("wis", "Error setProgress");
            }

        }
        new AlertDialog.Builder(myApp)
                .setTitle(getString(R.string.finished))
                .setMessage(getString(R.string.calendars_updated))
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();

        finish();


    }

    private void publishEvent(WisLocation wisLocation, CalendarManager calendarManager) {
        calendarManager.InsertEvent(wisLocation, getContentResolver());

    }

    private WisLocation getWisLocation(String location, String time, String meetTime) {
        WisLocation wisLocation = new WisLocation();
        try {
            wisLocation.setAddress(location.substring(location.indexOf(" , ") + 3));
            wisLocation.setName(location.substring(0, location.indexOf(" , ") - 1));
            wisLocation.setTime(time.substring(time.indexOf("Time: ") + 6));
            wisLocation.setMeetLocation(meetTime.substring(meetTime.indexOf("Location: ") + 10));
            wisLocation.setMeetTime(meetTime.substring(12, meetTime.indexOf("Location: ") - 1));

            //wisLocation.setDate(Date.valueOf(wisLocation.getTime()));
        } catch (Exception ex) {
            Log.e(myApp.getPackageName() + ":WISLocationParse", ex.toString());
        }
        return wisLocation;

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static String htmlData;
        private WebView browser;

        public PlaceholderFragment() {
        }

        public static String getHtmlData() {
            return htmlData;
        }

        public static void setHtmlData(String htmlData) {
            PlaceholderFragment.htmlData = htmlData;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_wis_browser, container, false);
            browser = (WebView) rootView.findViewById(R.id.webView);
            try {
                if (browser == null) {
                    assert getActivity().getActionBar() != null;
                    getActivity().getActionBar().setTitle("Error with Webview");
                } else {
             /* JavaScript must be enabled if you want it to work, obviously */
                    browser.getSettings().setJavaScriptEnabled(true);

/* Register a new JavaScript interface called HTMLOUT */
                    browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

/* WebViewClient must be set BEFORE calling loadUrl! */
                    browser.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
        /* This call inject JavaScript into the page which just finished loading. */
                            browser.loadUrl("javascript:window.HTMLOUT.showHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                        }
                    });

/* load a web page */

                    browser.loadUrl("https://wisil.wisintl.com/Login.asp?RedirectURL=%2FDefault%2Easp");
                    new AlertDialog.Builder(myApp)
                            .setTitle(getString(R.string.instructions))
                            .setMessage(getString(R.string.instructions_content))
                            .setPositiveButton(android.R.string.ok, null)
                            .setCancelable(false)
                            .create()
                            .show();


                }
            } catch (Exception ex) {
                Log.e(myApp.getPackageName(), ex.getMessage());

            }
            return rootView;
        }
    }

    /* An instance of this class will be registered as a JavaScript interface */
    static class MyJavaScriptInterface {
        @JavascriptInterface
        public void showHTML(String html) {
            PlaceholderFragment.setHtmlData(html);

        }
    }


}




