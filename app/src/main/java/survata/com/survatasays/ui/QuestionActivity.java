package survata.com.survatasays.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.survata.Survey;
import com.survata.SurveyOption;

import java.util.Random;

import survata.com.survatasays.R;
import survata.com.survatasays.model.Question;
import survata.com.survatasays.model.Questions;
import survata.com.survatasays.util.Settings;

public class QuestionActivity extends Activity{
    private static final String TAG = "QuestionActivity";

    private SeekBar mSeekBar;
    private TextView mQuestionTextView;
    private Button mEnterButton;
    private TextView mPercentageTextView;
    private TextView mLifeTextView;
    private ImageView mHeartImageView;
    private ImageView mPercentageBackground;
    private Survey mSurvey;
    private Button mCreateSurvey;
    private ViewGroup mContainer;
    private ImageButton mSettingsButton;

    private int index;
    private int currentPercentage = 50;
    private String questionText;
    public int currentLife = 100;
    public int qsAnswered = 0;

    private Question mCurrentQuestion;
    private Questions mQuestions = new Questions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Intent intent = getIntent();

        mSeekBar = (SeekBar) findViewById(R.id.percentageSeekBar);
        mQuestionTextView = (TextView) findViewById(R.id.questionTextView);
        mPercentageTextView = (TextView) findViewById(R.id.percentageTextView);
        mLifeTextView = (TextView)findViewById(R.id.lifeTextView);
        mEnterButton = (Button) findViewById(R.id.enterButton);
        mHeartImageView = (ImageView) findViewById(R.id.heartImageView);
        mContainer = (ViewGroup) findViewById(R.id.container);
        mSettingsButton = (ImageButton) findViewById(R.id.settingsButton);

        currentLife = 100;
        mLifeTextView.setText(currentLife + "%");
        qsAnswered = 0;

        mEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuestion();
            }
        });

        mCreateSurvey = (Button)findViewById(R.id.create_survey);
        mCreateSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSurvey();
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });

        mSeekBar.setProgress(50);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentPercentage = progress;
                mPercentageTextView.setText(Integer.toString(currentPercentage));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Random r = new Random();
        index = r.nextInt(43);
        mCurrentQuestion = mQuestions.getQuestion(index);
        questionText = mCurrentQuestion.getName();
        mQuestionTextView.setText(questionText);

        checkSurvey();
    }
    private void loadQuestion() {
        int guess = mSeekBar.getProgress();
        int actualPercentage = mCurrentQuestion.getPercentage();
        int difference = Math.abs(guess - actualPercentage);
        Toast.makeText(getApplicationContext(), "The actual percentage is " + mCurrentQuestion.getPercentage()+"!", Toast.LENGTH_SHORT).show();
        if((currentLife - difference) <= 0){
            mLifeTextView.setText("0%");
            Intent intent = new Intent(this, EndActivity.class);
            intent.putExtra("questionsAnswered", qsAnswered);
            startActivity(intent);
            //Toast.makeText(getApplicationContext(), "You answered " + qsAnswered+" questions!", Toast.LENGTH_SHORT).show();
        } else {
            qsAnswered += 1;
            currentLife = currentLife - difference;
            String lifeString = "" + currentLife + "%";
            mLifeTextView.setText(lifeString);
        }
        Random r = new Random();
        index = r.nextInt(43);
        mCurrentQuestion = mQuestions.getQuestion(index);

        String questionText = mCurrentQuestion.getName();
        mQuestionTextView.setText(questionText);

    }

    public void checkSurvey() {
        // show loading default
        showLoadingSurveyView();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean demoModeBool = prefs.getBoolean("demo_mode_switch", true);
        final Context context = this;
        String publisherId = Settings.getPublisherId(context);
        if(demoModeBool){
            SurveyOption option = new SurveyOption(publisherId);
            option.preview = "5fd725139884422e9f1bb28f776c702d";
            option.testing = true;
            option.contentName = Settings.getContentName(context);
            mSurvey = new Survey(option);
        } else {
            SurveyOption option = new SurveyOption(publisherId);
            option.contentName = Settings.getContentName(context);
            mSurvey = new Survey(option);
        }
//        Survey.setSurvataLogger(mSurvataLogger);
        mSurvey.create(this,
                new Survey.SurveyAvailabilityListener() {
                    @Override
                    public void onSurveyAvailable(Survey.SurveyAvailability surveyAvailability) {
                        Log.d(TAG, "check survey result: " + surveyAvailability);

                        String info = "";
                        switch (surveyAvailability) {

                            case AVAILABILITY:
                                info = "available";
                                showCreateSurveyWallButton();
                                break;
                            case NOT_AVAILABLE:
                                info = "not available";
                                showFullView();
                                break;
                            case ERROR:
                                info = "error";
                                showFullView();
                                break;
                            default:
                                break;
                        }
                        //Toast.makeText(context, "'/create' call result : " + info, Toast.LENGTH_SHORT).show();
                    }
                });
    }
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
                    //Toast.makeText(activity, "'surveyWall': : " + info, Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void showFullView() {

    }


    private void showCreateSurveyWallButton() {
        mCreateSurvey.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void showLoadingSurveyView() {
        mSeekBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSurvey();
        currentLife = 100;
        mLifeTextView.setText(currentLife + "%");
        qsAnswered = 0;
        mSeekBar.setProgress(50);
        mPercentageTextView.setText(Integer.toString(currentPercentage));
    }

}
