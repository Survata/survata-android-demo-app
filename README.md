# survata-android-demo-app

Demo app for the Survata SDK. Android SDK is located [here](https://github.com/Survata/survata-android-public-sdk).  

# Usage #


### Step 1

Add dependencies in `build.gradle`.
The current version is 1.0.14 (9/20/2016) please check the survata android sdk README [here](https://github.com/Survata/survata-android-public-sdk) for the latest version

```groovy
        dependencies {
            compile 'com.survata.android:Survata:1.0.+'
        }
```
### Step 2

Add permissions in `AndroidManifest.xml`

```
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
        
        // optional, if you want to send zipcode
        <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
        <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```
    

### Step 3

Define Survey

```java
    private Survey mSurvey;
    
    private Button mSurveyButton;
    
    ...
    
```

### Step 4

Check survey availability. The publisherId property is `@NonNull`. 

```java
     public void checkSurvey() {
            Context context = getContext();
            SurveyOption option = new SurveyOption(publisherId);
            mSurvey = new Survey(option);
            mSurvey.create(getActivity(),
                    new Survey.SurveyAvailabilityListener() {
                        @Override
                        public void onSurveyAvailable(Survey.SurveyAvailability surveyAvailability) {
                            if (surveyAvailability == Survey.SurveyAvailability.AVAILABILITY) {
                                mSurveyButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
 ```

#### IMPORTANT NOTE

There is a frequency cap on how many surveys we allow one day for a specific IP address. Thus while testing/developing, it might be frustrating to not see surveys appear after a couple of tries. You can bypass this in two ways. 

####1. FIRST WAY: Using "testing" property

There is a property called **testing** which is a boolean that can be set to true. Below is a snippet of the previous code above that includes the testing property. This will bring up real surveys (that might take very long to answer, so look at the second way), but your responses are not recorded.

```java
    SurveyOption option = new SurveyOption(publisherId);
    option.testing = true;
    mSurvey = new Survey(option);
```

####2. SECOND WAY: Using a default survey with SurveyOption, "preview" property & demo survey preview id 

There is a property called **preview** that allows you to set a default preview Id for a survey (thus, have a specific survey). We have a default short demo survey with just 3 questions at Survata that is perfect for testing that uses the preview id **5fd725139884422e9f1bb28f776c702d**. Here's some code as to show you how to integrate it: 

```java
    SurveyOption option = new SurveyOption(publisherId);
    option.preview = "5fd725139884422e9f1bb28f776c702d";
```

### Step 5  

Show survey in WebView. Should called after checkSurvey();
It will return the survey events (COMPLETED, SKIPPED, CANCELED, CREDIT_EARNED, NETWORK_NOT_AVAILABLE, NO_SURVEY_AVAILABLE). NO_SURVEY_AVAILABLE happens when users are under 13, when they have reached the frequency cap for surveys a day, etc. 

```java
     private void showSurvey() {


        final Activity activity = this;


            mSurvey.createSurveyWall(activity, new Survey.SurveyStatusListener() {
                @Override
                public void onEvent(Survey.SurveyEvents surveyEvents) {
                    Log.d(TAG, "surveyEvents: " + surveyEvents);

                    String info = "";
                    switch (surveyEvents) {

                        case COMPLETED:
                            info = "completed";
                            currentLife += 40;
                            mLifeTextView.setText(currentLife + "%");
                            showFullView();
                            break;
                        case SKIPPED:
                            info = "skipped";
                            break;
                        case CANCELED:
                            info = "canceled";
                            break;
                        case CREDIT_EARNED:
                            info = "credit earned";
                            break;
                        case NO_SURVEY_AVAILABLE:
                            info = "no survey available";
                            break;
                        case NETWORK_NOT_AVAILABLE:
                            info = "network not available";
                            break;
                        default:
                            break;
                    }
                    
                }
            });

    }
```
