package ca.uwaterloo.cs349;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    private SharedViewModel mViewModel;

    Button getMatchingButton;
    AdditionGestureRecognitionView additionGestureRecognitionView;

    List<ImageView> matchingStrokesImageView = new ArrayList<>();
    List<TextView> matchingStrokesNameTextView = new ArrayList<>();
    List<TextView> matchingStrokesScoreTextView = new ArrayList<>();

    int debugCount = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s + " - Recognition");
//            }
//        });

        ViewGroup view_group = (ViewGroup) root.findViewById(R.id.recognition_layout);
        additionGestureRecognitionView = new AdditionGestureRecognitionView(this.getContext());
        view_group.addView(additionGestureRecognitionView);

        matchingStrokesImageView.add((ImageView)root.findViewById(R.id.first_matching_stroke));
        matchingStrokesImageView.add((ImageView)root.findViewById(R.id.second_matching_stroke));
        matchingStrokesImageView.add((ImageView)root.findViewById(R.id.third_matching_stroke));

        matchingStrokesNameTextView.add((TextView)root.findViewById(R.id.first_matching_name));
        matchingStrokesNameTextView.add((TextView)root.findViewById(R.id.second_matching_name));
        matchingStrokesNameTextView.add((TextView)root.findViewById(R.id.third_matching_name));

        matchingStrokesScoreTextView.add((TextView)root.findViewById(R.id.first_matching_score));
        matchingStrokesScoreTextView.add((TextView)root.findViewById(R.id.second_matching_score));
        matchingStrokesScoreTextView.add((TextView)root.findViewById(R.id.third_matching_score));






        getMatchingButton = root.findViewById(R.id.match_button);

        Button debugButton =  root.findViewById(R.id.debug_button);

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additionGestureRecognitionView.curStroke.copyOriginToTransform();
                additionGestureRecognitionView.curStroke.resample(128);
                additionGestureRecognitionView.postInvalidate();

            }
        });



        getMatchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugCount += 1;
//                if (additionGestureRecognitionView.curStroke == null) return;
//                if (debugCount == 1)additionGestureRecognitionView.curStroke.copyOriginToTransform();
//                if (debugCount == 2)additionGestureRecognitionView.curStroke.setCentroid();
//                if (debugCount == 3)additionGestureRecognitionView.curStroke.resample(128);
//                if (debugCount == 4)additionGestureRecognitionView.curStroke.setCentroid();
//                if (debugCount == 5)additionGestureRecognitionView.curStroke.rotate();
//                if (debugCount == 6)additionGestureRecognitionView.curStroke.scale();
                if (additionGestureRecognitionView.curStroke == null) return;
                additionGestureRecognitionView.curStroke.save();


                List<Pair<Double,OneStroke>> list = new ArrayList<>();

                for(OneStroke s :  SharedViewModel.savedStrokes){
                    double score = OneStroke.computeScore(additionGestureRecognitionView.curStroke,s);
                    list.add(new Pair<Double, OneStroke>(score,s));
                }

                Collections.sort(list, new Comparator<Pair<Double, OneStroke>>() {
                    @Override
                    public int compare(Pair<Double, OneStroke> o1, Pair<Double, OneStroke> o2) {
                        if (o1.first == o2.first) return 0;
                        if (o1.first < o2.first) return  -1;
                        return 1;
                    }
                });


                for(int index = 0; index < 3; index ++){
                    if (list.size() == index) break;
                    Double score = list.get(index).first;
                    matchingStrokesImageView.get(index).setImageBitmap(list.get(index).second.bitmap);
                    matchingStrokesNameTextView.get(index).setText(list.get(index).second.strokeName);
                    matchingStrokesScoreTextView.get(index).setText("Score: " + score.intValue());

                }
                additionGestureRecognitionView.postInvalidate();

            }
        });

        return root;

    }



}