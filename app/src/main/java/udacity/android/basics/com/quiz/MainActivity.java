package udacity.android.basics.com.quiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    /**
     * OBS: For this app simplification, quiz should have exactly 10 questions.
     * String resources should have the corresponding values for them.
     * <p/>
     * About number of options for question, see the getQuestionsFromResources() method.
     */
    private final int QUIZ_TOTAL_QUESTIONS = 10;

    // Variable to keep all titles of questions
    private String[] questionsTitles = new String[QUIZ_TOTAL_QUESTIONS];
    // Variable to keep all correct answers for all questions
    private String[] questionsAnswers = new String[QUIZ_TOTAL_QUESTIONS];
    // Variable to keep all options of all questions
    private String[][] questionsOptions = new String[QUIZ_TOTAL_QUESTIONS][];
    // Variable to keep all user's answers to all questions
    private String[] userAnswers = new String[QUIZ_TOTAL_QUESTIONS];
    // Variable to keep a random order of questions
    private ArrayList<Integer> questionsOrder = new ArrayList<>();
    // current question user is answering
    private int currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get all questions and its options and answers from string resources
        getQuestionsFromResources();

        // Restore app state in case of screen rotation
        if (savedInstanceState != null) {
            currentQuestion = savedInstanceState.getInt("currentQuestion");
            userAnswers = savedInstanceState.getStringArray("userAnswers");
            questionsOrder = savedInstanceState.getIntegerArrayList("questionsOrder");

            // if quiz is already started, reload the current question, else reload start screen
            if (currentQuestion > -2) {
                // Reload the current question
                loadQuestion(questionsOrder);
            } else {
                loadStartScreen();
            }
        } else {
            // just load the start screen
            loadStartScreen();
        }
    }

    /**
     * Save app state in case of screen rotation.
     * We need to keep track of current question user is answering, all user's answers from previous
     * questions and current order of the questions in the quiz
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("currentQuestion", currentQuestion - 1);
        savedInstanceState.putStringArray("userAnswers", userAnswers);
        savedInstanceState.putIntegerArrayList("questionsOrder", questionsOrder);

        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This method get all questions, options and correct answers and keep them in corresponding
     * arrays to further use
     * <p/>
     * OBS1: For this app simplification, questions with checkboxes or radio buttons can only have 4
     * options for each question.
     * Question with free text can have undetermined correct answers (options), but they should be
     * manually instantiated inside the method.
     * More details about questions structure in strings resource XML
     * <p/>
     * OBS2: More detais about options usage in free text questions type are in checkAnswers method.
     */
    private void getQuestionsFromResources() {
        for (int i = 0; i < questionsTitles.length; i++) {
            // Get all questions' titles from resources using resource name instead resource id
            // Resource name expected for title should be like this: "question1_title"
            questionsTitles[i] = getString(this.getResources().getIdentifier("question" + (i + 1) + "_title", "string", this.getPackageName()));
            // Get all correct answers of questions from resources using resource name instead resource id
            // Resource name expected for correct answer should be like this: "question1_answer"
            questionsAnswers[i] = getString(this.getResources().getIdentifier("question" + (i + 1) + "_answers", "string", this.getPackageName()));

            // if correct answer value is not "all" it means it isn't a free text question, so
            // we can get all 4 questions' options automatically from resource names instead id.
            // Resource name expected for questions'options should be like this: "question1_option1"
            if (!questionsAnswers[i].equals("all")) {
                questionsOptions[i] = new String[4];
                for (int j = 0; j < questionsOptions[i].length; j++) {
                    questionsOptions[i][j] = getString(this.getResources().getIdentifier("question" + (i + 1) + "_option" + (j + 1), "string", this.getPackageName()));
                }
            }
        }

        // Here we can manually get the remaining free text questions' options from resource ids.
        questionsOptions[3] = new String[1];
        questionsOptions[3][0] = getString(R.string.question4_option1);

        questionsOptions[5] = new String[2];
        questionsOptions[5][0] = getString(R.string.question6_option1);
        questionsOptions[5][1] = getString(R.string.question6_option2);
    }

    /**
     * This method set the start layout for activity and clean all variables to start a fresh quiz.
     */
    private void loadStartScreen() {
        // set the start layout
        setContentView(R.layout.activity_main);
        Button startButton = (Button) findViewById(R.id.start_button);

        // clean values from previous quiz
        currentQuestion = -1;
        questionsOrder.clear();

        // When start button is clicked, random sort the order of questions and load the first one.
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < QUIZ_TOTAL_QUESTIONS; i++) {
                    questionsOrder.add(i);
                }
                Collections.shuffle(questionsOrder);

                loadQuestion(questionsOrder);
            }
        });
    }

    /**
     * This method load the next question according the order sorted and set the appropriate layout
     * depending on question type. Also the top progress bar is updated to indicate the quiz progression.
     * <p/>
     * If user have already answered all questions, the finish screen is loaded instead to show
     * the quiz results.
     * <p/>
     * OBS: More details about questions structure in strings resource XML
     *
     * @param questionsOrder is the order of questions for current quiz
     */
    private void loadQuestion(final ArrayList<Integer> questionsOrder) {
        // set the current question
        currentQuestion++;
        if (currentQuestion < QUIZ_TOTAL_QUESTIONS) {
            // check if the current question is a free text type and load the appropriate layout
            if (questionsAnswers[questionsOrder.get(currentQuestion)].equals("all")) {
                setContentView(R.layout.question_text);
                final EditText answerEditText = (EditText) findViewById(R.id.answer_edit_text);

                // When next button is clicked check if the user typed some text, save the answer
                // and load next question.
                Button nextButton = (Button) findViewById(R.id.next_button);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userText = answerEditText.getText().toString();
                        if (userText.trim().isEmpty()) {
                            // show a message to user in case of no text typed.
                            Toast.makeText(MainActivity.this, getString(R.string.error_text), Toast.LENGTH_LONG).show();
                        } else {
                            userAnswers[questionsOrder.get(currentQuestion)] = userText;
                            loadQuestion(questionsOrder);
                        }
                    }
                });
            }
            // check if the current question is a check type (2 or more correct answers) and load
            // the appropriate layout
            else if (questionsAnswers[questionsOrder.get(currentQuestion)].contains(",")) {
                setContentView(R.layout.question_checks);

                // set the current question's options to layout
                final CheckBox[] optionCheck = new CheckBox[4];
                optionCheck[0] = (CheckBox) findViewById(R.id.option1_check);
                optionCheck[0].setText(questionsOptions[questionsOrder.get(currentQuestion)][0]);
                optionCheck[1] = (CheckBox) findViewById(R.id.option2_check);
                optionCheck[1].setText(questionsOptions[questionsOrder.get(currentQuestion)][1]);
                optionCheck[2] = (CheckBox) findViewById(R.id.option3_check);
                optionCheck[2].setText(questionsOptions[questionsOrder.get(currentQuestion)][2]);
                optionCheck[3] = (CheckBox) findViewById(R.id.option4_check);
                optionCheck[3].setText(questionsOptions[questionsOrder.get(currentQuestion)][3]);

                // When next button is clicked check if the user checked at least one option, save
                // the user selection and load next question.
                Button nextButton = (Button) findViewById(R.id.next_button);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // If user select more than one option, all selection will be saved as a
                        // comma-separated string.
                        String userAnswer = "";
                        for (int i = 0; i < optionCheck.length; i++) {
                            if (optionCheck[i].isChecked()) {
                                userAnswer += i + ",";
                            }
                        }

                        if (userAnswer.equals("")) {
                            // show a message to user in case of a non-selection
                            Toast.makeText(MainActivity.this, getString(R.string.error_check), Toast.LENGTH_LONG).show();
                        } else {
                            // save the user selection and cut off the final comma from string
                            userAnswers[questionsOrder.get(currentQuestion)] = userAnswer.substring(0, userAnswer.length() - 1);
                            loadQuestion(questionsOrder);
                        }
                    }
                });
            }
            // Finally, check if the current question is a radio type (just one correct answer) and
            // load the appropriate layout
            else {
                setContentView(R.layout.question_radios);

                // set the current question's options to layout
                final RadioButton[] optionRadio = new RadioButton[4];
                optionRadio[0] = (RadioButton) findViewById(R.id.option1_radio);
                optionRadio[0].setText(questionsOptions[questionsOrder.get(currentQuestion)][0]);
                optionRadio[1] = (RadioButton) findViewById(R.id.option2_radio);
                optionRadio[1].setText(questionsOptions[questionsOrder.get(currentQuestion)][1]);
                optionRadio[2] = (RadioButton) findViewById(R.id.option3_radio);
                optionRadio[2].setText(questionsOptions[questionsOrder.get(currentQuestion)][2]);
                optionRadio[3] = (RadioButton) findViewById(R.id.option4_radio);
                optionRadio[3].setText(questionsOptions[questionsOrder.get(currentQuestion)][3]);

                // When next button is clicked check if the user selected an option, save the user
                // selection and load next question.
                Button nextButton = (Button) findViewById(R.id.next_button);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userAnswer = "";
                        for (int i = 0; i < optionRadio.length; i++) {
                            if (optionRadio[i].isChecked()) {
                                userAnswer += i;
                                break;
                            }
                        }

                        if (userAnswer.equals("")) {
                            // show a message to user in case of a non-selection
                            Toast.makeText(MainActivity.this, getString(R.string.error_radio), Toast.LENGTH_LONG).show();
                        } else {
                            userAnswers[questionsOrder.get(currentQuestion)] = userAnswer;
                            loadQuestion(questionsOrder);
                        }
                    }
                });
            }

            // set the current question title (common for all questions layouts)
            TextView textView = (TextView) findViewById(R.id.question_text_view);
            textView.setText((currentQuestion + 1) + ") " + questionsTitles[questionsOrder.get(currentQuestion)]);
        } else {
            // set finish layout in case user answered all questions
            setContentView(R.layout.finish_quiz);

            // check user answers and display results
            checkAnswers();

            // when restart button is clicked, load the app first screen
            Button restartButton = (Button) findViewById(R.id.restart_button);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadStartScreen();
                }
            });
        }

        // update the top progress bar (common for all screens layouts)
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setProgress(currentQuestion);
    }

    /**
     * This method check all user answers according the correct answer for each question and rate
     * the quiz as following and set a corresponding top image:
     * (0 - 3 hits): bad rate (droid sad image)
     * (4 - 6 hits): medium rate (droid with hat image)
     * (7 - 10 hits): great rate (droid with trophy image)
     * <p/>
     * All user answers are displayed according the initial questions' order sorted and an icon
     * besides each question's title is set to indicate the success (or not) to hit that question
     * <p/>
     * OBS: More details about questions structure in strings resource XML
     */
    private void checkAnswers() {
        // Textviews to display all questions titles and user answers of the quiz
        TextView[] questionsTextViews = new TextView[QUIZ_TOTAL_QUESTIONS];
        TextView[] answersTextViews = new TextView[QUIZ_TOTAL_QUESTIONS];
        // Icons to indicate if user hit or miss each question
        ImageView[] resultsImages = new ImageView[QUIZ_TOTAL_QUESTIONS];

        // Variable to count the user hits
        int correctAnswers = 0;

        for (int i = 0; i < userAnswers.length; i++) {
            // Find all textviews in finish layout by resource names instead ids to display questions' titles.
            questionsTextViews[i] = (TextView) findViewById(this.getResources().getIdentifier("question" + (i + 1) + "_text_view", "id", this.getPackageName()));
            // Find all textviews in finish layout by resource names instead ids to display user's answers.
            answersTextViews[i] = (TextView) findViewById(this.getResources().getIdentifier("answer" + (i + 1) + "_text_view", "id", this.getPackageName()));
            // Find all imageviews in finish layout by resource names instead ids to display hits indicators.
            resultsImages[i] = (ImageView) findViewById(this.getResources().getIdentifier("result" + (i + 1) + "_image", "id", this.getPackageName()));

            // Check if the question to be validated is a free text one and do the proper validation.
            // Basically, it only checks if the user answer contains all correct answers string values.
            // It doesn't check the order which correct answers appears in user answer or any
            // possible connection among them.
            if (questionsAnswers[questionsOrder.get(i)].equals("all")) {
                Boolean textAnswerCorrect = true;
                // if one correct answer string is not found in user answer, we can consider the user
                // answer is wrong and we don't need to check the others correct answers.
                for (int j = 0; j < questionsOptions[questionsOrder.get(i)].length; j++) {
                    if (!userAnswers[questionsOrder.get(i)].contains(questionsOptions[questionsOrder.get(i)][j])) {
                        textAnswerCorrect = false;
                        break;
                    }
                }

                // if user answer is correct, set the icon indicator as green arrow icon and icrease
                // the hits counter. Otherwise, set the icon indicator as red cross icon
                if (textAnswerCorrect) {
                    resultsImages[i].setImageResource(R.drawable.ic_correct);
                    correctAnswers++;
                } else {
                    resultsImages[i].setImageResource(R.drawable.ic_incorrect);
                }

                // set the user answer review as he typed in corresponding question
                answersTextViews[i].setText(userAnswers[questionsOrder.get(i)]);
            }
            // check user answer for check and radio questions type by direct comparison with questions'
            // correct answers strings values.
            else {
                // if user answer is correct, set the icon indicator as green arrow icon and icrease
                // the hits counter. Otherwise, set the icon indicator as red cross icon
                if (userAnswers[questionsOrder.get(i)].equals(questionsAnswers[questionsOrder.get(i)])) {
                    resultsImages[i].setImageResource(R.drawable.ic_correct);
                    correctAnswers++;
                } else {
                    resultsImages[i].setImageResource(R.drawable.ic_incorrect);
                }

                // set the user answer review with the options he selected in corresponding question.
                // If they're multiple options, display them the with a initial "-" and break lines
                if (userAnswers[questionsOrder.get(i)].contains(",")) {
                    String finalAnswer = "";
                    String answers[] = userAnswers[questionsOrder.get(i)].split(",");
                    for (int x = 0; x < answers.length; x++) {
                        finalAnswer += "- " + questionsOptions[questionsOrder.get(i)][Integer.parseInt(answers[x])] + "\n";
                    }
                    finalAnswer = finalAnswer.substring(0, finalAnswer.length() - 1);
                    answersTextViews[i].setText(finalAnswer);

                } else {
                    answersTextViews[i].setText(questionsOptions[questionsOrder.get(i)][Integer.parseInt(userAnswers[questionsOrder.get(i)])]);
                }
            }

            // Find the greeting message textview and grade imageview id's in finish layout
            TextView titleResultTextView = (TextView) findViewById(R.id.title_result_text_view);
            ImageView gradeImage = (ImageView) findViewById(R.id.grade_image);

            // compute the hits rate and display the corresponding image and final greeting
            // 0 and 1 hits have unique greeting messages to be displayed to user
            switch (correctAnswers) {
                case 0:
                    gradeImage.setImageResource(R.drawable.img_bad);
                    titleResultTextView.setText(getString(R.string.hit_no_question) + "\n" + getString(R.string.result_bad));
                    break;
                case 1:
                    gradeImage.setImageResource(R.drawable.img_bad);
                    titleResultTextView.setText(getString(R.string.hit_one_question) + "\n" + getString(R.string.result_bad));
                    break;
                case 2:
                case 3:
                    gradeImage.setImageResource(R.drawable.img_bad);
                    titleResultTextView.setText(getString(R.string.hit_many_questions).replace("##", "" + correctAnswers) + "\n" + getString(R.string.result_bad));
                    break;
                case 4:
                case 5:
                case 6:
                    gradeImage.setImageResource(R.drawable.img_medium);
                    titleResultTextView.setText(getString(R.string.hit_many_questions).replace("##", "" + correctAnswers) + "\n" + getString(R.string.result_medium));
                    break;
                default:
                    gradeImage.setImageResource(R.drawable.img_great);
                    titleResultTextView.setText(getString(R.string.hit_many_questions).replace("##", "" + correctAnswers) + "\n" + getString(R.string.result_great));
                    break;
            }

            // set the question title to user review
            questionsTextViews[i].setText((i + 1) + ") " + questionsTitles[questionsOrder.get(i)]);
        }
    }
}
