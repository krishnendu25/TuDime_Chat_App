package com.TuDime.utils;


/* renamed from: com.tudime.ui.activity.TwilloAsynTask */
/* compiled from: SignUpActivity */
class TwilloAsynTask/* extends AsyncTask*/ {
    /*public static final String ACCOUNT_SID = "AC9148a02938d476a9836cc4d495289ee7";
    public static final String AUTH_TOKEN = "42d256831da66a364cc1051a7abc7bc0";
    private final Context context;
    private String msg;
    private String phoneNo;

    public TwilloAsynTask(Context context2, String phoneNo2, String msg2) {
        this.context = context2;
        this.phoneNo = phoneNo2;
        this.msg = msg2;
    }

    *//* access modifiers changed from: protected *//*
    public Object doInBackground(Object[] objects) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://api.twilio.com/2010-04-01/Accounts/AC9148a02938d476a9836cc4d495289ee7/SMS/Messages");
        httppost.setHeader("Authorization", "Basic " + Base64.encodeToString("AC9148a02938d476a9836cc4d495289ee7:42d256831da66a364cc1051a7abc7bc0".getBytes(), 2));
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair(HttpHeaders.FROM, "+14159420975"));
            nameValuePairs.add(new BasicNameValuePair("To", this.phoneNo));
            nameValuePairs.add(new BasicNameValuePair("Body", this.context.getString(C3211R.string.tudime_one_time_password) + "" + this.msg));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            System.out.println("Entity post is: " + EntityUtils.toString(httpclient.execute(httppost).getEntity()));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return "Executed";
    }

    *//* access modifiers changed from: protected *//*
    public void onPreExecute() {
        super.onPreExecute();
    }

    *//* access modifiers changed from: protected *//*
    public void onPostExecute(Object o) {
        super.onPostExecute(o);
    }*/
}
