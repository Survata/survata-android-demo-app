package survata.com.survatasays.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private int currentPercentage;

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

        loadQuestion();
        checkSurvey();
    }
    private void loadQuestion() {
        Random r = new Random();
        int randomInt = r.nextInt(43);
        mCurrentQuestion = mQuestions.getQuestion(randomInt);

        String questionText = mCurrentQuestion.getName();
        mQuestionTextView.setText(questionText);

    }

    public void checkSurvey() {
        // show loading default
        showLoadingSurveyView();

        final Context context = getApplicationContext();//ask Evan
        String publisherId = Settings.getPublisherId(context);
        SurveyOption option = new SurveyOption(publisherId);
//        option.preview = Settings.getPreviewId(context);
//        option.zipcode = Settings.getZipCode(context);
//        option.sendZipcode = Settings.getZipCodeEnable(context);
        option.contentName = Settings.getContentName(context);

        mSurvey = new Survey(option);
//        Survey.setSurvataLogger(mSurvataLogger);
        mSurvey.create(this, //getActivity();
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
                        Toast.makeText(context, "'/create' call result : " + info, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showSurvey() {
        //blur();

        final Activity activity = this; //getActivity();

        mSurvey.createSurveyWall(activity, new Survey.SurveyStatusListener() {
            @Override
            public void onEvent(Survey.SurveyEvents surveyEvents) {
                Log.d(TAG, "surveyEvents: " + surveyEvents);

                String info = "";
                switch (surveyEvents){

                    case COMPLETED:
                        info = "completed";
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
                    case NETWORK_NOT_AVAILABLE:
                        info = "network not available";
                        break;
                    default:
                        break;
                }
                Toast.makeText(activity, "'surveyWall': : " + info, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFullView() {
        mSeekBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.GONE);
    }

    private void showCreateSurveyWallButton() {
        mSeekBar.setVisibility(View.GONE);
        mCreateSurvey.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.VISIBLE);
    }

    private void showLoadingSurveyView() {
        mSeekBar.setVisibility(View.VISIBLE);
        mCreateSurvey.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

//    public void blur() {
//        Activity activity = getActivity();
//
//        if (activity == null) {
//            Log.d(TAG, "activity is null");
//            return;
//        }
//
//        if (!mBlurred) {
//            ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
//            Blurry.with(activity).sampling(12).onto(viewGroup);
//            mBlurred = true;
//        }
//    }
//
//    public void unBlur() {
//        Activity activity = getActivity();
//
//        if (activity == null) {
//            Log.d(TAG, "activity is null");
//            return;
//        }
//
//        if (mBlurred) {
//            ViewGroup viewGroup = (ViewGroup) getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//            Blurry.delete(viewGroup);
//            mBlurred = false;
//        }
//    }
}
